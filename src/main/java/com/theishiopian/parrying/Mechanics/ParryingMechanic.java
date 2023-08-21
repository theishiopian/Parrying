package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Items.BandolierItem;
import com.theishiopian.parrying.Network.GameplayStatusPacket;
import com.theishiopian.parrying.Network.SyncDefPacket;
import com.theishiopian.parrying.ParryingMod;
import com.theishiopian.parrying.Registration.*;
import com.theishiopian.parrying.Utility.ModUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.network.PacketDistributor;

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
                    if(ModUtil.IsStackWeapon(held))
                    {
                        Entity attacker = source.getEntity();//the attacking entity
                        Vec3 playerLookDir = player.getViewVector(1);//the direction the player is looking

                        //enchantment levels
                        int ripLevel = Config.riposteEnchantEnabled.get() ? EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.RIPOSTE.get(), held) : 0;
                        int fragLevel = Config.fragileCurseEnabled.get() ? EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FRAGILE.get(), held) : 0;
                        int phaseLevel = Config.phasingCurseEnabled.get() ? EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.PHASING.get(), held) : 0;

                        assert attacker != null : "How the hell did this throw null???";
                        Vec3 attackerDir = new Vec3(attacker.position().x, 0, attacker.position().z).subtract(new Vec3(player.position().x, 0, player.position().z));
                        Vec3 attackerDirNorm = attackerDir.normalize();

                        //the angle from player look direction to the direction from the player to the enemy
                        double angle = playerLookDir.dot(attackerDirNorm);

                        if(angle >= GetSurfaceAngle(player) && player.swinging)
                        {
                            //phasing check
                            if(phaseLevel == 0 || ModUtil.random.nextInt(3) != 0)
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
                                double pX = (attacker.getX() + player.getX()) / 2 + (ModUtil.random.nextDouble()-0.5) * 0.2 + (attackerDirNorm.x * 0.2);
                                double pY = ((attacker.getY() + player.getY()) / 2) + 1.45 + (ModUtil.random.nextDouble()-0.5) * 0.2+ (attackerDirNorm.y * 0.2);
                                double pZ = (attacker.getZ() + player.getZ()) / 2 + (ModUtil.random.nextDouble()-0.5) * 0.2+ (attackerDirNorm.z * 0.2);

                                SoundEvent toPlay = GetMaterialParrySound(held.getItem());

                                //play particles and sound
                                player.level.playSound(null, player.blockPosition(), toPlay, SoundSource.PLAYERS, 1, (ModUtil.random.nextFloat() * 0.5f) + 0.5f);
                                ((ServerLevel) player.level).sendParticles(ModParticles.PARRY_PARTICLE.get(), pX, pY, pZ, 1, 0D, 0D, 0D, 0.0D);

                                //cancel player damage logic
                                event.setCanceled(true);
                            }
                            else
                            {
                                //called when a player's weapon phases through a clash, caused by the phasing curse
                                player.level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1, ModUtil.random.nextFloat() * 2f);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void DoParryTick(ServerPlayer player)
    {
        if(BandolierItem.Has(player.getUUID()))
        {
            if(GameplayStatusPacket.isPlayerPlaying(player) && GameplayStatusPacket.getTicks(player) > 5)
            {
                BandolierItem.findItemInBandolier(player);
            }

            BandolierItem.Remove(player.getUUID());
        }

        if(!ModUtil.IsStackWeapon(player.getMainHandItem()) && ModUtil.IsStackWeapon(player.getOffhandItem()))
        {
            DualWieldingMechanic.dualWielders.remove(player.getUUID());
        }

        float newValue;
        ParryingMechanic.ServerDefenseValues.putIfAbsent(player.getUUID(), 1f);
        float v = ParryingMechanic.ServerDefenseValues.get(player.getUUID());

        if(v <= 0)
        {
            player.addEffect(new MobEffectInstance(ModEffects.STUNNED.get(), 60));
            float pitch = ModUtil.random.nextFloat() * 0.4f + 0.8f;
            player.level.playSound(null, player.blockPosition(), ModSoundEvents.DEFENSE_BREAK.get(), SoundSource.PLAYERS, 1f, pitch);
            Vec3 pos = player.position();

            ((ServerLevel) player.level).sendParticles(ParticleTypes.ANGRY_VILLAGER, pos.x, pos.y, pos.z, 30, 0.5D, 2D, 0.5D, 0.0D);
            Vec3 dir = player.getViewVector(1);
            player.knockback(1, dir.x, dir.z);
            player.hurtMarked = true;

            ModTriggers.stagger.trigger((ServerPlayer) player);

            if (player.isBlocking())
            {
                player.getCooldowns().addCooldown(player.getUseItem().getItem(), 60);
                player.stopUsingItem();
                player.level.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1f,1f);
            }
            newValue = 0.001f;
        }
        else if(v < 1)
        {
            newValue = v + 0.003f;
        }
        else
        {
            newValue = 1f;
        }

        ParryingMechanic.ServerDefenseValues.replace(player.getUUID(), newValue);

        ParryingMod.channel.send(PacketDistributor.PLAYER.with(()-> player), new SyncDefPacket(newValue));
    }

    public static SoundEvent GetMaterialParrySound(Item item)
    {
        SoundEvent toPlay = ModSoundEvents.PARRY_METAL.get();

        if(item instanceof TieredItem tieredItem)
        {
            if(tieredItem.getTier() == Tiers.WOOD)
            {
                toPlay = ModSoundEvents.PARRY_WOOD.get();
            }
            else if(tieredItem.getTier() == Tiers.STONE)
            {
                toPlay = ModSoundEvents.PARRY_STONE.get();
            }
        }

        return toPlay;
    }

    /**
     * Calculates the angle to the target that must be exceeded in order to parry.
     * @param player the player
     * @return the adjusted value, taking into account attack recharge.
     */
    public static double GetSurfaceAngle(Player player)
    {
        double angle = Config.parryAngle.get();
        double remainder = 1 - angle;
        double charge = Mth.clamp(player.getAttackStrengthScale(0f), 0.1f, 1f);
        double penalty = (1f - charge) * remainder;
        return angle + (penalty * Config.parryPenalty.get());
    }
}
