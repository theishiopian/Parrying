package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.ParryingMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class ModTags
{
    public static TagKey<Item> TWO_HANDED_WEAPONS = ItemTags.create(new ResourceLocation(ParryingMod.MOD_ID, "two_handed_weapons"));
    public static TagKey<Item> BANDOLIER = ItemTags.create(new ResourceLocation(ParryingMod.MOD_ID, "bandolier"));
    public static TagKey<Item> THROW_CANCEL = ItemTags.create(new ResourceLocation(ParryingMod.MOD_ID, "throw_cancel"));
    public static TagKey<Item> STEW = ItemTags.create(new ResourceLocation(ParryingMod.MOD_ID, "stew"));
    public static TagKey<EntityType<?>> NON_DEFLECTABLE = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(ParryingMod.MOD_ID, "non_deflectable"));
}