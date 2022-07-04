package com.theishiopian.parrying.Network;

import com.theishiopian.parrying.Items.ScabbardItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

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
        Player player = context.get().getSender();

        assert player != null;
        ScabbardItem.DrawSword(player);

        context.get().setPacketHandled(true);
    }
}
