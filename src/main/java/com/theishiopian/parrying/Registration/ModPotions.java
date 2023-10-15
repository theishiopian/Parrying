package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.ParryingMod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions
{
    //todo stability and instability potions, splash resistance, stew buffs, bees potion
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, ParryingMod.MOD_ID);
    public static final RegistryObject<Potion> COALESCENCE = POTIONS.register("coalescence", () -> new Potion(new MobEffectInstance(ModEffects.COALESCENCE.get(), 1200)));
    public static final RegistryObject<Potion> COALESCENCE_LONG = POTIONS.register("coalescence_long", () -> new Potion(new MobEffectInstance(ModEffects.COALESCENCE.get(), 3600)));
    public static final RegistryObject<Potion> IMMORTALITY = POTIONS.register("immortality", () -> new Potion(new MobEffectInstance(ModEffects.IMMORTALITY.get(), 600)));
    public static final RegistryObject<Potion> IMMORTALITY_LONG = POTIONS.register("immortality_long", () -> new Potion(new MobEffectInstance(ModEffects.IMMORTALITY.get(), 1200)));
    public static final RegistryObject<Potion> VITALITY = POTIONS.register("vitality", () -> new Potion(new MobEffectInstance(ModEffects.VITALITY.get(), 600)));
    public static final RegistryObject<Potion> VITALITY_LONG = POTIONS.register("vitality_long", () -> new Potion(new MobEffectInstance(ModEffects.VITALITY.get(), 1200)));
    public static final RegistryObject<Potion> SUSTENANCE = POTIONS.register("sustenance", () -> new Potion(new MobEffectInstance(ModEffects.SUSTENANCE.get(), 1)));
    public static final RegistryObject<Potion> CLEANSING = POTIONS.register("cleansing", () -> new Potion(new MobEffectInstance(ModEffects.CLEANSING.get(), 1)));
    public static final RegistryObject<Potion> BEES = POTIONS.register("bees", () -> new Potion(new MobEffectInstance(ModEffects.BEES.get(), 1)));
    public static final RegistryObject<Potion> DECAY = POTIONS.register("decay", () -> new Potion(new MobEffectInstance(MobEffects.WITHER, 1200)));
    public static final RegistryObject<Potion> DECAY_LONG = POTIONS.register("decay_long", () -> new Potion(new MobEffectInstance(MobEffects.WITHER, 3600)));
}