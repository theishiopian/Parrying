package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.world.item.Tiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Tiers.class)
public class TiersMixin
{
    @Inject(method = "getUses", at = @At("HEAD"), cancellable = true)
    private void InjectIntoGetUses(CallbackInfoReturnable<Integer> cir)
    {
        if(Tiers.class.cast(this) == Tiers.GOLD && Config.isGoldBuffed.get())
        {
            cir.setReturnValue(180);
        }
    }

    @Inject(method = "getLevel", at = @At("HEAD"), cancellable = true)
    private void InjectIntoGetLevel(CallbackInfoReturnable<Integer> cir)
    {
        if(Tiers.class.cast(this) == Tiers.GOLD && Config.isGoldBuffed.get())
        {
            cir.setReturnValue(2);
        }
    }

    @Inject(method = "getAttackDamageBonus", at = @At("HEAD"), cancellable = true)
    private void InjectIntoGetDamage(CallbackInfoReturnable<Float> cir)
    {
        if(Tiers.class.cast(this) == Tiers.GOLD && Config.isGoldBuffed.get())
        {
            cir.setReturnValue(2f);
        }
    }
}
