package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEffect.class)
public class MobEffectMixin
{
    @ModifyConstant(method = "applyEffectTick", constant = @Constant(floatValue = 1.0f, ordinal = 1))
    private float ModifyPoisonLethality(float constant)
    {
        return Config.poisonLethal.get() ? 0 : 1;
    }

    @Inject(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 1), cancellable = true)
    private void InjectIntoApplyEffectTick(CallbackInfo ci)
    {
        if(Config.witherRework.get())ci.cancel();
    }
}
