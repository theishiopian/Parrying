package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.ModTags;
import com.theishiopian.parrying.Utility.ParryModUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class is a container for all dual wielding mechanics.
 * Which hand a player attacks with is handled by the client, the actual
 * attack logic is handled by the server. Communication between the two is
 * handled via SwingPacket.
 */
public class DualWielding
{
    //region CLIENT
    public static InteractionHand CurrentHand = InteractionHand.MAIN_HAND;
    //endregion

    //region SERVER
    public final static HashMap<UUID, InteractionHand> dualWielders = new HashMap<UUID, InteractionHand>();

    /**
     * This method handles attacking while dual wielding.
     * @param player the player doing the dual wielding attack
     * @param currentHand the hand the client says it is currently using for dual wielding
     */
    public static void DoDualWield(ServerPlayer player, InteractionHand currentHand)
    {
        if(Config.dualWieldEnabled.get())
        {
            //Debug.log("dual wield init");
            EntityHitResult potentialTarget = ParryModUtil.GetAttackTargetWithRange(player.getItemInHand(currentHand), player);
            dualWielders.put(player.getUUID(), currentHand);
            //Debug.log(currentHand);
            if(currentHand == InteractionHand.MAIN_HAND)
            {
                if(potentialTarget != null)player.attack(potentialTarget.getEntity());
                player.swing(InteractionHand.MAIN_HAND, true);
            }
            else
            {
                ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
                ItemStack mainhand = player.getItemInHand(InteractionHand.MAIN_HAND);
                player.setItemInHand(InteractionHand.MAIN_HAND, offhand);
                player.setItemInHand(InteractionHand.OFF_HAND, mainhand);
                if(potentialTarget != null)player.attack(potentialTarget.getEntity());
                player.swing(InteractionHand.OFF_HAND, true);
                player.setItemInHand(InteractionHand.MAIN_HAND, mainhand);
                player.setItemInHand(InteractionHand.OFF_HAND, offhand);
            }

            player.resetAttackStrengthTicker();
        }
    }

    public static boolean IsDualWielding(Player player)
    {
        boolean twoHanded = Config.dualWieldEnabled.get();
        ItemStack mainItem = player.getMainHandItem();
        ItemStack offItem = player.getOffhandItem();

        //boolean main = ParryModUtil.IsWeapon(mainItem) && (!mainItem.getItem().is(ModTags.TWO_HANDED_WEAPONS) || !twoHanded);
        boolean main = ParryModUtil.IsWeapon(mainItem) && (!mainItem.is(ModTags.TWO_HANDED_WEAPONS) || !twoHanded);

        //boolean off = ParryModUtil.IsWeapon(offItem) && (!offItem.getItem().is(ModTags.TWO_HANDED_WEAPONS) || !twoHanded);
        boolean off = ParryModUtil.IsWeapon(offItem) && (!offItem.is(ModTags.TWO_HANDED_WEAPONS) || !twoHanded);

        return main && off;
    }
    //endregion
}
