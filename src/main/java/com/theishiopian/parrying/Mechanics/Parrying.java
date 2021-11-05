package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.*;
import com.theishiopian.parrying.Utility.ParryModUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.Objects;

/**
 * This class is a container for the parrying mechanic. This mechanic is triggered from CommonEvents, within OnAttackedEvent.
 */
public abstract class Parrying
{
    public static void Parry(LivingAttackEvent event)
    {
        if(Config.parryEnabled.get() && event.getEntity() instanceof ServerPlayerEntity)
        {
            DamageSource source = event.getSource();//the properties of the damage

            if(source instanceof EntityDamageSource && !(source instanceof IndirectEntityDamageSource))
            {
                PlayerEntity player = (PlayerEntity) event.getEntity();//the defender

                //make sure the player isn't stunned
                if(!player.hasEffect(ModEffects.STUNNED.get()))
                {
                    ItemStack held = player.getMainHandItem();//the item in use

                    //is the player holding a weapon?
                    if(ParryModUtil.IsWeapon(held))
                    {
                        Entity attacker = source.getEntity();//the attacking entity
                        Vector3d playerLookDir = player.getViewVector(1);//the direction the player is looking

                        //enchantment levels
                        int ripLevel = Config.riposteEnchantEnabled.get() ? EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.RIPOSTE.get(), held) : 0;
                        int fragLevel = Config.fragileCurseEnabled.get() ? EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FRAGILE.get(), held) : 0;
                        int phaseLevel = Config.phasingCurseEnabled.get() ? EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.PHASING.get(), held) : 0;

                        assert attacker != null : "How the hell did this throw null???";
                        Vector3d attackerDir = attacker.position().subtract(player.position());
                        Vector3d attackerDirNorm = attackerDir.normalize();

                        double attackSpeed = Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_SPEED)).getValue();//the speed of the weapon in use

                        //the angle from player look direction to the direction from the player to the enemy
                        double angle = new Vector3d(playerLookDir.x, 0, playerLookDir.z).dot(new Vector3d(attackerDirNorm.x, 0, attackerDirNorm.z));

                        //the minimum angle for a successful parry, determined by the attack speed. based around the speed of a sword (1.6)
                        double surfaceAngle = MathHelper.clamp(Config.parryAngle.get() - (attackSpeed - 1.6) * 0.05, 0, 1);

                        //default 0.95
                        if(angle >= surfaceAngle && player.swinging)
                        {
                            //phasing check
                            if(phaseLevel == 0 || ParryModUtil.random.nextInt(3) != 0)
                            {
                                //successful parry
                                player.awardStat(ModStats.parry);

                                player.knockback(0.33f, attackerDir.x, attackerDir.z);
                                player.hurtMarked = true;//this makes knockback work
                                player.causeFoodExhaustion(0.5f);//exhaust player

                                //add strength for riposte
                                if(Config.riposteEnchantEnabled.get() && ripLevel > 0)
                                {
                                    player.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 60));
                                }

                                //damage weapon
                                held.hurtAndBreak(fragLevel > 0 ? 3 : 1, player, (playerEntity) ->
                                        playerEntity.broadcastBreakEvent(player.getUsedItemHand()));

                                //get particle position
                                double pX = (attacker.getX() + player.getX()) / 2 + (ParryModUtil.random.nextDouble()-0.5) * 0.2 + (attackerDirNorm.x * 0.2);
                                double pY = ((attacker.getY() + player.getY()) / 2) + 1.45 + (ParryModUtil.random.nextDouble()-0.5) * 0.2+ (attackerDirNorm.y * 0.2);
                                double pZ = (attacker.getZ() + player.getZ()) / 2 + (ParryModUtil.random.nextDouble()-0.5) * 0.2+ (attackerDirNorm.z * 0.2);

                                //play particles and sound
                                player.level.playSound(null, player.blockPosition(), ModSoundEvents.BLOCK_HIT.get(), SoundCategory.PLAYERS, 1, ParryModUtil.random.nextFloat() * 2f);
                                ((ServerWorld) player.level).sendParticles(ModParticles.PARRY_PARTICLE.get(), pX, pY, pZ, 1, 0D, 0D, 0D, 0.0D);

                                //cancel player damage logic
                                event.setCanceled(true);
                            }
                            else
                            {
                                //called when a player's weapon phases through a clash, caused by the phasing curse
                                player.level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1, ParryModUtil.random.nextFloat() * 2f);
                            }
                        }
                    }
                }
            }
        }
    }
}
