package com.theishiopian.parrying.Mixin;

import com.theishiopian.parrying.Registration.ModTags;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allows "smite" on maces and flails
 */
@Mixin(net.minecraft.enchantment.DamageEnchantment.class)
public class DamageEnchantmentMixin
{
    @Inject(at = @At("HEAD"), method = "canEnchant", cancellable = true)
    private void InjectIntoCanEnchant(ItemStack stackIn, CallbackInfoReturnable<Boolean> cir)
    {
        if(stackIn.getItem().is(ModTags.SMITE_ITEMS))
        {
            cir.setReturnValue(true);
        }
    }
}