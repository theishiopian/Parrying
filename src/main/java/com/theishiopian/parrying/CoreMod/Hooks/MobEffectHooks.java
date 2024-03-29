package com.theishiopian.parrying.CoreMod.Hooks;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.ModDamageSources;
import com.theishiopian.parrying.Registration.ModEffects;
import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Optional;

public abstract class MobEffectHooks
{
    public static Optional<Float> ModifyInstantDamage(float damage, LivingEntity livingEntity)
    {
        if(!Config.splashProtectionEnabled.get()) return Optional.empty();

        var armor = livingEntity.getArmorSlots();
        var totalLvl = 0;

        for(var slot : armor)
        {
            totalLvl += EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SPLASH_PROTECTION.get(), slot);
        }

        return Optional.of(Math.max(0, damage - (int) (damage * 0.05f * totalLvl)));
    }

    public static Optional<Boolean> BuffRegen(MobEffect effect, int pDuration, int pAmplifier)
    {
        if(Config.buffRegen.get() && effect == MobEffects.REGENERATION)
        {
            return Optional.of(pDuration % 20 == 0);
        }

        return Optional.empty();
    }

    public static void ModifyRegenValue(MobEffect effect, int pAmplifier, LivingEntity pLivingEntity)
    {
        if(!Config.buffRegen.get()) return;

        if(effect == MobEffects.REGENERATION)
        {
            pLivingEntity.heal(pAmplifier + 1);
        }
    }

    public static int ModifyPoisonLethality()
    {
        return Config.poisonLethal.get() ? 0 : 1;
    }

    public static boolean ModifyWither()
    {
        return Config.witherRework.get();
    }

    public static void DoInstantEffects(MobEffect effect, LivingEntity pLivingEntity, int pAmplifier)
    {
        if(effect == ModEffects.SUSTENANCE.get() && pLivingEntity instanceof Player player)
        {
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20);
        }
        else if(effect == ModEffects.CLEANSING.get())
        {
            if(!pLivingEntity.isInvertedHealAndHarm())
            {
                var list = new ArrayList<MobEffectInstance>();

                for(var effectInstance : pLivingEntity.getActiveEffects())
                {
                    if(!effectInstance.getEffect().isBeneficial())
                    {
                        list.add(effectInstance);
                    }
                }

                if(!list.isEmpty())
                {
                    pLivingEntity.level.playSound(null, pLivingEntity.blockPosition(), ModSoundEvents.CLEANSE.get(), SoundSource.NEUTRAL, 0.4F, 0.8F + pLivingEntity.getLevel().getRandom().nextFloat() * 0.2F);

                    for(var effectInstance : list)
                    {
                        var duration = effectInstance.getDuration();
                        var newDuration = duration - 1200;

                        pLivingEntity.removeEffect(effectInstance.getEffect());

                        if(newDuration > 0)
                        {
                            var newEffectInstance = new MobEffectInstance(effectInstance.getEffect(), newDuration, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible(), effectInstance.showIcon());
                            pLivingEntity.addEffect(newEffectInstance);
                        }
                    }
                }
            }
            else
            {
                pLivingEntity.hurt(ModDamageSources.CLEANSING, 10);
            }
        }
        else if(effect == ModEffects.BEES.get() && !pLivingEntity.level.isClientSide)
        {
            if(!pLivingEntity.hasEffect(ModEffects.NO_BEES.get()) && !(pLivingEntity instanceof Bee))
            {
                pLivingEntity.addEffect(new MobEffectInstance(ModEffects.NO_BEES.get(), 1200));

                var level = pLivingEntity.level;
                var beeCount = level.random.nextInt(3) + 3;

                for(int i = 0; i < beeCount; i++)
                {
                    var bee = EntityType.BEE.create(level);

                    assert bee != null : "Bee entity is null!";

                    var signX = level.random.nextInt(3) - 1;
                    var signZ = level.random.nextInt(3) - 1;
                    var xOffset = signX * level.random.nextFloat(3) + 1;
                    var yOffset = level.random.nextFloat(2);
                    var zOffset = signZ * level.random.nextFloat(3) + 1;
                    var beeX = pLivingEntity.getX() + xOffset;
                    var beeY = pLivingEntity.getY() + yOffset;
                    var beeZ = pLivingEntity.getZ() + zOffset;

                    bee.setPos(new Vec3(beeX, beeY, beeZ));
                    bee.setAggressive(true);
                    bee.setTarget(pLivingEntity);

                    level.addFreshEntity(bee);
                }
            }
        }
    }
}
