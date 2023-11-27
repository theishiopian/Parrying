package com.theishiopian.parrying.CoreMod.Hooks;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Mechanics.DeltaMechanic;
import com.theishiopian.parrying.Registration.ModEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public abstract class EntityHooks
{
    public static boolean ModifyCollisionPush(Entity entity)
    {
        if(entity instanceof LivingEntity living)
        {
            return living.hasEffect(ModEffects.STABILITY.get());
        }

        return false;
    }

    public static boolean ModifyPush(Entity entity, double pX, double pY, double pZ)
    {
        if(entity instanceof LivingEntity living)
        {
            if(Config.stabilityModifiesPush.get() && living.hasEffect(ModEffects.STABILITY.get())) return true;
            if(Config.instabilityModifiesPush.get() && living.hasEffect(ModEffects.INSTABILITY.get()))
            {
                entity.setDeltaMovement(entity.getDeltaMovement().add(pX * 2, pY * 2, pZ * 2));
                entity.hasImpulse = true;
                return true;
            }
        }

        return false;
    }

    public static void PreTick(Entity entity)
    {
        DeltaMechanic.PreTick(entity);
    }

    public static void PostTick(Entity entity)
    {
        DeltaMechanic.PostTick(entity);
    }
}
