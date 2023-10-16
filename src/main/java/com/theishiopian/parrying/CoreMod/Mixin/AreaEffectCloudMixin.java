package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Registration.ModEnchantments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.world.entity.AreaEffectCloud.class)
public class AreaEffectCloudMixin
{
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean RedirectAddEffect(LivingEntity target, MobEffectInstance effect, Entity source)
    {
        var shouldDo = !(effect.getEffect().isInstantenous() || effect.getEffect().isBeneficial());
        var totalLvl = 0;

        if(shouldDo)
        {
            var armor = target.getArmorSlots();

            for(var slot : armor)
            {
                totalLvl += EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SPLASH_PROTECTION.get(), slot);
            }
        }

        var newDuration = Math.max(20, effect.getDuration() - (int)(effect.getDuration() * 0.05f * totalLvl));
        var newInstance = !shouldDo ? effect : new MobEffectInstance(effect.getEffect(), newDuration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon());
        return target.addEffect(new MobEffectInstance(newInstance), source);
    }
}
