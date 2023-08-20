package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.ParryingMod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions
{
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, ParryingMod.MOD_ID);
    public static final RegistryObject<Potion> COALESCENCE = POTIONS.register("coalescence", () -> new Potion(new MobEffectInstance(ModEffects.COALESCENCE.get(), 1200)));
    public static final RegistryObject<Potion> COALESCENCE_LONG = POTIONS.register("coalescence_long", () -> new Potion(new MobEffectInstance(ModEffects.COALESCENCE.get(), 3600)));
    public static final RegistryObject<Potion> IMMORTALITY = POTIONS.register("immortality", () -> new Potion(new MobEffectInstance(ModEffects.IMMORTALITY.get(), 600)));
    public static final RegistryObject<Potion> IMMORTALITY_LONG = POTIONS.register("immortality_long", () -> new Potion(new MobEffectInstance(ModEffects.IMMORTALITY.get(), 1200)));
}
