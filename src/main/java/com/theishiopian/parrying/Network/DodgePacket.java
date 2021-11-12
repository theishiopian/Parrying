package com.theishiopian.parrying.Network;

import com.theishiopian.parrying.Mechanics.Dodging;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Used to tell the server to dodge.
 * Encodes the direction of the dodge using an integer.
 */
public class DodgePacket
{
    public final boolean left;
    public final boolean right;
    public final boolean back;

    public void toBytes(PacketBuffer buffer)
    {
        buffer.writeBoolean(left);
        buffer.writeBoolean(right);
        buffer.writeBoolean(back);
    }

    public static DodgePacket fromBytes(PacketBuffer buffer)
    {
        return new DodgePacket(buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    }

    public DodgePacket(boolean l, boolean r, boolean b)
    {
        left = l;
        right = r;
        back = b;
    }

    public static void handle(DodgePacket packet, Supplier<NetworkEvent.Context> context)
    {
        Dodging.Dodge(context.get().getSender(), packet.left, packet.right, packet.back);

        context.get().setPacketHandled(true);
    }
}
