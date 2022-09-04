package com.theishiopian.parrying.CoreMod.Mixin;

import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.ThrowablePotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(net.minecraft.world.item.ThrowablePotionItem.class)
public class ThrowablePotionItemMixin
{
    @ModifyConstant(constant = @Constant(floatValue = 0.5f), method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;")
    private float ModifyPotionThrowingPower(float constant)
    {
        return ((ThrowablePotionItem)(Object)this) instanceof SplashPotionItem ? 0.8f : 0.6f;//todo config
    }
}
