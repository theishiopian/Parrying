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
public class ScabbardPacket
{
    public void toBytes(FriendlyByteBuf buffer)
    {

    }

    public static ScabbardPacket fromBytes(FriendlyByteBuf buffer)
    {
        return new ScabbardPacket();
    }

    public ScabbardPacket()
    {

    }

    public static void handle(ScabbardPacket packet, Supplier<NetworkEvent.Context> context)
    {
        ScabbardItem.SheatheOrDrawSword(Objects.requireNonNull(context.get().getSender()));

        context.get().setPacketHandled(true);
    }
}
