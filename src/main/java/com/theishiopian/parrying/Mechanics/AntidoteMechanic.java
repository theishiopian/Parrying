package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Effects.CoalescenceEffect;
import com.theishiopian.parrying.Registration.ModEffects;
import com.theishiopian.parrying.Registration.ModSoundEvents;
import com.theishiopian.parrying.Registration.ModTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public abstract class AntidoteMechanic
{
    //TODO let people set this via config or data pack
    private static final Map<MobEffect, MobEffect> antidotes = new HashMap<>()
    {
        {put(MobEffects.POISON, MobEffects.REGENERATION);}
        {put(MobEffects.REGENERATION, MobEffects.POISON);}
        {put(MobEffects.MOVEMENT_SPEED, MobEffects.MOVEMENT_SLOWDOWN);}
        {put(MobEffects.MOVEMENT_SLOWDOWN, MobEffects.MOVEMENT_SPEED);}
        {put(MobEffects.BLINDNESS, MobEffects.NIGHT_VISION);}
        {put(MobEffects.NIGHT_VISION, MobEffects.BLINDNESS);}
        {put(MobEffects.DAMAGE_BOOST, MobEffects.WEAKNESS);}
        {put(MobEffects.WEAKNESS, MobEffects.DAMAGE_BOOST);}
        {put(MobEffects.INVISIBILITY, MobEffects.GLOWING);}
        {put(MobEffects.GLOWING, MobEffects.INVISIBILITY);}
        {put(MobEffects.JUMP, MobEffects.SLOW_FALLING);}
        {put(MobEffects.SLOW_FALLING, MobEffects.JUMP);}
        {put(MobEffects.WATER_BREATHING, MobEffects.FIRE_RESISTANCE);}
        {put(MobEffects.FIRE_RESISTANCE, MobEffects.WATER_BREATHING);}
    };

    public static boolean DoAntidoteCheck(LivingEntity entity, MobEffectInstance effect)
    {
        if(entity.hasEffect(ModEffects.COALESCENCE.get()) || effect.getEffect() instanceof CoalescenceEffect)
        {
            if(entity instanceof ServerPlayer player) ModTriggers.surrender.trigger(player);
        }
        else
        {
            //ANTIDOTES
            MobEffect incoming = effect.getEffect();
            MobEffect opposite = antidotes.getOrDefault(incoming, null);

            if(opposite != null && entity.hasEffect(opposite))
            {
                int reduction = effect.getAmplifier() + 1;

                MobEffectInstance i = entity.getEffect(opposite);
                entity.removeEffect(opposite);

                assert i != null;
                int newLevel = ((i.getAmplifier() + 1) - reduction);
                if(newLevel > 0)entity.addEffect(new MobEffectInstance(i.getEffect(), i.getDuration(), newLevel - 1));

                if(newLevel < 0)
                {
                    entity.addEffect(new MobEffectInstance(incoming, effect.getDuration(), -1 * (newLevel + 1)));
                }

                entity.level.playSound(null, entity.blockPosition(), ModSoundEvents.CLEANSE.get(), SoundSource.PLAYERS, 0.4F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.2F);
                return false;
            }
        }

        return true;
    }
}
