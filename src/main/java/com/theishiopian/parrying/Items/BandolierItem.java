package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModItems;
import com.theishiopian.parrying.Registration.ModTags;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

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

    public static ItemStack findBandolier(Player player)
    {
        ItemStack itemToScan;
        ItemStack bandolier = ItemStack.EMPTY;
        ItemStack priorityBandolier = ItemStack.EMPTY;
        for(int i = 45; i >= 0; i--)
        {
            itemToScan = player.getInventory().getItem(i);

            if(itemToScan.is(ModItems.BANDOLIER.get()) )
            {
                if(AbstractBundleItem.isEmpty(itemToScan))continue;

                bandolier = itemToScan;

                if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.INTRUSIVE.get(), itemToScan) > 0)
                {
                    priorityBandolier = itemToScan;
                }
            }
        }

        if(!priorityBandolier.isEmpty())bandolier = priorityBandolier;

        return bandolier;
    }
}