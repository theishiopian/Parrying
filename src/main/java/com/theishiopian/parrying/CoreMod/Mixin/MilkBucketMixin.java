package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.item.MilkBucketItem.class)
public class MilkBucketMixin
{
    @Inject(at = @At("HEAD"), method = "finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;", cancellable = true)
    private void InjectIntoFinishedUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, CallbackInfoReturnable<ItemStack> cir)
    {
        if(Config.milkBucketRework.get())
        {
            //TODO add fortified effect
            if (pEntityLiving instanceof ServerPlayer serverplayer)
            {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, pStack);
                serverplayer.awardStat(Stats.ITEM_USED.get(Items.MILK_BUCKET));
            }

            if (pEntityLiving instanceof Player && !((Player)pEntityLiving).getAbilities().instabuild)
            {
                pStack.shrink(1);
            }

            cir.setReturnValue(pStack.isEmpty() ? new ItemStack(Items.BUCKET) : pStack);
        }
    }
}
