package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.Enchantment.*;
import com.theishiopian.parrying.ParryingMod;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * This class is used to register custom enchantments.
 */
public class ModEnchantments
{
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ParryingMod.MOD_ID);

    public static final RegistryObject<Enchantment> DEFLECTING = ENCHANTMENTS.register("deflection", DeflectingEnchantment::new);
    public static final RegistryObject<Enchantment> RIPOSTE = ENCHANTMENTS.register("riposte", RiposteEnchantment::new);
    public static final RegistryObject<Enchantment> BASHING = ENCHANTMENTS.register("bashing", BashingEnchantment::new);
    public static final RegistryObject<Enchantment> TREACHERY = ENCHANTMENTS.register("treachery", TreacheryEnchantment::new);
    public static final RegistryObject<Enchantment> JOUSTING = ENCHANTMENTS.register("jousting", JoustingEnchantment::new);
    public static final RegistryObject<Enchantment> PROVIDENCE = ENCHANTMENTS.register("providence", ProvidenceEnchantment::new);
    public static final RegistryObject<Enchantment> SPLASH_PROTECTION = ENCHANTMENTS.register("splash_protection", SplashProtectionEnchantment::new);
    public static final RegistryObject<Enchantment> SWIFT_STRIKE = ENCHANTMENTS.register("swift_strike", SwiftStrikeEnchantment::new);
    public static final RegistryObject<Enchantment> CONTEXT = ENCHANTMENTS.register("context", ContextEnchantment::new);
    public static final RegistryObject<Enchantment> RAPIDITY = ENCHANTMENTS.register("rapidity", RapidityEnchantment::new);
    public static final RegistryObject<Enchantment> VENOMOUS = ENCHANTMENTS.register("venomous", VenomousEnchantment::new);
    public static final RegistryObject<Enchantment> PHASING = ENCHANTMENTS.register("phasing", PhasingCurse::new);
    public static final RegistryObject<Enchantment> FRAGILE = ENCHANTMENTS.register("fragile", FragileCurse::new);
    public static final RegistryObject<Enchantment> INTRUSIVE = ENCHANTMENTS.register("intrusive", IntrusiveCurse::new);
}