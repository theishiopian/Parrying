package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.CoreMod.Hooks.LivingEntityHooks;
import com.theishiopian.parrying.Mechanics.DeltaPositionMechanic;
import com.theishiopian.parrying.Registration.ModEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Shadow private float speed;

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

        if(entity.level.isClientSide)return;

        var tracker = DeltaPositionMechanic.velocityTracker.get(entity.getUUID());

        if(tracker != null)
        {
            if(tracker.oldPos != null)
            {
                tracker.oldDeltaPosition = tracker.deltaPosition;
                tracker.deltaPosition = entity.position().subtract(tracker.oldPos).length();
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void InjectIntoTickTail(CallbackInfo ci)
    {
        var entity = (LivingEntity)(Object)this;

        if(entity.level.isClientSide)return;

        var tracker = DeltaPositionMechanic.velocityTracker.get(entity.getUUID());

        if(tracker != null)
        {
            var oldSpeed = tracker.oldDeltaPosition * 20;
            if(entity.hasEffect(ModEffects.INSTABILITY.get()) && entity.horizontalCollision)
            {
                entity.hurt(DamageSource.FLY_INTO_WALL, (float) oldSpeed);
            }

            tracker.oldPos = entity.position();
        }
    }
}
