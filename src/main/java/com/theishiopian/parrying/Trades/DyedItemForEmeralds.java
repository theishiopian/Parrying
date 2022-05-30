package com.theishiopian.parrying.Trades;

import com.google.common.collect.Lists;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.List;
import java.util.Random;

/*
 * Modified version of net.minecraft.world.entity.npc.VillagerTrades.DyedArmorForEmeralds
 * Supports any dyeable item and adds a random number of colors for maximum fancy
 */
public class DyedItemForEmeralds implements VillagerTrades.ItemListing
{
    private final Item item;
    private final int value;
    private final int maxUses;
    private final int villagerXp;

    private final int numColors;

    public DyedItemForEmeralds(Item pItem, int pValue, int numColors)
    {
        this(pItem, pValue, numColors, 12, 1);
    }

    public DyedItemForEmeralds(Item pItem, int pValue, int numColors, int pMaxUses, int pVillagerXp)
    {
        this.item = pItem;
        this.value = pValue;
        this.maxUses = pMaxUses;
        this.villagerXp = pVillagerXp;
        this.numColors = numColors;
    }

    public MerchantOffer getOffer(Entity trader, Random random)
    {
        ItemStack emeraldStack = new ItemStack(Items.EMERALD, this.value);
        ItemStack productStack = new ItemStack(this.item);
        if (this.item instanceof DyeableLeatherItem)
        {
            List<DyeItem> dyes = Lists.newArrayList();
            int dyeAmount = random.nextInt(numColors + 1);

            for (int i = 0; i < dyeAmount; i++)
            {
                dyes.add(getRandomDye(random));
            }

            productStack = dyes.size() > 0 ? DyeableLeatherItem.dyeArmor(productStack, dyes) : productStack;
        }

        return new MerchantOffer(emeraldStack, productStack, this.maxUses, this.villagerXp, 0.2F);
    }

    private static DyeItem getRandomDye(Random pRandom)
    {
        return DyeItem.byColor(DyeColor.byId(pRandom.nextInt(16)));
    }
}