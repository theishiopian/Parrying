package com.theishiopian.parrying;

import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEffects
{
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, ParryingMod.MOD_ID);

    public static final RegistryObject<Effect> STUNNED = EFFECTS.register("stunned", StunnedEffect::new);
}
