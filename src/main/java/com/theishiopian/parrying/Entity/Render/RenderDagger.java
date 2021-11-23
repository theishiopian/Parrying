package com.theishiopian.parrying.Entity.Render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.theishiopian.parrying.Entity.DaggerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;

public class RenderDagger extends EntityRenderer<DaggerEntity>
{
    public RenderDagger(EntityRendererManager manager)
    {
        super(manager);
    }

    @Override
    public void render(DaggerEntity daggerEntity, float yaw, float partialTicks, MatrixStack matrix, @NotNull IRenderTypeBuffer buffer, int light)
    {
        matrix.pushPose();
        matrix.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, daggerEntity.yRotO, daggerEntity.yRot) - 90.0F));

        if(!daggerEntity.GetHasImpacted())
        {
            matrix.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, daggerEntity.xRotO, daggerEntity.xRot) - 135 + (float)daggerEntity.GetSpinTicks() * -50f));
        }
        else
        {
            matrix.mulPose(Vector3f.ZP.rotationDegrees(daggerEntity.xRot - 135));
        }

        //matrix.translate(0,-0.2f,0);

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        renderer.renderStatic(daggerEntity.daggerItem, ItemCameraTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrix, buffer);
        matrix.popPose();
    }

    @Override//it works, trust me
    public @SuppressWarnings("all") ResourceLocation getTextureLocation(@NotNull DaggerEntity entity)
    {
        return null;
    }
}
