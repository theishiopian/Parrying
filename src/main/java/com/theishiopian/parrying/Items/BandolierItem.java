package com.theishiopian.parrying.Items;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.ItemTags;

public class BandolierItem extends AbstractBundleItem
{
    public BandolierItem(Properties pProperties)
    {
        super(pProperties, 512, ItemTags.BOATS, new TranslatableComponent("filter.parrying.bandolier"));
    }
}
