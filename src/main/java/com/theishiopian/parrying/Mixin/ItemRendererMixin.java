package com.theishiopian.parrying.Mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.theishiopian.parrying.Registration.ModItems;
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
    private IBakedModel render(IBakedModel originalModel, ItemStack stack, ItemCameraTransforms.TransformType transformType, boolean flag, MatrixStack matrixStack, IRenderTypeBuffer buffer, int i, int j, IBakedModel original)
    {
        boolean isGuiTransform =
            transformType == ItemCameraTransforms.TransformType.GUI ||
            transformType == ItemCameraTransforms.TransformType.GROUND ||
            transformType == ItemCameraTransforms.TransformType.FIXED;
        boolean isGui = stack.getItem() == ModItems.IronSpear && isGuiTransform;
        String model = "parrying:iron_spear_gui#inventory";//todo custom path
        //if(isGui)ParryingMod.LOGGER.info(model);
        return isGui ? Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation(model)) : originalModel;//yes this is stupid. ideas are appreciated
        //return null;
    }
}
