package com.theishiopian.parrying.Mixin;

import com.theishiopian.parrying.Items.AdvancedBundle;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin exists to let me redirect the add method in bundles, as without copying a large portion of mojang code practically verbatim,
 * I cannot use my updated add method.
 */
@Deprecated
@Mixin(net.minecraft.world.item.BundleItem.class)
public class BundleItemMixin
{
    @Inject(at = @At("HEAD"), method = "add", cancellable = true)
    private static void add(ItemStack bundle, ItemStack toInsert, CallbackInfoReturnable<Integer> cir)
    {
        if(bundle.getItem() instanceof AdvancedBundle)
        {
            cir.setReturnValue(AdvancedBundle.add(bundle, toInsert));
        }
    }
}
