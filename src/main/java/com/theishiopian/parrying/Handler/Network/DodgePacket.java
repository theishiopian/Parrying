package com.theishiopian.parrying.Handler.Network;

import com.ibm.icu.impl.coll.UVector32;
import com.theishiopian.parrying.Registration.ModEffects;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class DodgePacket
{
    //0=none
    //1=left
    //2=back
    //3=right
    public int direction = 0;

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

    private static final Map<UUID, Integer> dodgeCooldown = new HashMap<>();

    public static void handle(DodgePacket packet, Supplier<NetworkEvent.Context> context)
    {
        boolean canDodge = false;
        ServerPlayerEntity player = context.get().getSender();

        if(!player.isFallFlying() && player.isOnGround())
        {
            if(dodgeCooldown.containsKey(player.getUUID()))return;

            Vector3d playerDir = player.getViewVector(1);
            Vector3d playerDirLevel = new Vector3d(playerDir.x, 0, playerDir.z);
            playerDirLevel = playerDirLevel.normalize();
            Vector3d cross = playerDirLevel.cross(new Vector3d(0,1,0));

            EffectInstance jumpBoost = player.getEffect(Effects.JUMP.getEffect());

            int level = (jumpBoost == null) ? 0 : jumpBoost.getAmplifier() + 1;

            switch (packet.direction)
            {
                case 1:
                {
                    player.knockback(0.5f + (0.15f * level), cross.x, cross.z);
                    player.hurtMarked = true;//this makes knockback work
                }
                break;

                case 2:
                {
                    player.knockback(0.5f + (0.15f * level), playerDirLevel.x, playerDirLevel.z);
                    player.hurtMarked = true;//this makes knockback work
                }
                break;

                case 3:
                {
                    player.knockback(0.5f + (0.15f * level), -cross.x, -cross.z);
                    player.hurtMarked = true;//this makes knockback work
                }
                break;
            }

            dodgeCooldown.put(player.getUUID(), 2 * 120);//replace "2" with config
        }
    }

    //used for cooldowns
    // todo: move to class if needed
    public static void OnWorldTick(TickEvent.WorldTickEvent event)
    {
        if(event.world.isClientSide)return;

        dodgeCooldown.replaceAll((k, v) -> v - 1);
        dodgeCooldown.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }
}
