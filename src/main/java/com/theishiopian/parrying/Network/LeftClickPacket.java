package com.theishiopian.parrying.Network;

import com.theishiopian.parrying.Items.FlailItem;
import com.theishiopian.parrying.Mechanics.Bashing;
import com.theishiopian.parrying.Registration.ModSoundEvents;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class LeftClickPacket
{
    @SuppressWarnings("EmptyMethod")
    public void toBytes(PacketBuffer buffer)
    {

    }

    public static LeftClickPacket fromBytes(PacketBuffer buffer)
    {
        return new LeftClickPacket();
    }

    public static void handle(LeftClickPacket packet, Supplier<NetworkEvent.Context> context)
    {
        ServerPlayerEntity player = context.get().getSender();
        assert player != null;//how would this be null?
        Bashing.Bash(player);

        if(player.getMainHandItem().getItem() instanceof FlailItem)
        {
            player.level.playSound(null, player.blockPosition(), ModSoundEvents.FLAIL_SWING.get(), SoundCategory.PLAYERS, 1, 1);
            player.level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1, 1);
        }

        context.get().setPacketHandled(true);
    }
}
