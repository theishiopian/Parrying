package com.theishiopian.parrying.CoreMod.Mixin;

import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MobEffect.class)
public class PoisonMixin
{
    @ModifyConstant(method = "applyEffectTick", constant = @Constant(floatValue = 1.0f, ordinal = 1))
    private float ModifyPoisonLethality(float constant)
    {
        return 0;//TODO config
    }
}
