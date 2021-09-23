package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.Effects.StunnedEffect;
import com.theishiopian.parrying.ParryingMod;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * This class is used to register custom potion effects.
 */
public class ModEffects
{
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, ParryingMod.MOD_ID);

    public static final RegistryObject<Effect> STUNNED = EFFECTS.register("stunned", StunnedEffect::new);
}