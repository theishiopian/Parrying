package com.theishiopian.parrying.Network;

import com.theishiopian.parrying.Mechanics.Parrying;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Used to sync the current defense value. fired frequently
 */
public record SyncDefPacket(float value)
{
    public void toBytes(FriendlyByteBuf buffer)
    {
        buffer.writeFloat(value);
    }

    public static SyncDefPacket fromBytes(FriendlyByteBuf buffer)
    {
        return new SyncDefPacket(buffer.readFloat());
    }

    public static void handle(SyncDefPacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork
        (() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Parrying.ClientDefense = packet.value));
        context.get().setPacketHandled(true);
    }
}
