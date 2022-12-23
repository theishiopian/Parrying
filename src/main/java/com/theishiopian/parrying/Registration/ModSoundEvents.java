package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.ParryingMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * This class is used to register custom sound events.
 */
public class ModSoundEvents
{
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ParryingMod.MOD_ID);

    public static final RegistryObject<SoundEvent> PARRY_WOOD = registerSoundEvent("parry_wood");
    public static final RegistryObject<SoundEvent> PARRY_STONE = registerSoundEvent("parry_stone");
    public static final RegistryObject<SoundEvent> PARRY_METAL = registerSoundEvent("parry_metal");
    public static final RegistryObject<SoundEvent> SHIELD_BASH = registerSoundEvent("shield_bash");
    public static final RegistryObject<SoundEvent> SHIELD_BASH_MISS = registerSoundEvent("shield_bash_miss");
    public static final RegistryObject<SoundEvent> FLAIL_SWING = registerSoundEvent("flail_swing");
    public static final RegistryObject<SoundEvent> DEFENSE_BREAK = registerSoundEvent("defense_break");
    public static final RegistryObject<SoundEvent> DRAW_SWORD = registerSoundEvent("draw_sword");
    public static final RegistryObject<SoundEvent> SHEATHE_SWORD = registerSoundEvent("sheathe_sword");
    public static final RegistryObject<SoundEvent> SWIFT_STRIKE = registerSoundEvent("swift_strike");
    public static final RegistryObject<SoundEvent> CLEANSE = registerSoundEvent("cleanse");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name)
    {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(ParryingMod.MOD_ID, name)));
    }
}