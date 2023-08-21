package com.theishiopian.parrying.Utility;

import com.theishiopian.parrying.ParryingMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This class contains misc methods that I have found to be useful in multiple places.
 * If a method is useful in several unrelated places, it goes in here.
 */
public class ModUtil
{
    public static final ResourceLocation GENERAL_ICONS = new ResourceLocation(ParryingMod.MOD_ID, "textures/gui/icons.png");
    public static final Random random = new Random();

    public static InteractionHand GetOtherHand(InteractionHand hand)
    {
        return hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

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

    public static boolean IsStackWeapon(ItemStack stack)
    {
        //eventually this will be removed, stack has a method for this, but item may become a problem
        return IsItemWeapon(stack.getItem());
    }

    @SuppressWarnings("deprecation")
    public static boolean IsItemWeapon(Item item)
    {
        return item.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE) && item.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_SPEED);
    }

    public static boolean ShouldBeHarmful(List<MobEffectInstance> list, LivingEntity target)
    {
        if(list != null)
        {
            boolean shouldBeHarmful = true;
            for (MobEffectInstance i : list)
            {
                boolean beneficial = i.getEffect().isBeneficial();
                boolean isInstantHeal = i.getEffect() == MobEffects.HEAL;
                boolean isInstantHarm = i.getEffect() == MobEffects.HARM;
                boolean targetUndead = target.isInvertedHealAndHarm();
                if(beneficial && !(targetUndead && isInstantHeal))
                {
                    shouldBeHarmful = false;
                    break;
                }
                else if(targetUndead && isInstantHarm)
                {
                    shouldBeHarmful = false;
                    break;
                }
            }

            return shouldBeHarmful;
        }

        return true;
    }

    public static Comparator<Entity> GetDistanceSorter(Entity target)
    {
        return (o1, o2) ->
        {
            double distA = o1.position().distanceTo(target.position());
            double distB = o2.position().distanceTo(target.position());

            return Double.compare(distA, distB);
        };
    }

    //TODO put corners at min and max instead of centered cube, should increase performance
    public static List<Entity> GetEntitiesInCone(Player player, double distance, double angle)//NOTE: angle is in dot product, NOT DEGREES
    {
        List<Entity> list = player.level.getEntitiesOfClass(Entity.class, new AABB(
                        player.position().x + distance,
                        player.position().y + distance,
                        player.position().z + distance,
                        player.position().x - distance,
                        player.position().y - distance,
                        player.position().z - distance));

        list.remove(player);

        list.sort(ModUtil.GetDistanceSorter(player));
        Vec3 pDir = player.getViewVector(1);
        return list.stream().filter(
                entity ->
                {
                    Vec3 dir = (entity.position().subtract(player.position())).normalize();
                    double dot = dir.dot(pDir);

                    return dot > angle &&
                            player.position().distanceTo(entity.position()) <= distance;
                }
        ).collect(Collectors.toList());
    }
}