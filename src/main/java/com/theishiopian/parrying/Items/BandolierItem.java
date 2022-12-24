package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Registration.ModTags;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public class BandolierItem extends AbstractBundleItem
{
    public BandolierItem(Properties pProperties)
    {
        super(pProperties, 512, 64, ModTags.BANDOLIER, new TranslatableComponent("tooltip.parrying.bandolier"));
    }

    public static int GetCount(ItemStack bandolier)
    {
        BundleItemCapability c = getActualCapability(bandolier);

        if(c == null) return 0;

        return c.stacksList.size();
    }
}