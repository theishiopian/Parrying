package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffect.class)
public class MobEffectMixin
{
    @ModifyConstant(method = "applyEffectTick", constant = @Constant(floatValue = 1.0f, ordinal = 1))
    private float ModifyPoisonLethality(float constant)
    {
        return Config.poisonLethal.get() ? 0 : 1;
    }

    @Inject(method = "applyEffectTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 1), cancellable = true)
    private void ModifyWitherEffect(CallbackInfo ci)
    {
        if(Config.witherRework.get())ci.cancel();
    }

    @Inject(method = "applyEffectTick", at = @At(value = "HEAD"))
    private void ModifyRegenValue(LivingEntity pLivingEntity, int pAmplifier, CallbackInfo ci)
    {
        var effect = (MobEffect)(Object)this;

        if(effect == MobEffects.REGENERATION)
        {
            pLivingEntity.heal(pAmplifier + 1);
        }
    }

    @Inject(method = "applyEffectTick", at = @At(value = "HEAD"))
    private void DoSustenance(LivingEntity pLivingEntity, int pAmplifier, CallbackInfo ci)
    {
        var effect = (MobEffect)(Object)this;

        if(effect == ModEffects.SUSTENANCE.get() && pLivingEntity instanceof Player player)
        {
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20);
        }
    }

    @Inject(method = "isDurationEffectTick", at = @At(value = "HEAD"), cancellable = true)
    private void BuffRegen(int pDuration, int pAmplifier, CallbackInfoReturnable<Boolean> cir)
    {
        var effect = (MobEffect)(Object)this;

        if (effect == MobEffects.REGENERATION)
        {
            cir.setReturnValue(pDuration % 20 == 0);
        }
    }
}
