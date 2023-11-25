package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.CoreMod.Hooks.LivingEntityHooks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void InjectIntoDeathProtectionCheck(DamageSource pDamageSource, CallbackInfoReturnable<Boolean> cir)
    {
        Optional<Boolean> value = LivingEntityHooks.ModifyDeathProtectionCheck(pDamageSource, ((LivingEntity)(Object)this));
        value.ifPresent(cir::setReturnValue);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void InjectIntoTickHead(CallbackInfo ci)
    {
        var entity = (LivingEntity)(Object)this;
        LivingEntityHooks.PreTick(entity);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void InjectIntoTickTail(CallbackInfo ci)
    {
        var entity = (LivingEntity)(Object)this;
        LivingEntityHooks.PostTick(entity);
    }
}
