package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.ParryingMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.CombatRules;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public abstract class ArmorPenetration
{
    static boolean bypassing = false;

    public static boolean IsBypassing()
    {
        return bypassing;
    }

    public static void DoAPDamage(float amount, float ap, LivingEntity entity, LivingEntity attacker, boolean bypassShield, String src)
    {
        if(!bypassing)
        {
            ParryingMod.LOGGER.info("piercing");
            bypassing = true;
            float nonAP = 1 - ap;
            float dmgAP = amount * ap;
            float dmgNAP = amount * nonAP;

            entity.hurt(new EntityDamageSource(src, attacker), dmgNAP);
            entity.invulnerableTime = 0;
            if(!IsBlocked(entity, attacker))
            {
                entity.hurt(new EntityDamageSource(src, attacker).bypassArmor(), dmgAP);
            }
            else if(bypassShield)
            {
                //this is stupid
                //minecraft apparently has decided that armor and shields are the same thing, so bypassArmor is also used to bypass shields.
                //thus, I need to do all this math AGAIN
                float d = amount/2;
                float da = CombatRules.getDamageAfterAbsorb(d, (float)entity.getArmorValue(), (float)entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
                //TODO: add shield effects
                entity.hurt(new EntityDamageSource(src, attacker).bypassArmor(), d * ap);
                entity.invulnerableTime = 0;
                entity.hurt(new EntityDamageSource(src, attacker).bypassArmor(), da * nonAP);

                BlockHelper(attacker, entity, amount / 2);
            }
            else
            {
                BlockHelper(attacker, entity, amount);
            }

            attacker.getMainHandItem().hurtAndBreak(1, attacker, (playerEntity) -> playerEntity.broadcastBreakEvent(attacker.getUsedItemHand()));

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

}
