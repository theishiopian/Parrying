package com.theishiopian.parrying.Items;

import net.minecraft.item.IItemTier;

public class FlailItem extends APItem
{
    public float swingTIme = 0;

    public FlailItem(IItemTier itemTier, int baseDamage, float baseSpeed, float baseAP, Properties properties)
    {
        super(itemTier, baseDamage, baseSpeed, baseAP, properties);
    }
}
