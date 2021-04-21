package com.theishiopian.parrying;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.TieredItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.apache.logging.log4j.Logger;

public class EventHandler
{
    static Logger log = ParryingMod.LOGGER;
    public static void OnAttackedEvent(LivingAttackEvent event)
    {
        DamageSource source = event.getSource();


        if(event.getEntity() instanceof ServerPlayerEntity && source instanceof EntityDamageSource)
        {
            //ParryingMod.LOGGER.info("parry event called");

            PlayerEntity player = (PlayerEntity) event.getEntity();

            if(player.getMainHandItem().getItem() instanceof TieredItem)
            {
                Entity attacker = source.getEntity();

                Vector3d playerDir = player.getViewVector(1);
                Vector3d attackerDir = attacker.position().subtract(player.position());

                Vector3d attackerDirNorm = attackerDir.normalize();

                double angle = playerDir.dot(attackerDirNorm);

                if(angle > 0.8 && player.swinging)
                {
                    log.info("blocked");
                    player.level.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundCategory.PLAYERS, 1,1);
                    double dirX = player.getX() - attacker.getX();
                    double dirZ = player.getZ() - attacker.getZ();
                    player.knockback(1, -dirX, -dirZ);
                    player.hurtMarked = true;//this makes knockback work
                    event.setCanceled(true);
                }
            }
        }
    }
}