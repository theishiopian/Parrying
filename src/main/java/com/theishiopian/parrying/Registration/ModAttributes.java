package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.Enchantment.DeflectingEnchantment;
import com.theishiopian.parrying.ParryingMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class ModAttributes
{
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, ParryingMod.MOD_ID);

    public static final UUID AP_UUID = UUID.fromString("42f502a6-5bd5-4c7b-9043-3cf5d484b049");
    public static final RegistryObject<Attribute> AP = ATTRIBUTES.register("armor_pen", () -> new RangedAttribute("attribute.name.parrying.armor_pen", 0, 0, 100).setSyncable(true));
}
