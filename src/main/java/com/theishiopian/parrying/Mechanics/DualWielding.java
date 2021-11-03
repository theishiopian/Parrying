package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Utility.Util;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;

import java.util.HashMap;
import java.util.UUID;

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
    public static Hand CurrentHand = Hand.MAIN_HAND;
    //endregion

    //region SERVER
    public static HashMap<UUID, Hand> dualWielders = new HashMap<>();

    /**
     * This method handles attacking while dual wielding.
     * @param player the player doing the dual wielding attack
     * @param currentHand the hand the client says it is currently using for dual wielding
     */
    public static void DoDualWield(ServerPlayerEntity player, Hand currentHand)
    {
        if(Config.dualWieldEnabled.get())
        {
            //Debug.log("dual wield init");
            EntityRayTraceResult potentialTarget = Util.GetAttackTargetWithRange(player.getItemInHand(currentHand), player);
            dualWielders.put(player.getUUID(), currentHand);
            //Debug.log(currentHand);
            if(currentHand == Hand.MAIN_HAND)
            {
                if(potentialTarget != null)player.attack(potentialTarget.getEntity());
                player.swing(Hand.MAIN_HAND, true);
            }
            else
            {
                ItemStack offhand = player.getItemInHand(Hand.OFF_HAND);
                ItemStack mainhand = player.getItemInHand(Hand.MAIN_HAND);
                player.setItemInHand(Hand.MAIN_HAND, offhand);
                player.setItemInHand(Hand.OFF_HAND, mainhand);
                if(potentialTarget != null)player.attack(potentialTarget.getEntity());
                player.swing(Hand.OFF_HAND, true);
                player.setItemInHand(Hand.MAIN_HAND, mainhand);
                player.setItemInHand(Hand.OFF_HAND, offhand);
            }

            player.resetAttackStrengthTicker();
        }
    }

    public static boolean IsDualWielding(PlayerEntity player)
    {
        return Util.IsWeapon(player.getMainHandItem()) && Util.IsWeapon(player.getOffhandItem());
    }
    //endregion
}
