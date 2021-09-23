package com.theishiopian.parrying.Entity.Render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.theishiopian.parrying.Entity.SpearEntity;
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

public class RenderSpear extends EntityRenderer<SpearEntity>
{
    public static boolean renderingSpear = false;//todo this is dumb as hell, remove if possible

    public RenderSpear(EntityRendererManager manager)
    {
        super(manager);
    }

    @Override
    public void render(SpearEntity spearEntity, float yaw, float partialTicks, MatrixStack matrix, @NotNull IRenderTypeBuffer buffer, int light)
    {
        matrix.pushPose();
        renderingSpear = true;
        matrix.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, spearEntity.yRotO, spearEntity.yRot) - 90.0F));
        matrix.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, spearEntity.xRotO, spearEntity.xRot) - 135));

        matrix.translate(0,-0.2f,0);
        matrix.scale(2.5f,2.5f,1.5f);

        IBakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(spearEntity.spearItem);

        Minecraft.getInstance().getItemRenderer().render(spearEntity.spearItem, ItemCameraTransforms.TransformType.FIXED, false, matrix, buffer, light, OverlayTexture.NO_OVERLAY, model);
        renderingSpear = false;
        matrix.popPose();
    }

    @Override//it works, trust me
    public @SuppressWarnings("all") ResourceLocation getTextureLocation(@NotNull SpearEntity entity)
    {
        return null;
    }
}
