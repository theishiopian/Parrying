package com.theishiopian.parrying.CoreMod.Mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$FuelSlot")
public class BrewingStandFuelSlotMixin
{
    @Inject(method = "mayPlaceItem", at = @At("HEAD"), cancellable = true)
    private static void InjectIntoMayPlaceItem(ItemStack pItemStack, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(false);//todo config
    }
}
