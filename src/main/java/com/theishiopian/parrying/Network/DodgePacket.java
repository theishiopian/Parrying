package com.theishiopian.parrying.Network;

import com.theishiopian.parrying.Mechanics.DodgingMechanic;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

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

    public void toBytes(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(left);
        buffer.writeBoolean(right);
        buffer.writeBoolean(back);
    }

    public static DodgePacket fromBytes(FriendlyByteBuf buffer)
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
        DodgingMechanic.Dodge(context.get().getSender(), packet.left, packet.right, packet.back);

        context.get().setPacketHandled(true);
    }
}
