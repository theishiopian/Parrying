package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.Handler.LootHandler;
import com.theishiopian.parrying.ParryingMod;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers
{
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, ParryingMod.MOD_ID);

    public static final RegistryObject<LootHandler.QuiverModifier.Serializer> QUIVER_DUNGEON_LOOT = GLM.register("quiver_modifier", LootHandler.QuiverModifier.Serializer::new);
}
