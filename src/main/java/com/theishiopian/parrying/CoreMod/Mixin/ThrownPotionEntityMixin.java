package com.theishiopian.parrying.CoreMod.Mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Utility.ModUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.util.List;

@Mixin(net.minecraft.world.entity.projectile.ThrownPotion.class)
public class ThrownPotionEntityMixin
{
    @Redirect(method = "applySplash", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private List<LivingEntity> RedirectAABB(Level instance, Class aClass, AABB aabb)
    {
        //Debug.log("redirecting");
        List<LivingEntity> targets = instance.getEntitiesOfClass(LivingEntity.class, aabb);
        var potion = (ThrownPotion)(Object)this;
        var owner = potion.getOwner();

        if(Config.shieldSplash.get())targets.removeIf(target -> ModUtil.IsBlocked(target, potion));
        if(Config.noSelfSplash.get())targets.removeIf(target -> target == owner);
        return targets;
    }

    @ModifyConstant(method = "makeAreaOfEffectCloud", constant = @Constant(floatValue = 3.0f))
    private float ModifyLingerCloudRadius(float constant)
    {
        return Config.lingeringRadius.get().floatValue();
    }

    @ModifyArg(method = "applySplash", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;<init>(Lnet/minecraft/world/effect/MobEffect;IIZZ)V"), index = 1)
    private int ModifyDuration(int duration, @Local LivingEntity target, @Local MobEffect effect)
    {
        if(effect.isInstantenous() || effect.isBeneficial()) return duration;
        var armor = target.getArmorSlots();
        var totalLvl = 0;

        for(var slot : armor)
        {
            totalLvl += EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SPLASH_PROTECTION.get(), slot);
        }

        return Math.max(20, duration - (int)(duration * 0.05f * totalLvl));
    }
}
