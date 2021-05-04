package com.theishiopian.parrying;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEnchantments
{
    //public static final Enchantment DEFLECTING = new DeflectingEnchantment();
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ParryingMod.MOD_ID);

    public static final RegistryObject<Enchantment> DEFLECTING = ENCHANTMENTS.register("deflection", DeflectingEnchantment::new);
}
