package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.ParryingMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags
{
    public static TagKey<Item> TWO_HANDED_WEAPONS = ItemTags.create(new ResourceLocation(ParryingMod.MOD_ID, "two_handed_weapons"));
    public static TagKey<Item> BANDOLIER = ItemTags.create(new ResourceLocation(ParryingMod.MOD_ID, "bandolier"));
    public static TagKey<Item> BANDOLIER_INSTANT = ItemTags.create(new ResourceLocation(ParryingMod.MOD_ID, "bandolier_instant"));
    public static TagKey<Item> BANDOLIER_FINISH = ItemTags.create(new ResourceLocation(ParryingMod.MOD_ID, "bandolier_finish"));
    public static TagKey<Item> BANDOLIER_PASSIVE = ItemTags.create(new ResourceLocation(ParryingMod.MOD_ID, "bandolier_passive"));
}