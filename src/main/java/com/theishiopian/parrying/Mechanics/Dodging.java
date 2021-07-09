package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Dodging
{
    public static final Map<UUID, Integer> dodgeCooldown = new HashMap<>();
    public static void Dodge(ServerPlayerEntity player, int direction)
    {
        if(Config.dodgeEnabled.get())
        {
            assert player != null : "If this is null something is horribly wrong";
            if(!player.isFallFlying() && player.isOnGround() && !player.onClimbable() && !player.isInWater() && !player.isCrouching())
            {
                if(dodgeCooldown.containsKey(player.getUUID()))return;

                Vector3d playerDir = player.getViewVector(1);
                Vector3d playerDirLevel = new Vector3d(playerDir.x, 0, playerDir.z);
                playerDirLevel = playerDirLevel.normalize();
                Vector3d cross = playerDirLevel.cross(new Vector3d(0,1,0));

                EffectInstance jumpBoost = player.getEffect(Effects.JUMP.getEffect());

                int level = (jumpBoost == null) ? 0 : jumpBoost.getAmplifier() + 1;

                switch (direction)
                {
                    case 1:
                    {
                        player.knockback((float) (Config.dodgePower.get() + (0.15f * level)), cross.x, cross.z);
                        player.hurtMarked = true;//this makes knockback work
                    }
                    break;

                    case 2:
                    {
                        player.knockback((float) (Config.dodgePower.get() + (0.15f * level)), playerDirLevel.x, playerDirLevel.z);
                        player.hurtMarked = true;//this makes knockback work
                    }
                    break;

                    case 3:
                    {
                        player.knockback((float) (Config.dodgePower.get() + (0.15f * level)), -cross.x, -cross.z);
                        player.hurtMarked = true;//this makes knockback work
                    }
                    break;
                }
                player.causeFoodExhaustion(0.5f);
                dodgeCooldown.put(player.getUUID(), (int)(Config.dodgeCooldown.get() * 120));//replace "2" with config
            }
        }
    }
}
