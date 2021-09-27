package com.theishiopian.parrying.Entity.Render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.theishiopian.parrying.Entity.DaggerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
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
        matrix.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, daggerEntity.xRot, daggerEntity.xRot) - 135 + ((float)daggerEntity.GetSpinTicks() * 20f)));

        //matrix.translate(0,-0.2f,0);

        IBakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(daggerEntity.daggerItem);

        Minecraft.getInstance().getItemRenderer().render(daggerEntity.daggerItem, ItemCameraTransforms.TransformType.FIXED, false, matrix, buffer, light, OverlayTexture.NO_OVERLAY, model);
        matrix.popPose();
    }

    @Override//it works, trust me
    public @SuppressWarnings("all") ResourceLocation getTextureLocation(@NotNull DaggerEntity entity)
    {
        return null;
    }
}
