package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.ModEffects;
import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModParticles;
import com.theishiopian.parrying.Registration.ModSoundEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public abstract class Bashing
{
    public static void Bash(ServerPlayerEntity player)
    {
        if(Config.bashEnabled.get())
        {
            if(player != null && player.isBlocking())
            {
                List<LivingEntity> list = player.level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(player.position().x + 3, player.position().y + 3, player.position().z + 3,player.position().x - 3, player.position().y - 3, player.position().z - 3));

                list.remove(player);
                Random random = new Random();
                ItemStack main = player.getMainHandItem();
                ItemStack off = player.getOffhandItem();
                ItemStack shield = null;
                Hand hand = Hand.OFF_HAND;
                if(main.isShield(player))
                {
                    shield = main;
                    hand = Hand.MAIN_HAND;
                }
                else if(off.isShield(player))
                {
                    shield = off;
                    hand = Hand.OFF_HAND;
                }

                //sort by distance
                Comparator<LivingEntity> distCompare = (o1, o2) ->
                {
                    double distA = o1.position().distanceTo(player.position());
                    double distB = o2.position().distanceTo(player.position());

                    return Double.compare(distA, distB);
                };

                if(list.size() > 0)
                {
                    list.sort(distCompare);
                    Vector3d pDir = player.getViewVector(1);
                    int bashes = 0;
                    assert shield != null : "How";
                    int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.BASHING.get(), shield);
                    for (LivingEntity target : list)
                    {
                        Vector3d dir = (target.position().subtract(player.position())).normalize();
                        double dot = dir.dot(pDir);
                        //default 0.85
                        if (dot > Config.bashAngle.get() && player.position().distanceTo(target.position()) <= 3 && !target.isBlocking())
                        {
                            BashEntity(target, player, shield, hand);
                            bashes++;
                        }

                        if (bashes >= Config.bashTargets.get() + level) break;
                    }

                    player.level.playSound(null, player.blockPosition(), bashes == 0 ? ModSoundEvents.SHIELD_BASH_MISS.get() : ModSoundEvents.SHIELD_BASH.get(), SoundCategory.PLAYERS, 1, random.nextFloat() * 0.5f + 0.5f);
                    player.stopUsingItem();
                    player.swing(hand);
                    player.getCooldowns().addCooldown(shield.getItem(), bashes == 0 ? Config.bashMissCooldown.get() : Config.bashBaseCooldown.get() + 20 * bashes);

                    double pX = player.position().x + pDir.x;
                    double pY = player.position().y + 1.5f + pDir.y;
                    double pZ = player.position().z + pDir.z;
                    if(bashes > 0)((ServerWorld) player.level).sendParticles(ModParticles.BASH_PARTICLE.get(), pX, pY, pZ, 1, 0D, 0D, 0D, 0.0D);

                }
                else
                {
                    player.level.playSound(null, player.blockPosition(), ModSoundEvents.SHIELD_BASH_MISS.get(), SoundCategory.PLAYERS, 1, random.nextFloat() * 0.5f + 0.5f);
                    player.stopUsingItem();
                    player.swing(hand);
                    assert shield != null : "U what m8";
                    player.getCooldowns().addCooldown(shield.getItem(), Config.bashMissCooldown.get());
                }
            }
        }
    }

    private static void BashEntity(LivingEntity target, PlayerEntity player, ItemStack shield, Hand hand)
    {
        DamageSource source = new DamageSource("generic");

        player.causeFoodExhaustion(0.5f);

        shield.hurtAndBreak(1, player, (playerEntity) ->
                playerEntity.broadcastBreakEvent(hand));

        EffectInstance instance = new EffectInstance(ModEffects.STUNNED.get(), 60);
        target.hurt(source, 2);
        (target).addEffect(instance);
        (target).knockback(0.5f, -player.getViewVector(1).x, -player.getViewVector(1).z);
        target.hurtMarked = true;
    }
}
