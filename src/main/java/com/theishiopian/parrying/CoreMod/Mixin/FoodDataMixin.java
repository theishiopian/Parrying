package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FoodData.class)
public class FoodDataMixin
{
    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.0f, ordinal = 2))
    private float ModifySatHeal(float constant)//disables sat healing
    {
        return Config.noSatHeal.get() ? Integer.MAX_VALUE : 0;
    }
}
