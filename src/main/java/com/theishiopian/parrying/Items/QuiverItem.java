package com.theishiopian.parrying.Items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

public class QuiverItem extends Item
{
    public QuiverItem(Properties pProperties)
    {
        super(pProperties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {

        return super.initCapabilities(stack, nbt);
    }
}

