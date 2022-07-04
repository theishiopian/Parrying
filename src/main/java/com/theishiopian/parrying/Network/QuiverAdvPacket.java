package com.theishiopian.parrying.Network;

import com.theishiopian.parrying.Registration.ModTriggers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Used to tell the server to dodge.
 * Encodes the direction of the dodge using an integer.
 */
public class QuiverAdvPacket
{
    public void toBytes(FriendlyByteBuf buffer)
    {

    }

    public static QuiverAdvPacket fromBytes(FriendlyByteBuf buffer)
    {
        return new QuiverAdvPacket();
    }

    public QuiverAdvPacket()
    {

    }

    public static void handle(QuiverAdvPacket packet, Supplier<NetworkEvent.Context> context)
    {
        ServerPlayer player = context.get().getSender();

        //Debug.log("received");
        ModTriggers.quiver_over_stack.trigger(player);

        context.get().setPacketHandled(true);
    }
}
