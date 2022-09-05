package com.theishiopian.parrying.CoreMod.Mixin;

import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BrewingStandScreen.class)
public class BrewingStandGUIMixin
{
    @ModifyConstant(method = "renderBg(Lcom/mojang/blaze3d/vertex/PoseStack;FII)V", constant = @Constant(floatValue = 400))
    private float ModifyBarScale(float constant)
    {
        return 120;//todo config
    }

    @ModifyVariable(method = "renderBg(Lcom/mojang/blaze3d/vertex/PoseStack;FII)V", at = @At(value = "STORE"), ordinal = 4)
    private int ModifyFuelLevel(int var)
    {
        return 0;
    }
}
