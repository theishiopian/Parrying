package com.theishiopian.parrying;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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
            PlayerEntity player = (PlayerEntity) event.getEntity();
            Item held = player.getMainHandItem().getItem();
            if(held instanceof TieredItem || held == Items.TRIDENT)
            {
                Entity attacker = source.getEntity();

                Vector3d playerDir = player.getViewVector(1);
                Vector3d attackerDir = attacker.position().subtract(player.position());

                Vector3d attackerDirNorm = attackerDir.normalize();

                double angle = playerDir.dot(attackerDirNorm);

                if(angle > 0.95 && player.swinging)
                {
                    //log.info("blocked");
                    player.level.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundCategory.PLAYERS, 1,1);
                    player.knockback(0.33f, attackerDir.x, attackerDir.z);
                    player.hurtMarked = true;//this makes knockback work
                    float damage = event.getAmount();
                    player.causeFoodExhaustion(damage);
                    held.damageItem(player.getMainHandItem(), (int)damage, null, null);
                    event.setCanceled(true);
                }
            }
        }
    }
}