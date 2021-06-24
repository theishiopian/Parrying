package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModParticles;
import com.theishiopian.parrying.Registration.ModSoundEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.ProjectileImpactEvent;

import java.util.Random;

public abstract class Deflection
{
    public static void Deflect(ProjectileImpactEvent.Arrow event)
    {
        final AbstractArrowEntity projectile = event.getArrow();

        if(!projectile.level.isClientSide)
        {
            if (!(event.getRayTraceResult() instanceof EntityRayTraceResult))return;
            Entity entity = ((EntityRayTraceResult)event.getRayTraceResult()).getEntity();
            if(event.getEntity() != null && entity instanceof PlayerEntity)
            {
                PlayerEntity player = (PlayerEntity) entity;
                Vector3d playerDir = player.getViewVector(1);
                Vector3d arrowDir = projectile.position().subtract(player.position());
                Vector3d playerDirLevel = new Vector3d(playerDir.x, 0, playerDir.z);
                Vector3d arrowDirLevel = new Vector3d(arrowDir.x, 0, arrowDir.z);
                double angle = playerDirLevel.dot(arrowDirLevel);
                ItemStack held = player.getMainHandItem();

                int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DEFLECTING.get(), held);
                if(level > 0 && player.swinging && angle > 0.5)
                {
                    Random random = new Random();

                    player.causeFoodExhaustion(1f);
                    held.hurtAndBreak(1, player, (playerEntity) ->
                            playerEntity.broadcastBreakEvent(player.getUsedItemHand()));

                    float power = (float)(projectile.getDeltaMovement().normalize().length() / 5f) * level;
                    projectile.setDeltaMovement(playerDir.x * power, playerDir.y * power, playerDir.z * power);
                    projectile.yRot = (float)(MathHelper.atan2(playerDir.x, playerDir.z) * (double)(180F / (float)Math.PI));
                    projectile.xRot = (float)(MathHelper.atan2(playerDir.y, 1) * (double)(180F / (float)Math.PI));
                    projectile.yRotO = projectile.yRot;
                    projectile.xRotO = projectile.xRot;
                    projectile.hasImpulse = true;
                    Vector3d arrowMovement = projectile.getDeltaMovement();
                    projectile.setPos(projectile.getX() + arrowMovement.x, projectile.getY() + arrowMovement.y, projectile.getZ() + arrowMovement.z);


                    player.level.playSound(null, player.blockPosition(), ModSoundEvents.BLOCK_HIT.get(), SoundCategory.PLAYERS, 1, random.nextFloat() * 2f);

                    Vector3d particlePos = projectile.position();
                    ((ServerWorld) player.level).sendParticles(ModParticles.PARRY_PARTICLE.get(), particlePos.x, particlePos.y, particlePos.z, 1, 0D, 0D, 0D, 0.0D);

                    event.setCanceled(true);
                }
            }
        }
    }
}
