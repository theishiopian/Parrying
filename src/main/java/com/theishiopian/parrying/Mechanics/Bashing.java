package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.*;
import com.theishiopian.parrying.Utility.ParryModUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public abstract class Bashing
{
    public static void Bash(ServerPlayer player)
    {
        if(Config.bashEnabled.get())
        {
            if(player != null && player.isBlocking())
            {
                List<LivingEntity> list = player.level.getEntitiesOfClass(LivingEntity.class, new AABB(player.position().x + 3, player.position().y + 3, player.position().z + 3,player.position().x - 3, player.position().y - 3, player.position().z - 3));

                list.remove(player);
                Random random = ParryModUtil.random;
                ItemStack main = player.getMainHandItem();
                ItemStack off = player.getOffhandItem();
                ItemStack shield = null;
                InteractionHand hand = InteractionHand.OFF_HAND;

                if(main.canPerformAction(net.minecraftforge.common.ToolActions.SHIELD_BLOCK))
                {
                    shield = main;
                    //Debug.log("shield in mainhand");
                    hand = InteractionHand.MAIN_HAND;
                }
                else if(off.canPerformAction(net.minecraftforge.common.ToolActions.SHIELD_BLOCK))
                {
                    //Debug.log("shield in offhand");
                    shield = off;
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
                    //Debug.log("begin bash");
                    list.sort(distCompare);
                    Vec3 pDir = player.getViewVector(1);
                    int bashes = 0;
                    //Debug.log(shield);
                    assert shield != null : "How";
                    int level = Config.bashingEnchantEnabled.get() ? EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.BASHING.get(), shield) : 0;
                    for (LivingEntity target : list)
                    {
                        Vec3 dir = (target.position().subtract(player.position())).normalize();
                        double dot = dir.dot(pDir);
                        //default 0.85
                        if (dot > Config.bashAngle.get() && player.position().distanceTo(target.position()) <= 3 && !target.isBlocking())
                        {
                            BashEntity(target, player, shield, hand);
                            bashes++;
                        }

                        if (bashes >= Config.bashTargets.get() + level) break;
                    }

                    player.level.playSound(null, player.blockPosition(), bashes == 0 ? ModSoundEvents.SHIELD_BASH_MISS.get() : ModSoundEvents.SHIELD_BASH.get(), SoundSource.PLAYERS, 1, random.nextFloat() * 0.5f + 0.5f);
                    player.stopUsingItem();
                    player.swing(hand, true);
                    player.getCooldowns().addCooldown(shield.getItem(), bashes == 0 ? Config.bashMissCooldown.get() : Config.bashBaseCooldown.get() + 20 * bashes);

                    double pX = player.position().x + pDir.x;
                    double pY = player.position().y + 1.5f + pDir.y;
                    double pZ = player.position().z + pDir.z;
                    if(bashes > 0)((ServerLevel) player.level).sendParticles(ModParticles.BASH_PARTICLE.get(), pX, pY, pZ, 1, 0D, 0D, 0D, 0.0D);
                    if(bashes >=3) ModTriggers.bigBash.trigger(player);
                }
                else
                {
                    player.level.playSound(null, player.blockPosition(), ModSoundEvents.SHIELD_BASH_MISS.get(), SoundSource.PLAYERS, 1, random.nextFloat() * 0.5f + 0.5f);
                    player.stopUsingItem();
                    player.swing(hand, true);
                    assert shield != null : "U what m8";
                    player.getCooldowns().addCooldown(shield.getItem(), Config.bashMissCooldown.get());
                }
            }
        }
    }

    private static void BashEntity(LivingEntity target, Player player, ItemStack shield, InteractionHand hand)
    {
        DamageSource source = new DamageSource("bludgeoning");

        player.causeFoodExhaustion(0.5f);

        shield.hurtAndBreak(1, player, (playerEntity) ->
                playerEntity.broadcastBreakEvent(hand));

        MobEffectInstance instance = new MobEffectInstance(ModEffects.STUNNED.get(), 60);
        target.hurt(source, 2);
        (target).addEffect(instance);
        (target).knockback(0.5f, -player.getViewVector(1).x, -player.getViewVector(1).z);
        target.hurtMarked = true;
    }
}
