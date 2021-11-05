package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModParticles;
import com.theishiopian.parrying.Registration.ModSoundEvents;
import com.theishiopian.parrying.Utility.ParryModUtil;
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

/**
 * This class holds the code for the deflection mechanic, which is currently only used for the deflecting enchantment.
 * May want to move this code in the future. Will need to move deflection enchant out of this code to do this.
 * Works on any arrow or arrow like entity, such as tridents.
 */
public abstract class Deflection
{
    public static boolean Deflect(ProjectileImpactEvent.Arrow event)
    {
        if(Config.deflectionEnchantEnabled.get())return false;

        final AbstractArrowEntity projectile = event.getArrow();//get our projectile

        //make sure we are on the server and the projectile hit an entity
        if(!projectile.level.isClientSide && event.getRayTraceResult() instanceof EntityRayTraceResult)
        {
            Entity entity = ((EntityRayTraceResult)event.getRayTraceResult()).getEntity();
            if(event.getEntity() != null && entity instanceof PlayerEntity)
            {
                PlayerEntity player = (PlayerEntity) entity;//the player deflecting
                Vector3d playerLookDir = player.getViewVector(1);//the direction the player is looking
                Vector3d arrowDir = (projectile.position().subtract(player.position())).normalize();//the direction to the arrow
                playerLookDir.subtract(0,playerLookDir.y,0);//remove y difference
                arrowDir.subtract(0,arrowDir.y,0);//remove y difference
                double angle = playerLookDir.dot(arrowDir);//get angle between player and projectile
                ItemStack held = player.getMainHandItem();//get player item

                int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DEFLECTING.get(), held);//deflecting enchant level

                if(level > 0 && player.swinging && angle > 0.5)
                {
                    player.causeFoodExhaustion(1f);//exhaust player

                    //hurt item used
                    held.hurtAndBreak(1, player, (playerEntity) ->
                            playerEntity.broadcastBreakEvent(player.getUsedItemHand()));

                    float power = (float)(projectile.getDeltaMovement().length() / 5f) * level;//get power to deflect with
                    projectile.setDeltaMovement(playerLookDir.x * power, playerLookDir.y * power, playerLookDir.z * power);//set projectile speed

                    //set projectile rotation
                    projectile.yRot = (float)(MathHelper.atan2(playerLookDir.x, playerLookDir.z) * (double)(180F / (float)Math.PI));
                    projectile.xRot = (float)(MathHelper.atan2(playerLookDir.y, 1) * (double)(180F / (float)Math.PI));
                    projectile.yRotO = projectile.yRot;
                    projectile.xRotO = projectile.xRot;

                    //mark projectile for velocity change
                    projectile.hasImpulse = true;

                    //move arrow one tick, prevents bugs with some arrows (thanks vemerion)
                    Vector3d arrowMovement = projectile.getDeltaMovement();
                    projectile.setPos(projectile.getX() + arrowMovement.x, projectile.getY() + arrowMovement.y, projectile.getZ() + arrowMovement.z);

                    //play parry noise and spawn particle
                    player.level.playSound(null, player.blockPosition(), ModSoundEvents.BLOCK_HIT.get(), SoundCategory.PLAYERS, 1, ParryModUtil.random.nextFloat() * 2f);
                    Vector3d particlePos = projectile.position();
                    ((ServerWorld) player.level).sendParticles(ModParticles.PARRY_PARTICLE.get(), particlePos.x, particlePos.y, particlePos.z, 1, 0D, 0D, 0D, 0.0D);

                    //cancel hit logic
                    event.setCanceled(true);
                    return true;
                }
            }
        }

        return false;
    }
}
