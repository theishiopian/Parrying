package com.theishiopian.parrying.Network;

import com.theishiopian.parrying.Mechanics.DualWieldingMechanic;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * This class defines a packet that is used
 * to tell the server to do a dual wield attack.
 */
public record DualWieldPacket(boolean mainHand, int id)
{
    public void toBytes(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(mainHand);
        buffer.writeInt(id);
    }

    public static DualWieldPacket fromBytes(FriendlyByteBuf buffer)
    {
        return new DualWieldPacket(buffer.readBoolean(), buffer.readInt());
    }

    public static void handle(DualWieldPacket packet, Supplier<NetworkEvent.Context> context)
    {
        ServerPlayer sender = context.get().getSender();
        assert sender != null : "Sender is null in dual wield packet, this is a serious problem!";
        Level level = sender.level;
        DualWieldingMechanic.DoDualWield(Objects.requireNonNull(sender), level.getEntity(packet.id), packet.mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);

        context.get().setPacketHandled(true);
    }
}