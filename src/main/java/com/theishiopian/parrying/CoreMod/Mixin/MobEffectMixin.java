package com.theishiopian.parrying.CoreMod.Mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.theishiopian.parrying.CoreMod.Hooks.MobEffectHooks;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffect.class)
public class MobEffectMixin
{
    @ModifyConstant(method = "applyEffectTick", constant = @Constant(floatValue = 1.0f, ordinal = 1))
    private float ModifyPoisonLethality(float constant)
    {
        return MobEffectHooks.ModifyPoisonLethality();
    }

    @Inject(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 1), cancellable = true)
    private void ModifyWitherEffect(CallbackInfo ci)
    {
        if(MobEffectHooks.ModifyWither())ci.cancel();
    }

    @Inject(method = "applyEffectTick", at = @At(value = "HEAD"))
    private void ModifyRegenValue(LivingEntity pLivingEntity, int pAmplifier, CallbackInfo ci)
    {
        MobEffectHooks.ModifyRegenValue((MobEffect)(Object)this, pAmplifier, pLivingEntity);
    }

    @ModifyArg(method = "applyInstantenousEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
    private float ModifyInstantDamage(float damage, @Local LivingEntity pLivingEntity)
    {
        var value = MobEffectHooks.ModifyInstantDamage(damage, pLivingEntity);
        return value.orElse(damage);
    }

    @Inject(method = "applyEffectTick", at = @At(value = "HEAD"))
    private void DoInstantEffects(LivingEntity pLivingEntity, int pAmplifier, CallbackInfo ci)
    {
        var effect = (MobEffect)(Object)this;
        MobEffectHooks.DoInstantEffects(effect, pLivingEntity, pAmplifier);
    }

    @Inject(method = "isDurationEffectTick", at = @At(value = "HEAD"), cancellable = true)
    private void BuffRegen(int pDuration, int pAmplifier, CallbackInfoReturnable<Boolean> cir)
    {
        var value = MobEffectHooks.BuffRegen((MobEffect)(Object)this, pDuration, pAmplifier);
        value.ifPresent(cir::setReturnValue);
    }
}
