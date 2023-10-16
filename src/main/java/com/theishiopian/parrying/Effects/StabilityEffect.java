package com.theishiopian.parrying.Effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class StabilityEffect extends MobEffect
{
    public StabilityEffect()
    {
        super(MobEffectCategory.BENEFICIAL, 12592004);
        addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "7716a2c5-2425-4f57-8f24-09e35e56c52c", 1, AttributeModifier.Operation.MULTIPLY_BASE);
    }
}