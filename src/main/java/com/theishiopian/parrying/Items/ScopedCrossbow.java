package com.theishiopian.parrying.Items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.MultiShotEnchantment;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ScopedCrossbow extends CrossbowItem
{
    public ScopedCrossbow(Properties properties)
    {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand)
    {
        ItemStack crossbow = player.getItemInHand(hand);
        if (!isCharged(crossbow) && !player.getProjectile(crossbow).isEmpty())
        {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
            player.startUsingItem(hand);

            return InteractionResultHolder.consume(crossbow);
        }
        else if(ScopedCrossbow.isCharged(crossbow))
        {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(crossbow);
        }
        else
        {
            return InteractionResultHolder.fail(crossbow);
        }
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pCount)
    {
        if(!ScopedCrossbow.isCharged(pStack))super.onUseTick(pLevel, pLivingEntity, pStack, pCount);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack)
    {
        return ScopedCrossbow.isCharged(pStack) ? 72000 : getChargeDuration(pStack) + 3;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack)
    {
        if(ScopedCrossbow.isCharged(pStack))
        {
            return UseAnim.BOW;
        }
        return UseAnim.CROSSBOW;
    }

    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
    @Override
    public void releaseUsing(@NotNull ItemStack crossbow, @NotNull Level world, @NotNull LivingEntity pEntityLiving, int pTimeLeft)
    {
        if(isCharged(crossbow))
        {
            performShooting(world, pEntityLiving, InteractionHand.MAIN_HAND, crossbow, getShootingPower(crossbow) + 3, 0F);
            setCharged(crossbow, false);
        }
        else
        {
            super.releaseUsing(crossbow, world, pEntityLiving, pTimeLeft);
        }
    }

    @Override
    public int getEnchantmentValue()
    {
        return 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return enchantment.category == EnchantmentCategory.CROSSBOW && !(enchantment instanceof MultiShotEnchantment);
    }

    @Override
    public boolean isEnchantable(ItemStack pStack)
    {
        return true;
    }
}