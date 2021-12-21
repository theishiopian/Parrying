package com.theishiopian.parrying.Mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.client.renderer.ItemInHandRenderer.class)
/*
  This interface is used to pull data out of FirstPersonRenderer for dual wielding purposes
 */
public interface FirstPersonRendererAccessor
{
    @Accessor float getOffHandHeight();
    @Accessor
    ItemStack getOffHandItem();
}
