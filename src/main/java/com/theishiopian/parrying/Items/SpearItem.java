package com.theishiopian.parrying.Items;

import com.google.common.collect.ImmutableMultimap;
import com.theishiopian.parrying.Entity.SpearEntity;
import com.theishiopian.parrying.Registration.ModAttributes;
import net.minecraft.block.BlockState;
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
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class SpearItem extends LazyItem implements IVanishable
{

    private final float reach;

    public SpearItem(IItemTier itemTier, int baseDamage, float baseSpeed, float reach, Properties properties)
    {
        super(itemTier, properties, baseDamage, baseSpeed);
        this.reach = reach;
    }

    public boolean canAttackBlock(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, PlayerEntity player)
    {
        return !player.isCreative();
    }

    public float getDamage()
    {
        return this.attackDamage;
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

                    SpearEntity spearEntity = new SpearEntity(world, player, stack.copy());

                    spearEntity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 3F, 1.0F);

                    if (player.abilities.instabuild)
                    {
                        spearEntity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                    }

                    world.addFreshEntity(spearEntity);

                    world.playSound(null, spearEntity, SoundEvents.TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);

                    if (!player.abilities.instabuild)
                    {
                        stack.shrink(1);
                    }
                }

                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    public @NotNull ActionResult<ItemStack> use(@NotNull World world, PlayerEntity player, @NotNull Hand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        // check if the player meant to actually throw it, check if the player holds an item in the off hand.
        if(hand.equals(Hand.MAIN_HAND) && !player.getOffhandItem().isEmpty() && !player.isShiftKeyDown())
        {
            player.startUsingItem(Hand.OFF_HAND);
            return ActionResult.fail(stack);
        }
        else
        {
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

    @Override
    //may not be needed, but I don't trust forge to register their attribute at the right time
    protected void LazyModifiers()
    {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
        builder.put(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(ModAttributes.RD_UUID,"Tool Modifier", reach,  AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    //item right click use time in ticks, NOT durability
    public int getUseDuration(@NotNull ItemStack stack)
    {
        return 72000;
    }
}
