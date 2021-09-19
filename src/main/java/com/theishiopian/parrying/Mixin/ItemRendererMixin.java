package com.theishiopian.parrying.Mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.theishiopian.parrying.Entity.Render.RenderSpear;
import com.theishiopian.parrying.Items.SpearItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(net.minecraft.client.renderer.ItemRenderer.class)
public class ItemRendererMixin
{
    @ModifyVariable
    (
        method = "render(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V",
        at = @At(value = "HEAD", target = "net/minecraftforge/client/ForgeHooksClient.handleCameraTransforms(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;Z)Lnet/minecraft/client/renderer/model/IBakedModel;"),
        index = 8
    )
    /*
     * this mixin is a travesty against the machine spirit, but minecraft has forced my hand, as there's
     * no other way to change the model in the hand with data from the actual entity, AND be able to give the inventory a separate model.
     * if you have an idea that works, go ahead and open a PR.
     */
    private IBakedModel render(IBakedModel originalModel, ItemStack stack, ItemCameraTransforms.TransformType transformType, boolean flag, MatrixStack matrixStack, IRenderTypeBuffer buffer, int i, int j, IBakedModel original)
    {
        boolean isGuiTransform =
            transformType == ItemCameraTransforms.TransformType.GUI ||
            transformType == ItemCameraTransforms.TransformType.GROUND ||
            transformType == ItemCameraTransforms.TransformType.FIXED;
        boolean isSpear = stack.getItem() instanceof SpearItem;
        boolean isGui = isGuiTransform && !RenderSpear.renderingSpear;//very dumb
        String mat = isSpear ? ((SpearItem)stack.getItem().asItem()).getMaterialID() : "not_a_spear_please_report";//not sure how this could possibly not be a spear and still get used, but this should at least indicate where the problem is if it does happen
        String invModelPath = "parrying:" + mat + "_spear_gui#inventory";
        String modelPath = "parrying:" + mat + "_spear#inventory";
        String throwModelPath = "parrying:" + mat + "_spear_throwing#inventory";

        boolean isThrowing = SpearItem.throwingSpears.contains(stack);//this is dumb, this needs to be replaced with something less pasta-like at some point

        IBakedModel invModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation(invModelPath));
        IBakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation(modelPath));
        IBakedModel throwModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation(throwModelPath));

        model = isThrowing ? throwModel : model;

        return isSpear ? (isGui ? invModel : model) : originalModel;//yes, this is stupid
    }
}
