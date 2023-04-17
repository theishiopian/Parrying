package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModItems;
import com.theishiopian.parrying.Registration.ModTags;
import com.theishiopian.parrying.Utility.Debug;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.HashMap;
import java.util.UUID;

public class BandolierItem extends AbstractBundleItem
{
    public static HashMap<UUID, ItemStack> itemsToGive = new HashMap<>();
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

    public static ItemStack findItemInBandolier(Player player, ItemStack oldStack)
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

        BundleItemCapability c = getActualCapability(bandolier);

        if(c != null)
        {
            var oldClass = oldStack.getItem().getClass();
            c.deflate();
            Debug.log("starting loop");
            for (int i = 0; i < c.stacksList.size(); i++)
            {
                var newClass = c.stacksList.get(i).getItem().getClass();
                Debug.log("ITERATION");
                Debug.log("oldItem " + oldClass);
                Debug.log("newItem " + newClass);

                if(oldClass.toString().equals(newClass.toString()))//I hate this so much
                {
                    var stackOut = c.stacksList.get(i).copy();
                    c.stacksList.set(i, ItemStack.EMPTY);
                    c.deflate();
                    player.getCooldowns().addCooldown(stackOut.getItem(), 20);
                    Debug.log("returning " + stackOut);
                    return stackOut;
                }
            }
        }

        return oldStack;
    }
}