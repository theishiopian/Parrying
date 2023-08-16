package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Network.SyncFireballPacket;
import com.theishiopian.parrying.ParryingMod;
import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModParticles;
import com.theishiopian.parrying.Registration.ModTags;
import com.theishiopian.parrying.Registration.ModTriggers;
import com.theishiopian.parrying.Utility.ModUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.network.PacketDistributor;

/**
 * This class holds the code for the deflection mechanic, which is currently only used for the deflecting enchantment.
 * May want to move this code in the future. Will need to move deflection enchant out of this code to do this.
 * Works on any projectile or projectile like entity, such as tridents.
 */
public abstract class DeflectionMechanic
{
    public static boolean Deflect(ProjectileImpactEvent event)
    {
        if(!Config.deflectionEnchantEnabled.get()) return false;

        Projectile projectile = event.getProjectile();

        //make sure we are on the server and the projectile hit an entity
        if(!projectile.level.isClientSide && event.getRayTraceResult() instanceof EntityHitResult && !projectile.getType().is(ModTags.NON_DEFLECTABLE))
        {
            Entity entity = ((EntityHitResult)event.getRayTraceResult()).getEntity();
            if(event.getEntity() != null && entity instanceof ServerPlayer player)
            {
                //the player deflecting
                Vec3 playerLookDir = player.getViewVector(1);//the direction the player is looking
                Vec3 projectileDir = (projectile.position().subtract(player.position())).normalize();//the direction to the projectile
                playerLookDir.subtract(0,playerLookDir.y,0);//remove y difference
                projectileDir.subtract(0,projectileDir.y,0);//remove y difference
                double angle = playerLookDir.dot(projectileDir);//get angle between player and projectile
                ItemStack mainHandItem = player.getMainHandItem();//get player item
                //ItemStack offHandItem = player.getOffhandItem();//get player item

                int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DEFLECTING.get(), mainHandItem);

                if(level > 0 && player.swinging && angle > 0.5)
                {
                    player.causeFoodExhaustion(1f);//exhaust player

                    //hurt item used
                    mainHandItem.hurtAndBreak(1, player, (playerEntity) ->
                            playerEntity.broadcastBreakEvent(InteractionHand.MAIN_HAND));

                    float power = (float)(projectile.getDeltaMovement().length() / 5f) * level;//get power to deflect with

                    projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, power, 0);//shoot projectile back at attacker

                    if (projectile instanceof AbstractHurtingProjectile fireball)
                    {
                        fireball.xPower = playerLookDir.x * 0.1D;
                        fireball.yPower = playerLookDir.y * 0.1D;
                        fireball.zPower = playerLookDir.z * 0.1D;

                        //Debug.log("Fireball deflected on server: " + fireball.xPower + " " + fireball.yPower + " " + fireball.zPower);

                        ParryingMod.channel.send(PacketDistributor.ALL.noArg(), new SyncFireballPacket(fireball.getId(), fireball.xPower, fireball.yPower, fireball.zPower));
                    }

                    projectile.hasImpulse = true;

                    //move projectile one tick, prevents bugs with some projectiles (thanks vemerion)
                    Vec3 projectileMovement = projectile.getDeltaMovement();
                    projectile.setPos(projectile.getX() + projectileMovement.x, projectile.getY() + projectileMovement.y, projectile.getZ() + projectileMovement.z);

                    //play parry noise and spawn particle
                    player.level.playSound(null, player.blockPosition(), ParryingMechanic.GetMaterialParrySound(mainHandItem.getItem()), SoundSource.PLAYERS, 1, ModUtil.random.nextFloat() * 2f);
                    Vec3 particlePos = projectile.position();
                    ((ServerLevel) player.level).sendParticles(ModParticles.PARRY_PARTICLE.get(), particlePos.x, particlePos.y, particlePos.z, 1, 0D, 0D, 0D, 0.0D);

                    ModTriggers.deflect.trigger(player);
                    if(projectile instanceof ThrownTrident)ModTriggers.deflectTrident.trigger(player);

                    //cancel hit logic
                    event.setCanceled(true);
                    return true;
                }
            }
        }

        return false;
    }
}
