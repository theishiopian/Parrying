package com.theishiopian.parrying.CoreMod.Mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorMaterials.class)
public class ArmorMaterialsMixin
{
    private static final int[] protections = new int[]{2, 4, 5, 2};
    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    private static final int goldDurabilityMultiplier = 12;

    @Inject(method = "getDefenseForSlot", at = @At("HEAD"), cancellable = true)
    private void InjectIntoGetDefenseForSlot(EquipmentSlot pSlot, CallbackInfoReturnable<Integer> cir)
    {
        if(ArmorMaterials.class.cast(this) == ArmorMaterials.GOLD)cir.setReturnValue(protections[pSlot.getIndex()]);
    }

    @Inject(method = "getDurabilityForSlot", at = @At("HEAD"), cancellable = true)
    private void InjectIntoGetDurabilityForSlot(EquipmentSlot pSlot, CallbackInfoReturnable<Integer> cir)
    {
        if(ArmorMaterials.class.cast(this) == ArmorMaterials.GOLD)cir.setReturnValue(goldDurabilityMultiplier * HEALTH_PER_SLOT[pSlot.getIndex()]);
    }

    @Inject(method = "getKnockbackResistance", at = @At("HEAD"), cancellable = true)
    private void InjectIntoGetKnockbackResistance(CallbackInfoReturnable<Float> cir)
    {
        if(ArmorMaterials.class.cast(this) == ArmorMaterials.GOLD)cir.setReturnValue(0.1f);
    }
}
