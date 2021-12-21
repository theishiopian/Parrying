package com.theishiopian.parrying.Network;

import com.theishiopian.parrying.Mechanics.DualWielding;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * This class defines a packet that is used
 * to tell the server to do a dual wield attack.
 */
public record SwingPacket(boolean mainHand)
{
    public void toBytes(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(mainHand);
    }

    public static SwingPacket fromBytes(FriendlyByteBuf buffer)
    {
        return new SwingPacket(buffer.readBoolean());
    }

    public static void handle(SwingPacket packet, Supplier<NetworkEvent.Context> context)
    {
        //Debug.log("packet received from: " + context.get().getSender());
        DualWielding.DoDualWield(Objects.requireNonNull(context.get().getSender()), packet.mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);

        context.get().setPacketHandled(true);
    }
}