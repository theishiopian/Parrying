package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.CoreMod.Mixin.AbstractArrowInvoker;
import com.theishiopian.parrying.Registration.ModItems;
import com.theishiopian.parrying.Registration.ModTriggers;
import com.theishiopian.parrying.Utility.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

import java.util.List;

//used for all active arrow mechanics
public abstract class ArrowMechanics
{
    public static void DoShieldPunching(AbstractArrow arrow, HitResult result)
    {
        if(arrow.getKnockback() >= 2 && result instanceof EntityHitResult hit)
        {
            if(hit.getEntity() instanceof Player target)
            {
                target.disableShield(true);
            }
        }
    }

    public static void DoSnipeChallenge(AbstractArrow arrow, HitResult result)
    {
        if
        (
            arrow.getOwner() instanceof ServerPlayer player &&
            arrow.isNoGravity() &&
            result instanceof EntityHitResult hit &&
            hit.getEntity() instanceof LivingEntity target &&
            BackstabMechanic.CanBackstab(player, target) &&
            player.distanceTo(target) >= 50
        )
        {
            ModTriggers.snipe.trigger(player);
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

    public static void DoZeroGravityBolts(Entity potentialArrow)
    {
        if
        (
                Config.zeroGravityBolts.get() &&
                        potentialArrow instanceof AbstractArrow arrow &&
                        arrow.getOwner() instanceof LivingEntity shooter &&
                        shooter.getMainHandItem().is(ModItems.SCOPED_CROSSBOW.get())
        )
        {
            arrow.setNoGravity(true);
        }
    }

    public static void DoBurningArrow(AbstractArrow arrow, HitResult result)
    {
        if (arrow.isOnFire() && Config.flamingArrowGriefing.get() && result instanceof BlockHitResult hit)
        {
            BlockPos posToIgnite = hit.getBlockPos().relative(hit.getDirection());
            BlockState toBurn = arrow.level.getBlockState(hit.getBlockPos());

            if (toBurn.isFlammable(arrow.level, posToIgnite, hit.getDirection()))
            {
                BlockState fireState = FireBlock.getState(arrow.level, posToIgnite);

                arrow.level.setBlock(posToIgnite, fireState, 11);
            }

            if (arrow.getOwner() instanceof ServerPlayer player && toBurn.is(Blocks.CAMPFIRE) && !toBurn.getValue(CampfireBlock.LIT))
            {
                ModTriggers.campfire.trigger(player);
            }
        }
    }

    public static boolean DoAPBolts(AbstractArrow bolt, LivingEntity target, LivingEntity attacker, float pAmount, float strength)
    {
        int pLevel = bolt.getPierceLevel();

        if(Config.apPiercing.get() && ArmorPenetrationMechanic.IsNotBypassing() && pLevel > 0)
        {
            //it actually will bypass the shield, this is just to trick the helper method
            ArmorPenetrationMechanic.DoAPDamage(pAmount,strength, 0.2f * pLevel, target, attacker, false, "piercing.player");
            //event.setAmount(0);//prevent extra damage
            return false;
            //NOTE: the backstab still applies with this because the damage is applied separately inside DoAPDamage
            //hence the need for a check if the system is doing AP
        }

        return true;
    }

    public static boolean DoPickyPotionProjectiles(AbstractArrow arrow, LivingEntity target)
    {
        if(Config.pickyPotionArrows.get())
        {
            List<MobEffectInstance> list;
            if(arrow instanceof Arrow)
            {
                list = ((Arrow)arrow).potion.getEffects();
            }
            else
            {
                list = PotionUtils.getMobEffects(((AbstractArrowInvoker)arrow).invokeGetPickup());
            }

            return ModUtil.ShouldBeHarmful(list, target);
        }

        return true;
    }
}