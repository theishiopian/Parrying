package com.theishiopian.parrying.Network;

import com.theishiopian.parrying.Mechanics.Bashing;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BashPacket
{
    public void toBytes(PacketBuffer buffer)
    {

    }

    public static BashPacket fromBytes(PacketBuffer buffer)
    {
        return new BashPacket();
    }

    public static void handle(BashPacket packet, Supplier<NetworkEvent.Context> context)
    {
        Bashing.Bash(context.get().getSender());

        context.get().setPacketHandled(true);
    }
}
