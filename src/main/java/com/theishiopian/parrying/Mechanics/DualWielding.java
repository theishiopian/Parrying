package com.theishiopian.parrying.Mechanics;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is a container for all dual wielding mechanics.
 * Which hand a player attacks with is handled by the client, the actual
 * attack logic is handled by the server. Communication between the two is
 * handled via SwingPacket.
 *
 * NOTE: all fields in the client region are CLIENT ONLY. They cannot be accessed from the server
 * It may be prudent to develop a system that lets the server ask the client if the player is dual wielding in the future...
 */
public class DualWielding
{
    //region CLIENT
    @OnlyIn(Dist.CLIENT) public static boolean IsDualWielding = false;
    @OnlyIn(Dist.CLIENT) public static Hand CurrentHand = Hand.MAIN_HAND;
    //endregion

    //region SERVER

    /**
     * This method handles attacking while dual wielding.
     * @param player the player doing the dual wielding attack
     * @param currentHand the hand the client says it is currently using for dual wielding
     */
    public static void DoDualWield(ServerPlayerEntity player, Hand currentHand)
    {
        float range = 2.5f;
        //ItemStack handItem = player.getItemInHand(currentHand);


        Vector3d eyePos = player.getEyePosition(1);
        Vector3d lookVector = player.getViewVector(1.0F);
        Vector3d projection = eyePos.add(lookVector.x * range, lookVector.y * range, lookVector.z * range);
        AxisAlignedBB axisalignedbb = player.getBoundingBox().expandTowards(lookVector.scale(range)).inflate(1.0D, 1.0D, 1.0D);
        EntityRayTraceResult entityraytraceresult = ProjectileHelper.getEntityHitResult(player, eyePos, projection, axisalignedbb, (entity) -> !entity.isSpectator() && entity.isPickable(), range);

        //todo attack here

        player.resetAttackStrengthTicker();
    }
    //endregion
}
