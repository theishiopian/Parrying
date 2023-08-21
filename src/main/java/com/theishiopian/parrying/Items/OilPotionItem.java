package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Registration.ModSoundEvents;
import com.theishiopian.parrying.Utility.Debug;
import com.theishiopian.parrying.Utility.ModUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class OilPotionItem extends PotionItem
{
    public static final float DURATION_MOD = 0.2f;
    public OilPotionItem(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public @NotNull SoundEvent getDrinkingSound()
    {
        return ModSoundEvents.SMEAR.get();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pHand)
    {
        var oil = pPlayer.getItemInHand(pHand);
        var otherItem = pHand == InteractionHand.MAIN_HAND ? pPlayer.getOffhandItem() : pPlayer.getMainHandItem();

        if(!ModUtil.IsStackWeapon(otherItem)) return InteractionResultHolder.fail(oil);

        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack) {
        return 32;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack)
    {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltip, @NotNull TooltipFlag pFlag)
    {
        PotionUtils.addPotionTooltip(pStack, pTooltip, DURATION_MOD);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving)
    {
        Player player = pEntityLiving instanceof Player ? (Player)pEntityLiving : null;
        if(player == null) return pStack;
        var hand = player.getUsedItemHand();
        var otherItem = hand == InteractionHand.MAIN_HAND ? player.getOffhandItem() : player.getMainHandItem();

        if(!ModUtil.IsStackWeapon(otherItem))
        {
            return pStack;
        }

        if (player instanceof ServerPlayer)
        {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, pStack);
        }

        if (!pLevel.isClientSide)
        {
            var color = PotionUtils.getColor(pStack);
            var tag = otherItem.getTag();
            if(tag == null) tag = new CompoundTag();
            tag.putInt("CustomPotionColor", color);
            otherItem.setTag(tag);

            var potion = PotionUtils.getPotion(pStack);
            PotionUtils.setPotion(otherItem, potion);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, 20);

        if (!player.getAbilities().instabuild)
        {
            Debug.log("Shrinking oil stack");
            pStack.shrink(1);
            Debug.log("Oil stack shrunk");
            player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
        }

        return pStack;
    }
}
