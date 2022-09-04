package com.theishiopian.parrying.CoreMod.Mixin;

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
        cir.setReturnValue(16);//TODO make configurable
    }
}
