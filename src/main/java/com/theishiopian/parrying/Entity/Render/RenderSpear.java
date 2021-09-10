package com.theishiopian.parrying.Entity.Render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.theishiopian.parrying.Entity.SpearEntity;
import com.theishiopian.parrying.Items.SpearItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class RenderSpear extends EntityRenderer<SpearEntity>
{
    public RenderSpear(EntityRendererManager manager)
    {
        super(manager);
    }

    @Override
    public void render(SpearEntity spearEntity, float yaw, float partialTicks, MatrixStack matrix, @NotNull IRenderTypeBuffer buffer, int light)
    {
        matrix.pushPose();

        matrix.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, spearEntity.yRotO, spearEntity.yRot) - 90.0F));
        matrix.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, spearEntity.xRotO, spearEntity.xRot) - 135));

        matrix.translate(0,-0.2f,0);

        Minecraft mc = Minecraft.getInstance();
        mc.getItemRenderer().render(spearEntity.spearItem, ItemCameraTransforms.TransformType.FIXED, false, matrix, buffer, light, OverlayTexture.NO_OVERLAY, getSpearModel(spearEntity.spearItem, spearEntity.level, null));
        matrix.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(@NotNull SpearEntity entity)
    {
        return null;
    }

    public IBakedModel getSpearModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity)
    {
        SpearItem item = (SpearItem) stack.getItem();
        IBakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation("parrying:test_spear_handheld#inventory"));

        ClientWorld clientworld = world instanceof ClientWorld ? (ClientWorld)world : null;
        IBakedModel toReturn = model.getOverrides().resolve(model, stack, clientworld, entity);
        return toReturn == null ? Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getMissingModel() : toReturn;
    }
}
