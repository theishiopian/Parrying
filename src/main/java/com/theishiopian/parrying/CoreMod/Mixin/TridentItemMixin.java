package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Registration.ModTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public class TridentItemMixin
{
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void InjectIntoUse(Level pLevel, Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir)
    {
        if(pPlayer.getItemInHand(InteractionHand.OFF_HAND).is(ModTags.THROW_CANCEL) && !pPlayer.isCrouching())
        {
            ItemStack stack = pPlayer.getItemInHand(pHand);
            pPlayer.startUsingItem(InteractionHand.OFF_HAND);
            cir.setReturnValue(InteractionResultHolder.fail(stack));
        }
    }
}
