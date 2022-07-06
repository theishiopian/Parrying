package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.ParryingMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * This class is used to register custom particles.
 */
public class ModParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ParryingMod.MOD_ID);
    public static final RegistryObject<SimpleParticleType> PARRY_PARTICLE = PARTICLE_TYPES.register("parry", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> STAB_PARTICLE = PARTICLE_TYPES.register("stab", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BASH_PARTICLE = PARTICLE_TYPES.register("bash", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SLICE_PARTICLE = PARTICLE_TYPES.register("slice", () -> new SimpleParticleType(true));
}
