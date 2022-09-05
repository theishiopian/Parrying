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

    //todo disable line 44 to disable fuel bar

    //todo in menu class (separate mixin for BrewingStandMenu) set co-ords on line 44 to something very high, to move the fuel slot off screen
    //todo in menu class redirect method on line 147 to return false
    //todo make gui retexture
}
