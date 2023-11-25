package com.theishiopian.parrying.Mechanics;

import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.UUID;

public abstract class DeltaPositionMechanic
{
    public static class VelocityTracker
    {
        public Vec3 oldPos;

        //multiply by 20 to get blocks per second
        public double deltaPosition;
        public double oldDeltaPosition;
    }
    public static HashMap<UUID, VelocityTracker> velocityTracker = new HashMap<>();
}
