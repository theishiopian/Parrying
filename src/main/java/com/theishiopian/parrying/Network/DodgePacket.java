package com.theishiopian.parrying.Network;

import com.theishiopian.parrying.Mechanics.Dodging;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class DodgePacket
{
    //0=none
    //1=left
    //2=back
    //3=right
    public final int direction;

    public void toBytes(PacketBuffer buffer)
    {
        buffer.writeInt(direction);
    }

    public static DodgePacket fromBytes(PacketBuffer buffer)
    {
        return new DodgePacket(buffer.readInt());
    }

    public DodgePacket(int dir)
    {
        direction = dir;
    }

    public static void handle(DodgePacket packet, Supplier<NetworkEvent.Context> context)
    {
        Dodging.Dodge(context.get().getSender(), packet.direction);

        context.get().setPacketHandled(true);
    }
}
