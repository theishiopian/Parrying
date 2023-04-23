package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.CoreMod.Hooks.PlayerHooks;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Player.class)
public class PlayerMixin
{
    @Inject(at = @At("HEAD"), method = "getCurrentItemAttackStrengthDelay", cancellable = true)
    private void InjectIntoGetCurrentItemAttackStrengthDelay(CallbackInfoReturnable<Float> cir)
    {
        Optional<Float> value = PlayerHooks.ModifyAttackStrength((Player) (Object) this);
        value.ifPresent(cir::setReturnValue);
    }

    @Inject(at = @At("HEAD"), method = "isScoping", cancellable = true)
    private void InjectIntoIsScoping(CallbackInfoReturnable<Boolean> cir)
    {
        Optional<Boolean> value = PlayerHooks.ModifyScopingStatus((Player) (Object) this);
        value.ifPresent(cir::setReturnValue);
    }
}
