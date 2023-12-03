package com.theishiopian.parrying.CoreMod.Mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DiggerItem.class)
public class DiggerItemMixin
{
    @ModifyArg(method = "hurtEnemy", at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V"))
    private int ModifyDamage(int pAmount, @Local ItemStack pStack, @Local LivingEntity pTarget)
    {
        return pStack.getItem() instanceof AxeItem ? 1 : 2;
    }
}
