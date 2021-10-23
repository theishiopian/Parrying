package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.ModTriggers;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;

//used for all active arrow mechanics
public abstract class Arrows
{
    public static void DoSonicArrow(AbstractArrowEntity arrow)
    {
        if(arrow instanceof SpectralArrowEntity && Config.sonicSpectralArrow.get())
        {
            Vector3d pos = arrow.position();
            List<LivingEntity> entities = arrow.level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(pos.x + 5, pos.y+5, pos.z+5, pos.x-5, pos.y-5, pos.z - 5));

            for(LivingEntity entity : entities)
            {
                entity.addEffect(new EffectInstance(Effects.GLOWING, 100));
            }
        }
    }

    public static void DoBurningArrow(AbstractArrowEntity arrow, RayTraceResult result)
    {
        if(arrow.isOnFire() && Config.flamingArrowGriefing.get() && result instanceof BlockRayTraceResult)
        {
            BlockRayTraceResult hit = ((BlockRayTraceResult)result);
            BlockPos posToIgnite = hit.getBlockPos().relative(hit.getDirection());
            BlockState toBurn = arrow.level.getBlockState(hit.getBlockPos());

            if(toBurn.isFlammable(arrow.level, posToIgnite, hit.getDirection()))
            {
                BlockState fireState = AbstractFireBlock.getState(arrow.level, posToIgnite);

                arrow.level.setBlock(posToIgnite, fireState, 11);
            }

            if(arrow.getOwner() instanceof ServerPlayerEntity && toBurn.is(Blocks.CAMPFIRE) && !toBurn.getValue(CampfireBlock.LIT))
            {
                ModTriggers.campfireLight.trigger((ServerPlayerEntity) arrow.getOwner());
            }
        }
    }
}