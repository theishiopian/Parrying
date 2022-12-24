package com.theishiopian.parrying.CoreMod.Mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.theishiopian.parrying.Registration.ModEffects;
import com.theishiopian.parrying.Utility.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.Gui.class)
public class GuiMixin
{
    private static int x = 0;
    private static int h = 0;
    @Inject(method = "renderHearts", at = @At(value = "HEAD"))
    private void InjectIntoHead(PoseStack pPoseStack, Player pPlayer, int pX, int pY, int pHeight, int p_168694_, float maxHealth, int health, int p_168697_, int p_168698_, boolean p_168699_, CallbackInfo ci)
    {
        x = 0;
        h = health;
    }

    @Redirect(method = "renderHearts", at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Gui$HeartType;IIIZZ)V"))
    private void RedirectHeartRender(Gui instance, PoseStack pPoseStack, Gui.HeartType pHeartType, int pX, int pY, int p_168705_, boolean p_168706_, boolean p_168707_)
    {
        Player player = Minecraft.getInstance().player;
        assert player != null : "Null player in heart renderer mixin! What the heck happened?!";

        if(!player.hasEffect(ModEffects.IMMORTALITY.get()))
        {
            instance.renderHeart(pPoseStack, pHeartType, pX, pY, p_168705_, p_168706_, p_168707_);
            return;
        }

        int heart = Math.round(h/2f) - 1;
        //Debug.log("heart: " + heart + " x: " + x);
        if(x != heart)
        {
            instance.renderHeart(pPoseStack, pHeartType, pX, pY, p_168705_, p_168706_, p_168707_);//vanilla render
        }
        else
        {//my render
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1f);
            RenderSystem.setShaderTexture(0, ModUtil.GENERAL_ICONS);

            Screen.blit(pPoseStack, pX, pY, 32, 16, 16, 16, 64, 64);
        }

        x++;
    }
}
