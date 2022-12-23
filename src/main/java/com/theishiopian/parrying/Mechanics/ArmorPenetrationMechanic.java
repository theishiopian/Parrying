package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Utility.ModUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ToolActions;

import java.util.Objects;

public abstract class ArmorPenetrationMechanic
{
    static boolean bypassing = false;

    public static boolean IsNotBypassing()
    {
        return !bypassing;
    }

    public static void DoAPDamage(float amount, float attackStrength, float ap, LivingEntity target, LivingEntity attacker, boolean bypassShield, String src)
    {
        if(!bypassing)
        {
            bypassing = true;
            float boost = EnchantmentHelper.getDamageBonus(attacker.getMainHandItem(), target.getMobType());
            int strLevel = attacker.hasEffect(MobEffects.DAMAGE_BOOST) ? Objects.requireNonNull(attacker.getEffect(MobEffects.DAMAGE_BOOST)).getAmplifier() : 0;
            amount += strLevel * 3;
            boolean critical = attacker instanceof Player && ModUtil.PlayerCritical((Player) attacker, target, attackStrength);
            if(critical)amount *= 1.5f;
            amount *= 0.2F + attackStrength * attackStrength * 0.8F;
            boost *= attackStrength;
            amount += boost;
            float nonAP = 1 - ap;
            float dmgAP = (amount * ap);
            float dmgNAP = (amount * nonAP);

            float healthBefore = target.getHealth();

            target.hurt(new EntityDamageSource(src, attacker), dmgNAP);
            target.invulnerableTime = 0;
            if(!ModUtil.IsBlocked(target, attacker))
            {
                target.hurt(new EntityDamageSource(src, attacker).bypassArmor(), dmgAP);
            }
            else if(bypassShield)
            {
                //this is stupid
                //minecraft apparently has decided that armor and shields are the same thing, so bypassArmor is also used to bypass shields.
                //thus, I need to do all this math AGAIN
                float halfDamage = amount / 2;
                float halfDamageAfterAbsorb = CombatRules.getDamageAfterAbsorb(halfDamage, (float)target.getArmorValue(), (float)target.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
                target.hurt(new EntityDamageSource(src, attacker).bypassArmor(), halfDamage * ap);
                target.invulnerableTime = 0;
                target.hurt(new EntityDamageSource(src, attacker).bypassArmor(), halfDamageAfterAbsorb * nonAP);

                BlockHelper(attacker, target, amount / 2);
            }
            else
            {
                BlockHelper(attacker, target, amount);
            }

            if(attacker.getMainHandItem().canPerformAction(ToolActions.SWORD_SWEEP) && attacker instanceof Player p)
            {
                SweepHelper(p, target, amount);
            }

            attacker.getMainHandItem().hurtAndBreak(1, attacker, (playerEntity) -> playerEntity.broadcastBreakEvent(attacker.getUsedItemHand()));

            float healthAfter = target.getHealth();

            if(attacker instanceof Player)
            {
                PostAttackHelper((Player) attacker, boost, attackStrength, critical, target, attacker.getMainHandItem(), healthBefore - healthAfter);
            }

            bypassing = false;
        }
    }

    private static void BlockHelper(LivingEntity toBlock, LivingEntity blocker, float blockedDMG)
    {
        toBlock.knockback(0.5F, toBlock.getX() - blocker.getX(), toBlock.getZ() - blocker.getZ());
        blocker.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + blocker.level.random.nextFloat() * 0.4F);

        if(blocker instanceof ServerPlayer)
        {
            ItemStack shield = blocker.getUseItem();
            ((ServerPlayer)blocker).awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(blockedDMG * 10.0F));
            ((ServerPlayer)blocker).awardStat(Stats.ITEM_USED.get(shield.getItem()));

            if (blockedDMG >= 3.0F)
            {
                int i = (int) (1 + Math.floor(blockedDMG));
                InteractionHand hand = blocker.getUsedItemHand();
                blocker.getUseItem().hurtAndBreak(i, blocker, (entity) ->
                {
                    entity.broadcastBreakEvent(hand);
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem((Player) blocker, shield, hand);
                });
                if (shield.isEmpty())
                {
                    if (hand == InteractionHand.MAIN_HAND)
                    {
                        blocker.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    }
                    else
                    {
                        blocker.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }

                    blocker.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + blocker.level.random.nextFloat() * 0.4F);
                }
            }
        }
    }

    private static void SweepHelper(Player player, LivingEntity target, float totalDamage)
    {
        float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * totalDamage;

        for(LivingEntity e : player.level.getEntitiesOfClass(LivingEntity.class, player.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(player, target)))
        {
            if (e != player && e != target && !player.isAlliedTo(e) && (!(e instanceof ArmorStand) || !((ArmorStand)e).isMarker()) && player.distanceToSqr(e) < 9.0D)
            {
                e.knockback(0.4F, Mth.sin(player.getYRot() * ((float)Math.PI / 180F)), (-Mth.cos(player.getYRot() * ((float)Math.PI / 180F))));
                e.hurt(DamageSource.playerAttack(player), f3);
            }
        }

        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
        player.sweepAttack();
    }

    private static void PostAttackHelper(Player player, float boost, float attackStrength, boolean critical, Entity target, ItemStack held, float damageDone)
    {
        boolean attackScale = attackStrength > 0.9f;
        if(critical)
        {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 1.0F, 1.0F);
            player.crit(target);
        }

        boolean knockbackAttack = attackScale && player.isSprinting();

        if(knockbackAttack)
        {
            if(target instanceof LivingEntity)
            {
                ((LivingEntity) target).knockback(0.5f, Math.sin(player.getYRot() * ((float)Math.PI / 180F)), (-Math.cos(player.getYRot() * ((float)Math.PI / 180F))));
                target.hurtMarked = true;
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, player.getSoundSource(), 1.0F, 1.0F);
            }
        }

        float delta = player.walkDist - player.walkDistO;

        if (!critical && player.isOnGround() && delta < player.getSpeed())
        {
            if (attackScale)
            {
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, player.getSoundSource(), 1.0F, 1.0F);
            }
            else
            {
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, player.getSoundSource(), 1.0F, 1.0F);
            }
        }

        if (boost > 0.0F)
        {
            player.magicCrit(target);
        }

        player.setLastHurtMob(target);

        if (target instanceof LivingEntity)
        {
            EnchantmentHelper.doPostHurtEffects((LivingEntity)target, player);
        }

        EnchantmentHelper.doPostDamageEffects(player, target);
        Entity entity = target;
        if (target instanceof net.minecraftforge.entity.PartEntity)
        {
            entity = ((net.minecraftforge.entity.PartEntity<?>) target).getParent();
        }

        if (!player.level.isClientSide && entity instanceof LivingEntity)
        {
            ItemStack copy = held.copy();
            held.hurtEnemy((LivingEntity)entity, player);

            if (held.isEmpty())
            {
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copy, InteractionHand.MAIN_HAND);
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }

        if (target instanceof LivingEntity)
        {
            player.awardStat(Stats.DAMAGE_DEALT, Math.round(damageDone));

            if (player.level instanceof ServerLevel && damageDone > 2.0F)
            {
                int k = (int) ((double) damageDone * 0.5D);
                ((ServerLevel) player.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5D), target.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
            }
        }

        player.causeFoodExhaustion(0.1F);
    }
}
