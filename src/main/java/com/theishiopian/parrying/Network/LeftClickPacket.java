package com.theishiopian.parrying.Network;

import com.theishiopian.parrying.Items.FlailItem;
import com.theishiopian.parrying.Mechanics.Bashing;
import com.theishiopian.parrying.Mechanics.DualWielding;
import com.theishiopian.parrying.Registration.ModSoundEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Used to tell the server a left click happened. Used to bypass the normal attack logic.
 * This is necessary because Minecraft disables the normal attack logic while a shield is raised,
 * as such this is needed to perform a shield bash. This also means we cannot use the attack packet for this, sadly.
 *
 * Also used for the flail sound.
 */
public class LeftClickPacket
{
    @SuppressWarnings("EmptyMethod")
    public void toBytes(FriendlyByteBuf buffer)
    {
        //needed by the implementation
    }

    public static LeftClickPacket fromBytes(FriendlyByteBuf buffer)
    {
        return new LeftClickPacket();
    }

    public static void handle(LeftClickPacket packet, Supplier<NetworkEvent.Context> context)
    {
        ServerPlayer player = context.get().getSender();
        assert player != null;//how would this be null?
        Bashing.Bash(player);

        if(DualWielding.IsDualWielding(player))
        {
            InteractionHand hand = DualWielding.dualWielders.get(player.getUUID());

            //DON'T ASK
            if(hand != InteractionHand.MAIN_HAND && player.getMainHandItem().getItem() instanceof FlailItem)
            {
                PlaySound(player);
            }

            if(hand != InteractionHand.OFF_HAND && player.getOffhandItem().getItem() instanceof FlailItem)
            {
                PlaySound(player);
            }
        }
        else if(player.getMainHandItem().getItem() instanceof FlailItem)
        {
            PlaySound(player);
        }



        context.get().setPacketHandled(true);
    }

    private static void PlaySound(Player player)
    {
        player.level.playSound(null, player.blockPosition(), ModSoundEvents.FLAIL_SWING.get(), SoundSource.PLAYERS, 1, 1);
        //player.level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1, 1);
    }
}
