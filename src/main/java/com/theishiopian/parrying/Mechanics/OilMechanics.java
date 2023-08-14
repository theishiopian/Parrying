package com.theishiopian.parrying.Mechanics;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Items.OilPotionItem;
import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModSoundEvents;
import com.theishiopian.parrying.Utility.ModUtil;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public abstract class OilMechanics
{
    public static boolean DoMeleeOil(ItemStack weapon, LivingEntity target, LivingEntity attacker)
    {
        if(ModUtil.IsWeapon(weapon))
        {
            var effects = PotionUtils.getMobEffects(weapon);

            for (MobEffectInstance effect : effects)
            {
                var e = effect.getEffect();
                var dur = effect.getDuration() * OilPotionItem.DURATION_MOD;
                var amp = effect.getAmplifier();

                target.level.playSound(null, target.blockPosition(), ModSoundEvents.CLEANSE.get(), SoundSource.PLAYERS, 0.4F, 0.8F + target.getLevel().getRandom().nextFloat() * 0.2F);

                if(e.isInstantenous())
                {
                    e.applyInstantenousEffect(target, target, target, amp, 1);
                }
                else
                {
                    target.addEffect(new MobEffectInstance(e, (int) dur, amp, effect.isAmbient(),effect.isVisible()), attacker);
                }
            }

            var vLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.VENOMOUS.get(), weapon);
            var random = target.getLevel().getRandom();
            var roll = random.nextInt(10);

            if(!Config.venomousEnabled.get() || vLevel <= roll)
            {
                weapon.removeTagKey("CustomPotionColor");
                weapon.removeTagKey("Potion");
            }

            return ModUtil.ShouldBeHarmful(effects, target);
        }

        return true;
    }
}