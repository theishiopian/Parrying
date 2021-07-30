package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.ParryingMod;
import com.theishiopian.parrying.Registration.ModEffects;
import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModParticles;
import com.theishiopian.parrying.Registration.ModSoundEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.Random;

public abstract class Parrying
{
    public static void Parry(LivingAttackEvent event)
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

                    assert attacker != null : "How the hell did this throw null???";
                    Vector3d attackerDir = attacker.position().subtract(player.position());

                    Vector3d attackerDirNorm = attackerDir.normalize();

                    double attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED).getValue();

                    double angle = new Vector3d(playerDir.x, 0, playerDir.z).dot(new Vector3d(attackerDirNorm.x, 0, attackerDirNorm.z));
                    double surfaceAngle = MathHelper.clamp(Config.parryAngle.get() - (attackSpeed - 1.6) * 0.05, 0, 1);
                    ParryingMod.LOGGER.info(surfaceAngle);
                    Random random = new Random();
                    //default 0.95
                    if(angle > surfaceAngle && player.swinging)
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
                                    playerEntity.broadcastBreakEvent(player.getUsedItemHand()));

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
}
