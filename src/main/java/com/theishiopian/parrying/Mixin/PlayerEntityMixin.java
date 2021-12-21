package com.theishiopian.parrying.Mixin;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Mechanics.DualWielding;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerEntityMixin
{
    @Inject(at = @At("HEAD"), method = "getCurrentItemAttackStrengthDelay", cancellable = true)
    private void InjectIntoGetCurrentItemAttackStrengthDelay(CallbackInfoReturnable<Float> cir)
    {
        if(Config.dualWieldEnabled.get())
        {
            Player player = ((Player)(Object)this);

            if(DualWielding.IsDualWielding(player))
            {
                float mainSpeed = (float) player.getMainHandItem().
                        getAttributeModifiers(EquipmentSlot.MAINHAND).
                        get(Attributes.ATTACK_SPEED).stream().findFirst().get().getAmount();

                float offSpeed = (float) player.getOffhandItem().
                        getAttributeModifiers(EquipmentSlot.MAINHAND).
                        get(Attributes.ATTACK_SPEED).stream().findFirst().get().getAmount();

                float speedMod = (mainSpeed + offSpeed) / 2;//average speed
                float diff = Math.abs(mainSpeed - offSpeed);//penalty for difference in speeds

                cir.setReturnValue((float)((1.0D / (Attributes.ATTACK_SPEED.getDefaultValue() + speedMod + 0.2f - diff)) * 20.0D));
            }
        }
    }
}