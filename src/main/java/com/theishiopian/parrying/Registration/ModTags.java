package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.ParryingMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags
{
    public static TagKey<Item> TWO_HANDED_WEAPONS = tag("two_handed_weapons");

    private static TagKey<Item> tag(String id)
    {
        return ItemTags.create(new ResourceLocation(ParryingMod.MOD_ID + ":" + id));
    }
}