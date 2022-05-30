package com.theishiopian.parrying.Registration.Utility;

import com.theishiopian.parrying.ParryingMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Random;

/**
 * This class contains misc methods that I have found to be useful in multiple places.
 * If a method is useful in several unrelated places, it goes in here.
 */
public class ParryModUtil
{
    public static final ResourceLocation GENERAL_ICONS = new ResourceLocation(ParryingMod.MOD_ID, "textures/gui/icons.png");
    public static final Random random = new Random();

    public static boolean IsBlocked(LivingEntity defender, LivingEntity attacker)
    {
        if (defender.isBlocking() && attacker != null)
        {
            Vec3 attackPos = attacker.position();
            Vec3 defenderLook = defender.getViewVector(1.0F);
            Vec3 vector3d1 = attackPos.vectorTo(defender.position()).normalize();
            vector3d1 = new Vec3(vector3d1.x, 0.0D, vector3d1.z);
            return vector3d1.dot(defenderLook) < 0.0D;
        }

        return false;
    }

    public static boolean PlayerCritical(Player player, Entity target, float cacheStrength)
    {
        boolean attackPowerFull = cacheStrength > 0.9f;
        boolean hasFallen = player.fallDistance > 0;
        boolean inAir = !player.isOnGround();
        boolean notClimbing = !player.onClimbable();
        boolean notWet = !player.isInWater();
        boolean notBlind = !player.hasEffect(MobEffects.BLINDNESS);
        boolean notRiding = !player.isPassenger();
        boolean targetValid = target instanceof LivingEntity;
        boolean walking = !player.isSprinting();

        return attackPowerFull && hasFallen && inAir && notClimbing && notWet && notBlind && notRiding && targetValid && walking;
    }

    public static boolean IsWeapon(ItemStack stack)
    {
        return stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE);
    }

//    /**
//     * Gets an entity hit result, taking range modifiers into account.
//     * @param toAttackWith The weapon to use, if any.
//     * @param toAttackFrom The attacker.
//     * @return An entity hit result, if applicable.
//     */
//    @Nullable
//    public static EntityHitResult GetAttackTargetWithRange(@Nullable ItemStack toAttackWith, LivingEntity toAttackFrom)
//    {
//        float range = 2.5f;
//
//        if(toAttackWith != null && !toAttackWith.isEmpty())//has weapon
//        {
//            boolean hasRange = toAttackWith.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(ForgeMod.REACH_DISTANCE.get());
//            int joustLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.JOUSTING.get(), toAttackWith);
//
//            if(hasRange)
//            {
//                range += toAttackWith.getAttributeModifiers(EquipmentSlot.MAINHAND).get(ForgeMod.REACH_DISTANCE.get()).stream().findFirst().get().getAmount();
//            }
//
//            if(toAttackFrom.isPassenger() && joustLevel > 0)
//            {
//                range += (joustLevel * 2) + 0.5f;
//            }
//        }
//
//        Vec3 eyePos = toAttackFrom.getEyePosition();
//        Vec3 lookVector = toAttackFrom.getViewVector(1.0F);
//        Vec3 projection = eyePos.add(lookVector.x * range, lookVector.y * range, lookVector.z * range);
//        AABB box = toAttackFrom.getBoundingBox().expandTowards(lookVector.scale(range)).inflate(1.0D);
//        EntityHitResult potentialTarget = ProjectileUtil.getEntityHitResult(toAttackFrom, eyePos, projection, box, ((entity) -> !entity.isSpectator() && entity.isPickable()), range * range);
//
//        if(potentialTarget != null)
//        {
//            boolean unobstructed = toAttackFrom.hasLineOfSight(potentialTarget.getEntity());
//
//            if(unobstructed)
//            {
//                //Debug.log(toAttackFrom.position().distanceTo(potentialTarget.getEntity().position()));
//                return potentialTarget;
//            }
//        }
//
//        return null;
//    }

    public static Comparator<Entity> GetDistanceSorter(Entity target)
    {
        return (o1, o2) ->
        {
            double distA = o1.position().distanceTo(target.position());
            double distB = o2.position().distanceTo(target.position());

            return Double.compare(distA, distB);
        };
    }
}