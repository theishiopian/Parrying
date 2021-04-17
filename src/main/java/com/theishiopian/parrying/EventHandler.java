package com.theishiopian.parrying;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class EventHandler
{
    public static void OnAttackedEvent(LivingAttackEvent event)
    {
        DamageSource source = event.getSource();


        if(event.getEntity() instanceof ServerPlayerEntity && source instanceof EntityDamageSource)
        {
            ParryingMod.LOGGER.info("parry event called");

            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
            Entity attacker = source.getEntity();

            Vector3d playerDir = player.getViewVector(1);
            Vector3d attackerDir = attacker.position().subtract(player.position());

            attackerDir = attackerDir.normalize();

            double angle = playerDir.dot(attackerDir);

            ParryingMod.LOGGER.info(angle);

            if(angle > 0.8)
            {
                event.setCanceled(true);
                ParryingMod.LOGGER.info("blocked");
            }
        }
    }
}
