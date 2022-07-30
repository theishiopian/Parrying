package com.theishiopian.parrying.CoreMod.Mixin;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public class ArrowMixin
{
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;getWaterInertia()F"))
    private void InjectIntoTick(CallbackInfo ci)
    {
        AbstractArrow arrow = (AbstractArrow)(Object)this;

        if(arrow.isNoGravity())
        {
            arrow.setNoGravity(false);
        }
    }
}
