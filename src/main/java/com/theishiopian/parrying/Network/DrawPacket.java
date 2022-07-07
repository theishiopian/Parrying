package com.theishiopian.parrying.Network;

import com.theishiopian.parrying.Items.ScabbardItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Used to tell the server to dodge.
 * Encodes the direction of the dodge using an integer.
 */
public class DrawPacket
{
    public void toBytes(FriendlyByteBuf buffer)
    {

    }

    public static DrawPacket fromBytes(FriendlyByteBuf buffer)
    {
        return new DrawPacket();
    }

    public DrawPacket()
    {

    }

    public static void handle(DrawPacket packet, Supplier<NetworkEvent.Context> context)
    {
        ScabbardItem.DrawSword(Objects.requireNonNull(context.get().getSender()));

        context.get().setPacketHandled(true);
    }
}
