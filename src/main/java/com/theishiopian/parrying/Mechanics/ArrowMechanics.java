package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Advancement.ModTriggers;
import com.theishiopian.parrying.Config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

import java.util.List;

//used for all active arrow mechanics
public abstract class ArrowMechanics
{
    public static void DoSnipeChallenge(AbstractArrow arrow, HitResult result)
    {
        if
        (
            arrow.getOwner() instanceof ServerPlayer player &&
            arrow.isNoGravity() &&
            result instanceof EntityHitResult hit &&
            hit.getEntity() instanceof LivingEntity target &&
            Backstab.CanBackstab(player, target) &&
            player.distanceTo(target) >= 50
        )
        {
            ModTriggers.snipe.trigger(ModTriggers.snipe.getId().toString(), player);
        }
    }

    public static void DoSonicArrow(AbstractArrow arrow)
    {
        if(arrow instanceof SpectralArrow && Config.sonicSpectralArrow.get())
        {
            Vec3 pos = arrow.position();
            List<LivingEntity> entities = arrow.level.getEntitiesOfClass(LivingEntity.class, new AABB(pos.x + 5, pos.y+5, pos.z+5, pos.x-5, pos.y-5, pos.z - 5));

            for(LivingEntity entity : entities)
            {
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100));
            }
        }
    }

    public static void DoBurningArrow(AbstractArrow arrow, HitResult result)
    {
        if(arrow.isOnFire() && Config.flamingArrowGriefing.get() && result instanceof BlockHitResult hit)
        {
            BlockPos posToIgnite = hit.getBlockPos().relative(hit.getDirection());
            BlockState toBurn = arrow.level.getBlockState(hit.getBlockPos());

            if(toBurn.isFlammable(arrow.level, posToIgnite, hit.getDirection()))
            {
                BlockState fireState = FireBlock.getState(arrow.level, posToIgnite);

                arrow.level.setBlock(posToIgnite, fireState, 11);
            }

            if(arrow.getOwner() instanceof ServerPlayer player && toBurn.is(Blocks.CAMPFIRE) && !toBurn.getValue(CampfireBlock.LIT))
            {
                ModTriggers.campfire.trigger(ModTriggers.campfire.getId().toString(), player);
            }
        }
    }
}