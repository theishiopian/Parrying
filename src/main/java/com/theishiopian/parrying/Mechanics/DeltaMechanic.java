package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Registration.ModParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.UUID;

public abstract class DeltaMechanic
{
    private static class VelocityTracker
    {
        public Vec3 oldPos;

        //multiply by 20 to get blocks per second
        public double delta;
        public double oldDelta;

        public int ticksSinceLastCollision = 20;
    }

    private static final HashMap<UUID, VelocityTracker> velocityTracker = new HashMap<>();

    public static boolean Contains(Entity entity)
    {
        return velocityTracker.containsKey(entity.getUUID());
    }

    public static void ResetCollision(Entity entity)
    {
        var velocityTracker = DeltaMechanic.velocityTracker.get(entity.getUUID());
        if(velocityTracker == null) return;

        velocityTracker.ticksSinceLastCollision = 20;
    }

    public static void TryCollisionDamage(Entity entity)
    {
        var velocityTracker = DeltaMechanic.velocityTracker.get(entity.getUUID());
        if(velocityTracker == null) return;

        if(velocityTracker.ticksSinceLastCollision < 20)velocityTracker.ticksSinceLastCollision++;

        if (velocityTracker.ticksSinceLastCollision >= 20 && DeltaMechanic.HorizontalCollision(entity))
        {
            var oldSpeed = 20 * DeltaMechanic.GetOldDelta(entity);

            if (oldSpeed > 5f)
            {
                entity.invulnerableTime = 0;
                entity.level.playSound(null, entity.blockPosition(), SoundEvents.PLAYER_SMALL_FALL, SoundSource.NEUTRAL, 2, 1);
                entity.hurt(DamageSource.FLY_INTO_WALL, oldSpeed);
                velocityTracker.ticksSinceLastCollision = 0;
                ((ServerLevel) entity.level).sendParticles(ModParticles.IMPACT_PARTICLE.get(), entity.getX(), entity.getY() + entity.getEyeHeight() / 2, entity.getZ(), 1, 0D, 0D, 0D, 0.0D);
            }
        }
    }

    public static void Add(Entity entity)
    {
        velocityTracker.put(entity.getUUID(), new VelocityTracker());
    }

    public static void Remove(Entity entity)
    {
        velocityTracker.remove(entity.getUUID());
    }

    public static float GetDelta(Entity entity)
    {
        if(entity == null) return 0.0f;
        if(!velocityTracker.containsKey(entity.getUUID())) return 0.0f;
        return (float) velocityTracker.get(entity.getUUID()).delta;
    }

    public static Vec3 GetFlatDeltaVec(Entity entity)
    {
        if(entity == null) return Vec3.ZERO;
        var velocityTracker = DeltaMechanic.velocityTracker.get(entity.getUUID());
        if(velocityTracker == null) return Vec3.ZERO;
        if(velocityTracker.oldPos == null) return Vec3.ZERO;
        return entity.position().multiply(1,0,1).subtract(velocityTracker.oldPos.multiply(1,0,1));
    }

    public static float GetOldDelta(Entity entity)
    {
        if(entity == null) return 0.0f;
        if(!velocityTracker.containsKey(entity.getUUID())) return 0.0f;
        return (float) velocityTracker.get(entity.getUUID()).oldDelta;
    }

    public static void PreTick(Entity entity)
    {
        VelocityTracker tracker = velocityTracker.get(entity.getUUID());

        if(tracker == null) return;

        tracker.oldDelta = tracker.delta;

        if(tracker.oldPos == null && entity instanceof Player) tracker.oldPos = entity.position();
    }

    public static void PostTick(Entity entity)
    {
        VelocityTracker tracker = velocityTracker.get(entity.getUUID());

        if(tracker == null) return;

        if(entity instanceof Player)
        {
            tracker.delta = entity.position().subtract(tracker.oldPos).horizontalDistance();
            tracker.oldPos = entity.position();
        }
        else
        {
            tracker.delta = entity.getDeltaMovement().horizontalDistance();
        }
    }

    public static boolean HorizontalCollision(Entity entity)
    {
        var motion = entity instanceof Player ? GetFlatDeltaVec(entity) : entity.getDeltaMovement();
        var collisionMotion = entity.collide(motion);

        return collisionMotion.x != motion.x || collisionMotion.z != motion.z;
    }
}
