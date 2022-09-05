package com.theishiopian.parrying.CoreMod.Mixin;

import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BrewingStandScreen.class)
public class BrewingStandGUIMixin
{
    @ModifyConstant(method = "renderBg", constant = @Constant(floatValue = 400))
    private float ModifyBarScale(float constant)
    {
        return 120;//todo config
    }
}
