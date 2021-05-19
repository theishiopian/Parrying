package com.theishiopian.parrying;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;


public class RayTracer
{
    @Nullable
    public static Entity getFistEntityInLine(World world, Vector3d start, Vector3d end, Entity toIgnore)
    {
        AxisAlignedBB searchVolume = new AxisAlignedBB(start.x, start.y, start.z, end.x, end.y, end.z);

        List<Entity> entities = world.getEntitiesOfClass(LivingEntity.class, searchVolume);

        AxisAlignedBB entityBounds;
        Entity closest = null;

        for(Entity e : entities)
        {
            if(e.equals(toIgnore))continue;
            if(e instanceof  LivingEntity)
            {
                ParryingMod.LOGGER.info(e);
                entityBounds = e.getBoundingBox();

                if(entityBounds.intersects(start, end))
                {
                    if(closest == null || (e.position().subtract(start).length()) < (closest.position().subtract(start).length()))
                    {
                        closest = e;
                    }
                }
            }
        }

        return closest;
    }
}
