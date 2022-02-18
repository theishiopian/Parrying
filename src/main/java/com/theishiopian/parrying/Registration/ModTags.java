package com.theishiopian.parrying.Registration;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public class ModTags
{
    public static Tag.Named<Item> TWO_HANDED_WEAPONS;

    public static void Bind()
    {
        TWO_HANDED_WEAPONS = ItemTags.bind("parrying:two_handed_weapons");
    }
}