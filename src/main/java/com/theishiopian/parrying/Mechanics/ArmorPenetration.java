package com.theishiopian.parrying.Mechanics;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.CombatRules;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public abstract class ArmorPenetration
{
    static boolean bypassing = false;

    public static boolean IsNotBypassing()
    {
        return !bypassing;
    }

    public static void DoAPDamage(float amount, float ap, LivingEntity target, LivingEntity attacker, boolean bypassShield, String src)
    {
        if(!bypassing)
        {
            bypassing = true;
            float boost = EnchantmentHelper.getDamageBonus(attacker.getMainHandItem(), target.getMobType());
            float nonAP = 1 - ap;
            float dmgAP = (amount * ap) + boost/2;
            float dmgNAP = (amount * nonAP) + boost/2;

            float healthBefore = target.getHealth();

            target.hurt(new EntityDamageSource(src, attacker), dmgNAP);
            target.invulnerableTime = 0;
            if(!IsBlocked(target, attacker))
            {
                target.hurt(new EntityDamageSource(src, attacker).bypassArmor(), dmgAP);
            }
            else if(bypassShield)
            {
                //this is stupid
                //minecraft apparently has decided that armor and shields are the same thing, so bypassArmor is also used to bypass shields.
                //thus, I need to do all this math AGAIN
                float d = amount/2;
                float da = CombatRules.getDamageAfterAbsorb(d, (float)target.getArmorValue(), (float)target.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
                target.hurt(new EntityDamageSource(src, attacker).bypassArmor(), d * ap);
                target.invulnerableTime = 0;
                target.hurt(new EntityDamageSource(src, attacker).bypassArmor(), da * nonAP);

                BlockHelper(attacker, target, amount / 2);
            }
            else
            {
                BlockHelper(attacker, target, amount);
            }

            attacker.getMainHandItem().hurtAndBreak(1, attacker, (playerEntity) -> playerEntity.broadcastBreakEvent(attacker.getUsedItemHand()));


            float healthAfter = target.getHealth();

            if(attacker instanceof PlayerEntity)PostAttackHelper(boost, (PlayerEntity) attacker, target, attacker.getMainHandItem(), healthBefore - healthAfter);

            bypassing = false;
        }
    }

    private static boolean IsBlocked(LivingEntity defender, LivingEntity attacker)
    {
        if (defender.isBlocking())
        {
            Vector3d attackPos = attacker.position();
            Vector3d defenderLook = defender.getViewVector(1.0F);
            Vector3d vector3d1 = attackPos.vectorTo(defender.position()).normalize();
            vector3d1 = new Vector3d(vector3d1.x, 0.0D, vector3d1.z);
            return vector3d1.dot(defenderLook) < 0.0D;
        }

        return false;
    }
    private static void BlockHelper(LivingEntity toBlock, LivingEntity blocker, float blockedDMG)
    {
        toBlock.knockback(0.5F, toBlock.getX() - blocker.getX(), toBlock.getZ() - blocker.getZ());
        blocker.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + blocker.level.random.nextFloat() * 0.4F);

        if(blocker instanceof ServerPlayerEntity)
        {
            ItemStack shield = blocker.getUseItem();
            ((ServerPlayerEntity)blocker).awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(blockedDMG * 10.0F));
            ((ServerPlayerEntity)blocker).awardStat(Stats.ITEM_USED.get(shield.getItem()));

            if (blockedDMG >= 3.0F)
            {
                int i = 1 + MathHelper.floor(blockedDMG);
                Hand hand = blocker.getUsedItemHand();
                blocker.getUseItem().hurtAndBreak(i, blocker, (entity) ->
                {
                    entity.broadcastBreakEvent(hand);
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem((PlayerEntity) blocker, shield, hand);
                });
                if (shield.isEmpty())
                {
                    if (hand == Hand.MAIN_HAND)
                    {
                        blocker.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
                    }
                    else
                    {
                        blocker.setItemSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
                    }

                    blocker.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + blocker.level.random.nextFloat() * 0.4F);
                }
            }
        }
    }

    private static void PostAttackHelper(float boost, PlayerEntity player, Entity target, ItemStack held, float damageDone)
    {
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
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copy, Hand.MAIN_HAND);
                player.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
        }

        if (target instanceof LivingEntity)
        {
            player.awardStat(Stats.DAMAGE_DEALT, Math.round(damageDone));

            if (player.level instanceof ServerWorld && damageDone > 2.0F)
            {
                int k = (int) ((double) damageDone * 0.5D);
                ((ServerWorld) player.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5D), target.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
            }
        }

        player.causeFoodExhaustion(0.1F);
    }
}
