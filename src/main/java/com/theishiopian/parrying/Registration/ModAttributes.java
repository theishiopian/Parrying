package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.ParryingMod;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

/**
 * This class defines custom weapon attributes, such as armor penetration.
 */
public class ModAttributes
{
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, ParryingMod.MOD_ID);

    public static final UUID AP_UUID = UUID.fromString("42f502a6-5bd5-4c7b-9043-3cf5d484b049");
    public static final UUID SP_UUID = UUID.fromString("7f4345c3-9c54-49d9-bdef-bd68e377ea4b");
    public static final UUID IR_UUID = UUID.fromString("47b15660-c370-41ad-b755-390eaceb1448");
    public static final UUID RD_UUID = UUID.fromString("080e0e27-d125-4473-8f77-7e154d821e83");//UUID for the forge reach attribute

    public static final RegistryObject<Attribute> AP = ATTRIBUTES.register("armor_pen", () -> new RangedAttribute("attribute.name.parrying.armor_pen", 0, 0, 100).setSyncable(true));
    public static final RegistryObject<Attribute> SP = ATTRIBUTES.register("shield_pen", () -> new RangedAttribute("attribute.name.parrying.shield_pen", 0, 0, 100).setSyncable(true));
    public static final RegistryObject<Attribute> IR = ATTRIBUTES.register("inv_reduction", () -> new RangedAttribute("attribute.name.parrying.inv_reduction", 0, 0, 10).setSyncable(true));
}
