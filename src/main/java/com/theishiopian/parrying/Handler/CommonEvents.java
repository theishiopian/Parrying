package com.theishiopian.parrying.Handler;

import com.theishiopian.parrying.Mechanics.Backstab;
import com.theishiopian.parrying.Mechanics.Deflection;
import com.theishiopian.parrying.Mechanics.Dodging;
import com.theishiopian.parrying.Mechanics.Parrying;
import com.theishiopian.parrying.Registration.ModEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class CommonEvents
{
    public static void OnAttackedEvent(LivingAttackEvent event)
    {
        Parrying.Parry(event);
    }

    public static void ArrowParryEvent(ProjectileImpactEvent.Arrow event)
    {
        Deflection.Deflect(event);
    }

    public static void OnHurtEvent(LivingHurtEvent event)
    {
        if(event.getEntity() instanceof LivingEntity)
        {
            LivingEntity entity = event.getEntityLiving();

            if((!(entity instanceof PlayerEntity)) && entity.hasEffect(ModEffects.STUNNED.get()))
            {
                event.setAmount(event.getAmount() * 1.5f);
            }

            Backstab.DoBackstab(event, entity);
        }
    }

    public static void OnWorldTick(TickEvent.WorldTickEvent event)
    {
        if(event.world.isClientSide)return;

        Dodging.dodgeCooldown.replaceAll((k, v) -> v - 1);
        Dodging.dodgeCooldown.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }
}