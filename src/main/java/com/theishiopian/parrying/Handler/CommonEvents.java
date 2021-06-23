package com.theishiopian.parrying.Handler;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModEffects;
import com.theishiopian.parrying.Registration.ModParticles;
import com.theishiopian.parrying.Registration.ModSoundEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Random;

public class CommonEvents
{
    public static void OnAttackedEvent(LivingAttackEvent event)
    {
        DamageSource source = event.getSource();

        if(Config.parryEnabled.get() && event.getEntity() instanceof ServerPlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            ItemStack held = player.getMainHandItem();
            Vector3d playerDir = player.getViewVector(1);
            int ripLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.RIPOSTE.get(), held);
            int fragLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FRAGILE.get(), held);
            int phaseLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.PHASING.get(), held);

            if(source instanceof EntityDamageSource && !(source instanceof IndirectEntityDamageSource))
            {
                if(player.getMainHandItem().getAttributeModifiers(EquipmentSlotType.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE) && !player.hasEffect(ModEffects.STUNNED.get()))
                {
                    Entity attacker = source.getEntity();

                    Vector3d attackerDir = attacker.position().subtract(player.position());

                    Vector3d attackerDirNorm = attackerDir.normalize();

                    double angle = new Vector3d(playerDir.x, 0, playerDir.z).dot(new Vector3d(attackerDirNorm.x, 0, attackerDirNorm.z));
                    Random random = new Random();
                    //default 0.95
                    if(angle > Config.parryAngle.get() && player.swinging)
                    {
                        if(phaseLevel == 0 || random.nextInt(3) != 0)
                        {
                            player.knockback(0.33f, attackerDir.x, attackerDir.z);
                            player.hurtMarked = true;//this makes knockback work
                            player.causeFoodExhaustion(0.5f);

                            if(ripLevel > 0)
                            {
                                EffectInstance instance = new EffectInstance(Effects.DAMAGE_BOOST, 60);
                                player.addEffect(instance);
                            }

                            held.hurtAndBreak(fragLevel > 0 ? 3 : 1, player, (playerEntity) ->
                            {
                                playerEntity.broadcastBreakEvent(player.getUsedItemHand());
                            });

                            double pX = (attacker.getX() + player.getX()) / 2 + (random.nextDouble()-0.5) * 0.2 + (attackerDirNorm.x * 0.2);
                            double pY = ((attacker.getY() + player.getY()) / 2) + 1.45 + (random.nextDouble()-0.5) * 0.2+ (attackerDirNorm.y * 0.2);
                            double pZ = (attacker.getZ() + player.getZ()) / 2 + (random.nextDouble()-0.5) * 0.2+ (attackerDirNorm.z * 0.2);
                            player.level.playSound(null, player.blockPosition(), ModSoundEvents.BLOCK_HIT.get(), SoundCategory.PLAYERS, 1, random.nextFloat() * 2f);
                            ((ServerWorld) player.level).sendParticles(ModParticles.PARRY_PARTICLE.get(), pX, pY, pZ, 1, 0D, 0D, 0D, 0.0D);
                            event.setCanceled(true);
                        }
                        else
                        {
                            player.level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1, random.nextFloat() * 2f);
                        }
                    }
                }
            }
        }
    }

    public static void ArrowParryEvent(ProjectileImpactEvent.Arrow event)
    {
        final AbstractArrowEntity projectile = event.getArrow();

        if(!projectile.level.isClientSide)
        {
            if (!(event.getRayTraceResult() instanceof EntityRayTraceResult))return;
            Entity entity = ((EntityRayTraceResult)event.getRayTraceResult()).getEntity();
            if(event.getEntity() != null && entity instanceof PlayerEntity)
            {
                PlayerEntity player = (PlayerEntity) entity;
                Vector3d playerDir = player.getViewVector(1);
                Vector3d arrowDir = projectile.position().subtract(player.position());
                Vector3d playerDirLevel = new Vector3d(playerDir.x, 0, playerDir.z);
                Vector3d arrowDirLevel = new Vector3d(arrowDir.x, 0, arrowDir.z);
                double angle = playerDirLevel.dot(arrowDirLevel);
                ItemStack held = player.getMainHandItem();

                int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DEFLECTING.get(), held);
                if(level > 0 && player.swinging && angle > 0.5)
                {
                    Random random = new Random();

                    player.causeFoodExhaustion(1f);
                    held.hurtAndBreak(1, player, (playerEntity) ->
                    {
                        playerEntity.broadcastBreakEvent(player.getUsedItemHand());
                    });

                    float power = (float)(projectile.getDeltaMovement().normalize().length() / 5f) * level;
                    projectile.setDeltaMovement(playerDir.x * power, playerDir.y * power, playerDir.z * power);
                    projectile.yRot = (float)(MathHelper.atan2(playerDir.x, playerDir.z) * (double)(180F / (float)Math.PI));
                    projectile.xRot = (float)(MathHelper.atan2(playerDir.y, (double)1) * (double)(180F / (float)Math.PI));
                    projectile.yRotO = projectile.yRot;
                    projectile.xRotO = projectile.xRot;
                    projectile.hasImpulse = true;

                    player.level.playSound(null, player.blockPosition(), ModSoundEvents.BLOCK_HIT.get(), SoundCategory.PLAYERS, 1, random.nextFloat() * 2f);

                    Vector3d particlePos = projectile.position();
                    ((ServerWorld) player.level).sendParticles(ModParticles.PARRY_PARTICLE.get(), particlePos.x, particlePos.y, particlePos.z, 1, 0D, 0D, 0D, 0.0D);

                    event.setCanceled(true);
                }
            }
        }
    }

    public static void OnHurtEvent(LivingHurtEvent event)
    {
        if(event.getEntity() instanceof LivingEntity)
        {
            Random random = new Random();
            LivingEntity entity = event.getEntityLiving();

            if((!(entity instanceof PlayerEntity)) && entity.hasEffect(ModEffects.STUNNED.get()))
            {
                event.setAmount(event.getAmount() * 1.5f);
            }

            if(Config.backStabEnabled.get())
            {
                Entity attacker = event.getSource().getEntity();

                if(attacker instanceof  LivingEntity && entity.getMaxHealth() <= Config.backStabMaxHealth.get())
                {
                    Vector3d attackerDir = attacker.getViewVector(1);
                    Vector3d defenderDir = entity.getViewVector(1);

                    double angle = (new Vector3d(attackerDir.x, 0, attackerDir.z)).dot(new Vector3d(defenderDir.x, 0, defenderDir.z));

                    if(angle > Config.backStabAngle.get())
                    {
                        event.setAmount((float) (event.getAmount() * Config.backStabDamageMultiplier.get()));

                        Vector3d pos = entity.position();

                        ((ServerWorld) attacker.level).sendParticles(ModParticles.STAB_PARTICLE.get(), pos.x, pos.y+1.5f, pos.z, 1, 0D, 0D, 0D, 0.0D);
                        attacker.level.playSound(null, attacker.blockPosition(), SoundEvents.PLAYER_BIG_FALL, SoundCategory.PLAYERS, 2, 1);
                    }
                }
            }
        }
    }
}