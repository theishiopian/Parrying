package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Items.OilPotionItem;
import com.theishiopian.parrying.Registration.ModSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ThrownTrident.class)
public class ThrownTridentMixin
{
    @Shadow private ItemStack tridentItem;

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownTrident;doPostHurtEffects(Lnet/minecraft/world/entity/LivingEntity;)V",shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void InjectIntoOnHit(EntityHitResult pResult, CallbackInfo ci, Entity entity, float f, Entity entity1, DamageSource damagesource, SoundEvent soundevent, LivingEntity livingentity1)
    {
        var effects = PotionUtils.getMobEffects(tridentItem);

        for (MobEffectInstance effect : effects)
        {
            var e = effect.getEffect();
            var dur = effect.getDuration() * OilPotionItem.DURATION_MOD;
            var amp = effect.getAmplifier();

            livingentity1.level.playSound(null, livingentity1.blockPosition(), ModSoundEvents.CLEANSE.get(), SoundSource.PLAYERS, 0.4F, 0.8F + livingentity1.getLevel().getRandom().nextFloat() * 0.2F);

            if(e.isInstantenous())
            {
                e.applyInstantenousEffect((ThrownTrident)(Object)this, entity1, livingentity1, amp, 1);
            }
            else
            {
                livingentity1.addEffect(new MobEffectInstance(e, (int) dur, amp, effect.isAmbient(),effect.isVisible()), livingentity1);
            }
        }

        tridentItem.removeTagKey("CustomPotionColor");
        tridentItem.removeTagKey("Potion");
    }
}
