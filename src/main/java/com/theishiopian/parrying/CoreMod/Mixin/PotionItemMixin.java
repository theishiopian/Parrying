package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.item.PotionItem.class)
public class PotionItemMixin
{
    @Inject(method = "getUseDuration(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private void ModifyUseDuration(ItemStack pStack, CallbackInfoReturnable<Integer> cir)
    {
        cir.setReturnValue(Config.sipTicks.get());
    }

    @Inject(method = "isFoil(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void ModifyFoil(ItemStack pStack, CallbackInfoReturnable<Boolean> cir)
    {
        //todo remove in 1.20
        if(Config.isPotionShimmerDisabled.get())cir.setReturnValue(false);
    }
}
