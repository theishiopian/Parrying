package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Registration.ModEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Explosion.class)
public class ExplosionMixin
{
    @Redirect(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void InjectIntoExplosion(Entity entity, net.minecraft.world.phys.Vec3 vec3)
    {
        if(entity instanceof LivingEntity living)
        {
            if(living.hasEffect(ModEffects.STABILITY.get()))
            {
                return;
            }

            if(living.hasEffect(ModEffects.INSTABILITY.get()))
            {
                entity.setDeltaMovement(vec3.multiply(2,2,2));
                return;
            }
        }

        entity.setDeltaMovement(vec3);
    }
}
