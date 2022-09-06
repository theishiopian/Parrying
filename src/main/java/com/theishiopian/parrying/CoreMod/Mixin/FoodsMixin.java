package com.theishiopian.parrying.CoreMod.Mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Foods.class)
public class FoodsMixin
{
    @Inject(method = "<clinit>()V",at = @At("RETURN"))
    private static void InjectIntoCInit(CallbackInfo ci)
    {
        Foods.GOLDEN_CARROT = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.6F).effect(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 0), 1.0F).alwaysEat().build();
        Foods.GOLDEN_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F).effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 2), 1.0F).alwaysEat().build();
        Foods.ENCHANTED_GOLDEN_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F).effect(new MobEffectInstance(MobEffects.ABSORPTION, 3600, 4), 1.0F).alwaysEat().build();
    }
}
