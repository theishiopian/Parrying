package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.*;
import com.theishiopian.parrying.Utility.ParryModUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class is a container for the parrying mechanic. This mechanic is triggered from CommonEvents, within OnAttackedEvent.
 */
public abstract class ParryingMechanic
{
    public static float ClientDefense = 1;
    public static HashMap<UUID, Float> ServerDefenseValues = new HashMap<>();

    public static void Parry(LivingAttackEvent event, Player player)
    {
        if(Config.parryEnabled.get() && event.getEntity() instanceof ServerPlayer)
        {
            DamageSource source = event.getSource();//the properties of the damage

            if(source instanceof EntityDamageSource && !(source instanceof IndirectEntityDamageSource))
            {
                //make sure the player isn't stunned
                if(!player.hasEffect(ModEffects.STUNNED.get()))
                {
                    ItemStack held = player.getMainHandItem();//the item in use

                    //is the player holding a weapon?
                    if(ParryModUtil.IsWeapon(held))
                    {
                        Entity attacker = source.getEntity();//the attacking entity
                        Vec3 playerLookDir = player.getViewVector(1);//the direction the player is looking

                        //enchantment levels
                        int ripLevel = Config.riposteEnchantEnabled.get() ? EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.RIPOSTE.get(), held) : 0;
                        int fragLevel = Config.fragileCurseEnabled.get() ? EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FRAGILE.get(), held) : 0;
                        int phaseLevel = Config.phasingCurseEnabled.get() ? EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.PHASING.get(), held) : 0;

                        assert attacker != null : "How the hell did this throw null???";
                        Vec3 attackerDir = attacker.position().subtract(player.position());
                        Vec3 attackerDirNorm = attackerDir.normalize();

                        //the angle from player look direction to the direction from the player to the enemy
                        double angle = new Vec3(playerLookDir.x, 0, playerLookDir.z).dot(new Vec3(attackerDirNorm.x, 0, attackerDirNorm.z));

                        //default 0.95
                        if(angle >= Config.parryAngle.get() && player.swinging)
                        {
                            //phasing check
                            if(phaseLevel == 0 || ParryModUtil.random.nextInt(3) != 0)
                            {
                                //successful parry
                                player.awardStat(ModStats.parry);

                                //reduce defense
                                float reduction = event.getAmount() / player.getMaxHealth();
                                UUID id = player.getUUID();
                                float oldValue = ParryingMechanic.ServerDefenseValues.get(id);
                                ParryingMechanic.ServerDefenseValues.replace(id, oldValue - reduction);

                                player.knockback(0.33f, attackerDir.x, attackerDir.z);
                                player.hurtMarked = true;//this makes knockback work
                                player.causeFoodExhaustion(0.5f);//exhaust player

                                //add strength for riposte
                                if(Config.riposteEnchantEnabled.get() && ripLevel > 0)
                                {
                                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60));
                                }

                                //damage weapon
                                held.hurtAndBreak(fragLevel > 0 ? 3 : 1, player, (playerEntity) ->
                                        playerEntity.broadcastBreakEvent(player.getUsedItemHand()));

                                //get particle position
                                double pX = (attacker.getX() + player.getX()) / 2 + (ParryModUtil.random.nextDouble()-0.5) * 0.2 + (attackerDirNorm.x * 0.2);
                                double pY = ((attacker.getY() + player.getY()) / 2) + 1.45 + (ParryModUtil.random.nextDouble()-0.5) * 0.2+ (attackerDirNorm.y * 0.2);
                                double pZ = (attacker.getZ() + player.getZ()) / 2 + (ParryModUtil.random.nextDouble()-0.5) * 0.2+ (attackerDirNorm.z * 0.2);

                                //play particles and sound
                                player.level.playSound(null, player.blockPosition(), ModSoundEvents.BLOCK_HIT.get(), SoundSource.PLAYERS, 1, ParryModUtil.random.nextFloat() * 2f);
                                ((ServerLevel) player.level).sendParticles(ModParticles.PARRY_PARTICLE.get(), pX, pY, pZ, 1, 0D, 0D, 0D, 0.0D);

                                //cancel player damage logic
                                event.setCanceled(true);
                            }
                            else
                            {
                                //called when a player's weapon phases through a clash, caused by the phasing curse
                                player.level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1, ParryModUtil.random.nextFloat() * 2f);
                            }
                        }
                    }
                }
            }
        }
    }
}
