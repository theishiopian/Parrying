package com.theishiopian.parrying.Mechanics;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is a container for all dual wielding mechanics.
 * Which hand a player attacks with is handled by the client, the actual
 * attack logic is handled by the server. Communication between the two is
 * handled via SwingPacket.
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
        //attack here
    }
    //endregion
}
