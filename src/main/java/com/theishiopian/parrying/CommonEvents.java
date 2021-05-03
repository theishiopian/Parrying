package com.theishiopian.parrying;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class CommonEvents
{
    static Logger log = ParryingMod.LOGGER;

    public static void OnAttackedEvent(LivingAttackEvent event)
    {
        DamageSource source = event.getSource();

        if(event.getEntity() instanceof ServerPlayerEntity && source instanceof EntityDamageSource)
        {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            Item held = player.getMainHandItem().getItem();

            if(player.getMainHandItem().getAttributeModifiers(EquipmentSlotType.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE))
            {
                Entity attacker = source.getEntity();

                Vector3d playerDir = player.getViewVector(1);
                Vector3d attackerDir = attacker.position().subtract(player.position());

                Vector3d attackerDirNorm = attackerDir.normalize();

                double angle = playerDir.dot(attackerDirNorm);

                if(angle > 0.95 && player.swinging)
                {
                    //log.info("blocked");
                    Random random = new Random();
                    player.level.playSound(null, player.blockPosition(), ModSoundEvents.BLOCK_HIT.get(), SoundCategory.PLAYERS, 1, random.nextFloat() * 2f);
                    //attacker.playSound(ParryingMod.BLOCK_HIT_EVENT, 1, random.nextFloat() * 2f);
                    player.knockback(0.33f, attackerDir.x, attackerDir.z);
                    player.hurtMarked = true;//this makes knockback work
                    float damage = event.getAmount();
                    player.causeFoodExhaustion(0.5f);
                    held.damageItem(player.getMainHandItem(), (int) damage, null, null);

                    double pX = (attacker.getX() + player.getX()) / 2 + (random.nextDouble()-0.5) * 0.2 + (attackerDirNorm.x * 0.2);
                    double pY = ((attacker.getY() + player.getY()) / 2) + 1.45 + (random.nextDouble()-0.5) * 0.2+ (attackerDirNorm.y * 0.2);
                    double pZ = (attacker.getZ() + player.getZ()) / 2 + (random.nextDouble()-0.5) * 0.2+ (attackerDirNorm.z * 0.2);

                    ((ServerWorld) player.level).sendParticles(ParryingMod.PARRY_PARTICLE.get(), pX, pY, pZ, 1, 0D, 0D, 0D, 0.0D);
                    event.setCanceled(true);
                }
            }
        }
    }
}