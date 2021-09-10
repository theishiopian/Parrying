package com.theishiopian.parrying.Items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.theishiopian.parrying.Entity.SpearEntity;
import com.theishiopian.parrying.Registration.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Random;

@SuppressWarnings({"deprecation"})
public class SpearItem extends TieredItem implements IVanishable
{
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    private final String materialID;//this is stupid

    public String getMaterialID()
    {
        return materialID;
    }

    public SpearItem(IItemTier itemTier, int baseDamage, float baseSpeed, Properties properties, String materialID)
    {
        super(itemTier, properties);
        Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", baseDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", baseSpeed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
        this.materialID = materialID;
    }

    public boolean canAttackBlock(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, PlayerEntity player)
    {
        return !player.isCreative();
    }

    public @NotNull UseAction getUseAnimation(@NotNull ItemStack stack)
    {
        return UseAction.SPEAR;
    }

    public void releaseUsing(@NotNull ItemStack stack, @NotNull World world, @NotNull LivingEntity entity, int useTicks)
    {
        if (entity instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity)entity;
            int timer = this.getUseDuration(stack) - useTicks;

            if (timer >= 10)
            {
                if (!world.isClientSide)
                {
                    stack.hurtAndBreak(1, player, (playerEntity) -> playerEntity.broadcastBreakEvent(entity.getUsedItemHand()));

                    SpearEntity spearEntity = new SpearEntity(world, player, stack);

                    spearEntity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 3F, 1.0F);

                    if (player.abilities.instabuild)
                    {
                        spearEntity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                    }

                    world.addFreshEntity(spearEntity);

                    world.playSound(null, spearEntity, SoundEvents.TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);

                    if (!player.abilities.instabuild)
                    {
                        player.inventory.removeItem(stack);
                    }
                }

                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    public @NotNull ActionResult<ItemStack> use(@NotNull World world, PlayerEntity player, @NotNull Hand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getDamageValue() >= stack.getMaxDamage() - 1)
        {
            return ActionResult.fail(stack);
        }
        else
        {
            player.startUsingItem(hand);
            return ActionResult.consume(stack);
        }
    }

    public boolean hurtEnemy(ItemStack stack, @NotNull LivingEntity enemy, @NotNull LivingEntity player)
    {
        stack.hurtAndBreak(1, player, (playerIn) -> playerIn.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
        return true;
    }

    public boolean mineBlock(@NotNull ItemStack stack, @NotNull World world, BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity player)
    {
        if (state.getDestroySpeed(world, pos) != 0.0F)
        {
            stack.hurtAndBreak(2, player, (playerIn) -> playerIn.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
        }

        return true;
    }

    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlotType slotType)
    {
        return slotType == EquipmentSlotType.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slotType);
    }

    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }//item right click use time

    //TODO this shit isn't working. investigate using item overrides for GUI model?
    @OnlyIn(Dist.CLIENT)
    public static void OnModelBake(ModelBakeEvent event)
    {
        //TODO foreach with array
        Map<ResourceLocation, IBakedModel> map = event.getModelRegistry();

        ResourceLocation spear = ModItems.IRON_SPEAR.get().getRegistryName();
        assert spear != null;
        ResourceLocation spearInventory = new ModelResourceLocation(spear, "inventory");
        ResourceLocation spearHand = new ModelResourceLocation(spear + "_handheld", "inventory");

        IBakedModel spearModelInventory = map.get(spearInventory);
        IBakedModel spearModelHand = map.get(spearHand);

        IBakedModel spearModelWrapper = new IBakedModel()
        {
            @Override
            public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, @NotNull Random random)
            {
                return spearModelInventory.getQuads(state, direction, random);
            }

            @Override
            public boolean useAmbientOcclusion()
            {
                return spearModelInventory.useAmbientOcclusion();
            }

            @Override
            public boolean isGui3d()
            {
                return spearModelInventory.isGui3d();
            }

            @Override
            public boolean usesBlockLight()
            {
                return spearModelInventory.usesBlockLight();
            }

            @Override
            public boolean isCustomRenderer()
            {
                return spearModelInventory.isCustomRenderer();
            }

            @Override
            public @NotNull TextureAtlasSprite getParticleIcon()
            {
                return spearModelInventory.getParticleIcon();
            }

            @Override
            public @NotNull ItemOverrideList getOverrides()
            {
                return spearModelInventory.getOverrides();
            }

            @Override
            public IBakedModel handlePerspective(ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack)
            {
                return ForgeHooksClient.handlePerspective(transformType == ItemCameraTransforms.TransformType.GUI ? spearModelInventory : spearModelHand, transformType, matrixStack);
            }
        };

        map.put(spearInventory, spearModelWrapper);
    }
}
