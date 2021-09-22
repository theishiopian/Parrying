package com.theishiopian.parrying.Mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.client.renderer.FirstPersonRenderer.class)
public interface FirstPersonRendererAccessor
{
    @Accessor float getOffHandHeight();
    @Accessor ItemStack getOffHandItem();
}
