package com.theishiopian.parrying.Registration;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods
{
    public static FoodProperties GLISTERING_MELON = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.3F).effect(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 0), 1.0F).alwaysEat().build();
    public static FoodProperties GOLDEN_CARROT = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.6F).effect(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 0), 1.0F).alwaysEat().build();
    public static FoodProperties GOLDEN_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F).effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 2), 1.0F).alwaysEat().build();
    public static FoodProperties ENCHANTED_GOLDEN_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F).effect(new MobEffectInstance(MobEffects.ABSORPTION, 3600, 4), 1.0F).alwaysEat().build();
}
