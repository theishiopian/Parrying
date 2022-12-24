package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.Effects.CoalescenceEffect;
import com.theishiopian.parrying.Effects.FortifiedEffect;
import com.theishiopian.parrying.Effects.ImmortalityEffect;
import com.theishiopian.parrying.Effects.StunnedEffect;
import com.theishiopian.parrying.ParryingMod;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * This class is used to register custom potion effects.
 */
public class ModEffects
{
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ParryingMod.MOD_ID);

    public static final RegistryObject<MobEffect> STUNNED = EFFECTS.register("stunned", StunnedEffect::new);
    public static final RegistryObject<MobEffect> FORTIFIED = EFFECTS.register("fortified", FortifiedEffect::new);
    public static final RegistryObject<MobEffect> COALESCENCE = EFFECTS.register("coalescence", CoalescenceEffect::new);
    public static final RegistryObject<MobEffect> IMMORTALITY = EFFECTS.register("immortality", ImmortalityEffect::new);
}