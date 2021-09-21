package com.theishiopian.parrying.Mechanics;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
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
        //todo attack here
        Entity target = Util.GetAttackTargetWithRange(player.getItemInHand(currentHand), player);

        if(target != null)
        {
            if(currentHand == Hand.MAIN_HAND)
            {
                player.attack(target);
            }
            else
            {
                ItemStack itemstack = player.getItemInHand(Hand.OFF_HAND);
                player.setItemInHand(Hand.OFF_HAND, player.getItemInHand(Hand.MAIN_HAND));
                player.setItemInHand(Hand.MAIN_HAND, itemstack);

                player.attack(target);
                itemstack = player.getItemInHand(Hand.OFF_HAND);
                player.setItemInHand(Hand.OFF_HAND, player.getItemInHand(Hand.MAIN_HAND));
                player.setItemInHand(Hand.MAIN_HAND, itemstack);
            }

        }

        player.resetAttackStrengthTicker();
    }
    //endregion
}
