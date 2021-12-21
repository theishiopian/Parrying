package com.theishiopian.parrying.Items;

import com.google.common.collect.ImmutableMultimap;
import com.theishiopian.parrying.Entity.SpearEntity;
import com.theishiopian.parrying.Registration.ModAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SpearItem extends LazyItem implements Vanishable
{

    private final float reach;

    public SpearItem(Tiers itemTier, int baseDamage, float baseSpeed, float reach, Properties properties)
    {
        super(itemTier, properties, baseDamage, baseSpeed);
        this.reach = reach;
    }

    public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player)
    {
        return !player.isCreative();
    }

    public float getDamage()
    {
        return this.attackDamage;
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack)
    {
        return UseAnim.SPEAR;
    }

    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level world, @NotNull LivingEntity entity, int useTicks)
    {
        if (entity instanceof Player player)
        {
            int timer = this.getUseDuration(stack) - useTicks;

            if (timer >= 10)
            {
                if (!world.isClientSide)
                {
                    stack.hurtAndBreak(1, player, (playerEntity) -> playerEntity.broadcastBreakEvent(entity.getUsedItemHand()));

                    SpearEntity spearEntity = new SpearEntity(world, player, stack.copy());

                    spearEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3F, 1.0F);

                    if (player.getAbilities().instabuild)
                    {
                        spearEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }

                    world.addFreshEntity(spearEntity);

                    world.playSound(null, spearEntity, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);

                    if (!player.getAbilities().instabuild)
                    {
                        stack.shrink(1);
                    }
                }

                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        //thanks k1r0s
        if(player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ShieldItem && !player.isCrouching())
        {
            player.startUsingItem(InteractionHand.OFF_HAND);
            return InteractionResultHolder.fail(stack);
        }

        if (stack.getDamageValue() >= stack.getMaxDamage() - 1)
        {
            return InteractionResultHolder.fail(stack);
        }
        else
        {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
    }

    public boolean hurtEnemy(ItemStack stack, @NotNull LivingEntity enemy, @NotNull LivingEntity player)
    {
        stack.hurtAndBreak(1, player, (playerIn) -> playerIn.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level world, BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity player)
    {
        if (state.getDestroySpeed(world, pos) != 0.0F)
        {
            stack.hurtAndBreak(2, player, (playerIn) -> playerIn.broadcastBreakEvent(EquipmentSlot.MAINHAND));
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
