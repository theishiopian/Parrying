package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Registration.ModTags;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BandolierItem extends AbstractBundleItem
{
    public BandolierItem(Properties pProperties)
    {
        super(pProperties, 512, ModTags.BANDOLIER, new TranslatableComponent("tooltip.parrying.bandolier"));
    }

    public static boolean CheckItemClass(Class<? extends Item> type, int index, ItemStack bundle)
    {
        BundleItemCapability c = getActualCapability(bundle);

        if(c == null) return false;

        return type.isInstance(c.stacksList.get(index).getItem());
    }
}