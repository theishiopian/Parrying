package com.theishiopian.parrying.Utility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;

public class Util
{
    public static boolean IsWeapon(ItemStack stack)
    {
        return stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE);
    }

    //todo use this with spears
    @Nullable
    public static Entity GetAttackTargetWithRange(@Nullable ItemStack toAttackWith, LivingEntity toAttackFrom)
    {
        float range = 5f;
        if(toAttackWith != null && toAttackWith.getAttributeModifiers(EquipmentSlotType.MAINHAND).containsKey(ForgeMod.REACH_DISTANCE))
        {
            range += toAttackWith.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(ForgeMod.REACH_DISTANCE.get()).stream().findFirst().get().getAmount();
        }

        Vector3d eyePos = toAttackFrom.getEyePosition(1);
        Vector3d lookVector = toAttackFrom.getViewVector(1.0F);
        Vector3d projection = eyePos.add(lookVector.x * range, lookVector.y * range, lookVector.z * range);
        AxisAlignedBB axisalignedbb = toAttackFrom.getBoundingBox().expandTowards(lookVector.scale(range)).inflate(1.0D, 1.0D, 1.0D);
        EntityRayTraceResult potentialTarget = ProjectileHelper.getEntityHitResult(toAttackFrom, eyePos, projection, axisalignedbb, (entity) -> !entity.isSpectator() && entity.isPickable(), range);
        if(potentialTarget != null && toAttackFrom.canSee(potentialTarget.getEntity()) /*&& potentialObstruction.getBlockPos().distSqr(toAttackFrom.position(), true) > potentialTarget.distanceTo(toAttackFrom)*/)
        {
            return potentialTarget.getEntity();
        }

        return null;
    }
}
