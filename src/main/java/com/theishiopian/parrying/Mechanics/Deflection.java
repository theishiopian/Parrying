package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModParticles;
import com.theishiopian.parrying.Registration.ModTriggers;
import com.theishiopian.parrying.Utility.ModUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;

/**
 * This class holds the code for the deflection mechanic, which is currently only used for the deflecting enchantment.
 * May want to move this code in the future. Will need to move deflection enchant out of this code to do this.
 * Works on any arrow or arrow like entity, such as tridents.
 */
public abstract class Deflection
{
    public static boolean Deflect(ProjectileImpactEvent event)
    {
        if(!Config.deflectionEnchantEnabled.get() || !(event.getProjectile() instanceof final AbstractArrow projectile))return false;

        //make sure we are on the server and the projectile hit an entity
        if(!projectile.level.isClientSide && event.getRayTraceResult() instanceof EntityHitResult)
        {
            Entity entity = ((EntityHitResult)event.getRayTraceResult()).getEntity();
            if(event.getEntity() != null && entity instanceof Player player)
            {
                //the player deflecting
                Vec3 playerLookDir = player.getViewVector(1);//the direction the player is looking
                Vec3 arrowDir = (projectile.position().subtract(player.position())).normalize();//the direction to the arrow
                playerLookDir.subtract(0,playerLookDir.y,0);//remove y difference
                arrowDir.subtract(0,arrowDir.y,0);//remove y difference
                double angle = playerLookDir.dot(arrowDir);//get angle between player and projectile
                ItemStack mainHandItem = player.getMainHandItem();//get player item
                ItemStack offHandItem = player.getOffhandItem();//get player item

                int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DEFLECTING.get(), mainHandItem);


                if(level > 0 && player.swinging && angle > 0.5)
                {
                    player.causeFoodExhaustion(1f);//exhaust player

                    //hurt item used
                    mainHandItem.hurtAndBreak(1, player, (playerEntity) ->
                            playerEntity.broadcastBreakEvent(InteractionHand.MAIN_HAND));

                    float power = (float)(projectile.getDeltaMovement().length() / 5f) * level;//get power to deflect with
                    projectile.setDeltaMovement(playerLookDir.x * power, playerLookDir.y * power, playerLookDir.z * power);//set projectile speed

                    //set projectile rotation
                    projectile.setYRot((float)(Math.atan2(playerLookDir.x, playerLookDir.z) * (double)(180F / (float)Math.PI)));
                    projectile.setXRot((float)(Math.atan2(playerLookDir.y, 1) * (double)(180F / (float)Math.PI)));
                    projectile.yRotO = projectile.getYRot();
                    projectile.xRotO = projectile.getXRot();

                    //mark projectile for velocity change
                    projectile.hasImpulse = true;

                    //move arrow one tick, prevents bugs with some arrows (thanks vemerion)
                    Vec3 arrowMovement = projectile.getDeltaMovement();
                    projectile.setPos(projectile.getX() + arrowMovement.x, projectile.getY() + arrowMovement.y, projectile.getZ() + arrowMovement.z);

                    //play parry noise and spawn particle
                    player.level.playSound(null, player.blockPosition(), ParryingMechanic.GetMaterialParrySound(mainHandItem.getItem()), SoundSource.PLAYERS, 1, ModUtil.random.nextFloat() * 2f);
                    Vec3 particlePos = projectile.position();
                    ((ServerLevel) player.level).sendParticles(ModParticles.PARRY_PARTICLE.get(), particlePos.x, particlePos.y, particlePos.z, 1, 0D, 0D, 0D, 0.0D);


                    ModTriggers.deflect.trigger((ServerPlayer) player);
                    if(projectile instanceof ThrownTrident)ModTriggers.deflectTrident.trigger((ServerPlayer) player);

                    //cancel hit logic
                    event.setCanceled(true);
                    return true;
                }
            }
        }

        return false;
    }
}
