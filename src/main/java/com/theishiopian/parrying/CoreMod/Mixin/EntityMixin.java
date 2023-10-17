package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Registration.ModEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//TODO convert to hooks
//TODO may cause lag, unlikely, but should spark it to be sure
@Mixin(Entity.class)
public class EntityMixin
{
    @Inject(method = "push(DDD)V", at = @At("HEAD"), cancellable = true)
    private void InjectIntoPush(double pX, double pY, double pZ, CallbackInfo ci)
    {
        ci.cancel();
        var entity = (Entity)(Object)this;

        if(entity instanceof LivingEntity living)
        {
            if(living.hasEffect(ModEffects.STABILITY.get())) return;
            if(living.hasEffect(ModEffects.INSTABILITY.get()))
            {
                entity.setDeltaMovement(entity.getDeltaMovement().add(pX * 2, pY * 2, pZ * 2));
                entity.hasImpulse = true;
                return;
            }
        }

        entity.setDeltaMovement(entity.getDeltaMovement().add(pX, pY, pZ));
        entity.hasImpulse = true;
    }

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(DDD)V"), cancellable = true)
    private void InjectIntoCollisionPush(Entity entity, CallbackInfo ci)
    {
        if(entity instanceof LivingEntity living)
        {
            if(living.hasEffect(ModEffects.STABILITY.get()))
            {
                ci.cancel();
            }
        }
    }
}
