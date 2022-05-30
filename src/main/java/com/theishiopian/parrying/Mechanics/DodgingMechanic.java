package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class DodgingMechanic
{
    public static final Map<UUID, Integer> dodgeCooldown = new HashMap<>();
    public static void Dodge(ServerPlayer player, boolean left, boolean right, boolean back)
    {
        if(Config.dodgeEnabled.get())
        {
            assert player != null : "If this is null something is horribly wrong";
            if(!player.isFallFlying() && player.isOnGround() && !player.onClimbable() && !player.isInWater() && !player.isCrouching())
            {
                if(dodgeCooldown.containsKey(player.getUUID()))return;

                Vec3 playerDir = player.getViewVector(1);
                Vec3 playerDirLevel = new Vec3(playerDir.x, 0, playerDir.z);
                playerDirLevel = playerDirLevel.normalize();
                Vec3 cross = playerDirLevel.cross(new Vec3(0,1,0));

                MobEffectInstance jumpBoost = player.getEffect(MobEffects.JUMP);

                Vec3 dir = playerDir.scale(back ? 1 : 0).add(cross.scale(left ? 1 : 0).add(cross.scale(right ? -1 : 0)));

                int level = (jumpBoost == null) ? 0 : jumpBoost.getAmplifier() + 1;

                player.knockback((float)(Config.dodgePower.get() + (0.15f * level)),  dir.x, dir.z);
                player.hurtMarked = true;

                player.causeFoodExhaustion(0.5f);
                dodgeCooldown.put(player.getUUID(), (int)(Config.dodgeCooldown.get() * 120));
            }
        }
    }
}
