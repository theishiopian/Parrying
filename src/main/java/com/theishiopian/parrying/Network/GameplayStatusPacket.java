package com.theishiopian.parrying.Network;

import com.theishiopian.parrying.Items.BandolierItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class GameplayStatusPacket
{
    private static class Tracker
    {
        private Tracker(boolean inP, int ticks)
        {
            isPlayInProgress = inP;
            ticksSinceLastChange = ticks;
        }
        private final boolean isPlayInProgress;
        private int ticksSinceLastChange;
    }

    private static final HashMap<UUID, Tracker> playerStates = new HashMap<>();
    public final boolean IsPlayInProgress;
    public void toBytes(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(IsPlayInProgress);
    }

    public static GameplayStatusPacket fromBytes(FriendlyByteBuf buffer)
    {
        return new GameplayStatusPacket(buffer.readBoolean());
    }

    public GameplayStatusPacket(boolean isOpen)
    {
        IsPlayInProgress = isOpen;
    }

    public static void handle(GameplayStatusPacket packet, Supplier<NetworkEvent.Context> context)
    {
        playerStates.put(Objects.requireNonNull(context.get().getSender()).getUUID(), new Tracker(packet.IsPlayInProgress, 0));
    }

    public static boolean isPlayerPlaying(ServerPlayer player)
    {
        if(!playerStates.containsKey(player.getUUID())) return false;
        return playerStates.get(player.getUUID()).isPlayInProgress;
    }

    public static int getTicks(ServerPlayer player)
    {
        if(!playerStates.containsKey(player.getUUID())) return 0;
        return playerStates.get(player.getUUID()).ticksSinceLastChange;
    }

    public static void UpdateTicks()
    {
        playerStates.forEach(
                (uuid, tracker) ->
                {
                    tracker.ticksSinceLastChange++;
                    if(tracker.ticksSinceLastChange > 20) tracker.ticksSinceLastChange = 20;
                }
        );
    }
}
