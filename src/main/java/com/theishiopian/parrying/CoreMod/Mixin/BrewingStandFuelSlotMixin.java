package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Config.Config;
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
        if(!Config.brewingRequiresFuel.get())cir.setReturnValue(false);
    }
}
