package com.theishiopian.parrying.Entity.Render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.theishiopian.parrying.Entity.SpearEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RenderSpear extends EntityRenderer<SpearEntity>
{
    public RenderSpear(EntityRendererManager manager)
    {
        super(manager);
    }

    //TODO this isn't getting called aaaaaaaaaah
    @Override
    public void render(SpearEntity spearEntity, float yaw, float partialTicks, MatrixStack matrix, @NotNull IRenderTypeBuffer buffer, int light)
    {
        System.out.println("the funny dad was here");
        matrix.pushPose();
        //todo: translate/rotate/scale matrix
        Minecraft mc = Minecraft.getInstance();

        mc.getItemRenderer().render(spearEntity.spearItem, ItemCameraTransforms.TransformType.FIXED, false, matrix, buffer, light, OverlayTexture.NO_OVERLAY, mc.getItemRenderer().getModel(spearEntity.spearItem, spearEntity.level, null));
        matrix.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(@NotNull SpearEntity entity)
    {
        return null;
    }
}
