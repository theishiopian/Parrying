package com.theishiopian.parrying.Network;

import com.theishiopian.parrying.Mechanics.DualWielding;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * this class defines a packet that is used
 * to tell the server to do a dual wield attack
 */
public class SwingPacket
{
    public final boolean mainHand;

    public void toBytes(PacketBuffer buffer){buffer.writeBoolean(mainHand);}

    public static SwingPacket fromBytes(PacketBuffer buffer){return new SwingPacket(buffer.readBoolean());}

    public SwingPacket(boolean mainHand)
    {
        this.mainHand = mainHand;
    }

    public static void handle(SwingPacket packet, Supplier<NetworkEvent.Context> context)
    {
        DualWielding.DoDualWield(context.get().getSender(), packet.mainHand ? Hand.MAIN_HAND : Hand.OFF_HAND);

        context.get().setPacketHandled(true);
    }
}
