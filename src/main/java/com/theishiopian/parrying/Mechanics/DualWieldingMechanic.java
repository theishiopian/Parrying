package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.ModTags;
import com.theishiopian.parrying.Registration.Utility.ParryModUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class is a container for all dual wielding mechanics.
 * Which hand a player attacks with is handled by the client, the actual
 * attack logic is handled by the server. Communication between the two is
 * handled via SwingPacket.
 */
public class DualWieldingMechanic
{
    //region CLIENT
    public static InteractionHand CurrentHand = InteractionHand.MAIN_HAND;
    //endregion

    //region SERVER
    public final static HashMap<UUID, InteractionHand> dualWielders = new HashMap<>();

    /**
     * This method handles attacking while dual wielding.
     * @param player the player doing the dual wielding attack
     * @param currentHand the hand the client says it is currently using for dual wielding
     */
    public static void DoDualWield(ServerPlayer player, @Nullable Entity target, InteractionHand currentHand)
    {
        if(Config.dualWieldEnabled.get())
        {
            dualWielders.put(player.getUUID(), currentHand);

            if(currentHand == InteractionHand.MAIN_HAND)
            {
                if(target != null)player.attack(target);
                player.swing(InteractionHand.MAIN_HAND, true);
            }
            else
            {
                ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);
                ItemStack mainHandItem = player.getItemInHand(InteractionHand.MAIN_HAND);
                player.setItemInHand(InteractionHand.MAIN_HAND, offHandItem);
                player.setItemInHand(InteractionHand.OFF_HAND, mainHandItem);
                if(target != null)player.attack(target);
                player.swing(InteractionHand.OFF_HAND, true);
                player.setItemInHand(InteractionHand.MAIN_HAND, mainHandItem);
                player.setItemInHand(InteractionHand.OFF_HAND, offHandItem);
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
