package com.theishiopian.parrying.Entity.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.theishiopian.parrying.Entity.SpearEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class RenderSpear extends EntityRenderer<SpearEntity>
{
    public RenderSpear(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void render(SpearEntity spearEntity, float yaw, float partialTicks, PoseStack matrix, @NotNull MultiBufferSource buffer, int light)
    {
        matrix.pushPose();
        matrix.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, spearEntity.yRotO, spearEntity.getYRot()) - 90.0F));
        matrix.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, spearEntity.xRotO, spearEntity.getXRot()) - 135));

        matrix.translate(0,-0.2f,0);
        matrix.scale(2.5f,2.5f,1.5f);

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        renderer.renderStatic(spearEntity.spearItem, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrix, buffer, spearEntity.getId());
        matrix.popPose();
    }

    @Override//it works, trust me
    public @SuppressWarnings("all")
    ResourceLocation getTextureLocation(@NotNull SpearEntity entity)
    {
        return null;
    }

    @Override
    public boolean shouldRender(SpearEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ)
    {
        return super.shouldRender(pLivingEntity, pCamera, pCamX, pCamY, pCamZ);
    }
}
