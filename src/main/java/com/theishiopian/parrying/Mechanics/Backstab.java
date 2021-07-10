package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.ModParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public abstract class Backstab
{
    public static boolean DoBackstab(LivingHurtEvent event, LivingEntity entity)
    {
        boolean stab = false;
        if(Config.backStabEnabled.get())
        {
            Entity attacker = event.getSource().getEntity();

            if(attacker instanceof LivingEntity && entity.getMaxHealth() <= Config.backStabMaxHealth.get())
            {
                Vector3d attackerDir = attacker.getViewVector(1);
                Vector3d defenderDir = entity.getViewVector(1);

                double angle = (new Vector3d(attackerDir.x, 0, attackerDir.z)).dot(new Vector3d(defenderDir.x, 0, defenderDir.z));

                if(angle > Config.backStabAngle.get())
                {
                    event.setAmount((float) (event.getAmount() * Config.backStabDamageMultiplier.get()));

                    Vector3d pos = entity.position();

                    ((ServerWorld) attacker.level).sendParticles(ModParticles.STAB_PARTICLE.get(), pos.x, pos.y+1.5f, pos.z, 1, 0D, 0D, 0D, 0.0D);
                    attacker.level.playSound(null, attacker.blockPosition(), SoundEvents.PLAYER_BIG_FALL, SoundCategory.PLAYERS, 2, 1);
                    stab = true;
                }
            }
        }
        return stab;
    }
}
