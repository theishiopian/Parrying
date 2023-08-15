package com.theishiopian.parrying.Network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Used to sync fireballs, because they can't handle their own business...
 */
public record SyncFireballPacket(int id, double xPower, double yPower, double zPower)
{
    public void toBytes(FriendlyByteBuf buffer)
    {
        buffer.writeInt(id);
        buffer.writeDouble(xPower);
        buffer.writeDouble(yPower);
        buffer.writeDouble(zPower);
    }

    public static SyncFireballPacket fromBytes(FriendlyByteBuf buffer)
    {
        return new SyncFireballPacket(buffer.readInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public static void handle(SyncFireballPacket packet, Supplier<NetworkEvent.Context> context)
    {

        context.get().enqueueWork
                (() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                {
                    var level = Minecraft.getInstance().level;
                    if(level == null) return;

                    if(level.getEntity(packet.id) instanceof AbstractHurtingProjectile fireball)
                    {
                        fireball.xPower = packet.xPower;
                        fireball.yPower = packet.yPower;
                        fireball.zPower = packet.zPower;
                    }
                }));
        context.get().setPacketHandled(true);
    }
}
