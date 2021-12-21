package com.theishiopian.parrying.Mixin;

import com.theishiopian.parrying.Mechanics.DualWielding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/*
This Mixin is used to allow the off hand to swing and animate just like the main hand. This is used for dual wielding.
 */
@Mixin(ItemInHandRenderer.class)
public abstract class FirstPersonRendererMixin
{
    @ModifyArg(method = {"tick()V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", ordinal = 3), index = 0)
    private float ModifyValueToClamp(float in, float min, float max)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;//if this throws null we have a problem
        ItemInHandRenderer thisRenderer = Minecraft.getInstance().getItemInHandRenderer();
        ItemStack offHandCurrentItemStack = player.getOffhandItem();

        boolean reEquip = net.minecraftforge.client.ForgeHooksClient.shouldCauseReequipAnimation(((FirstPersonRendererAccessor)(thisRenderer)).getOffHandItem(), offHandCurrentItemStack, -1);

        float f = player.getAttackStrengthScale(1);
        //Debug.log("you better not be on a server here");
        return (DualWielding.IsDualWielding(player) ? ((!reEquip) ? f * f * f : 0) : ((!reEquip) ? 1 : 0)) - ((FirstPersonRendererAccessor)(thisRenderer)).getOffHandHeight();
    }
}
