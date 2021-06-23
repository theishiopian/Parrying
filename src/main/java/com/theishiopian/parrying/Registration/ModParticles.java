package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.ParryingMod;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ParryingMod.MOD_ID);
    public static final RegistryObject<BasicParticleType> PARRY_PARTICLE = PARTICLE_TYPES.register("parry", () -> new BasicParticleType(true));
    public static final RegistryObject<BasicParticleType> STAB_PARTICLE = PARTICLE_TYPES.register("stab", () -> new BasicParticleType(true));
    public static final RegistryObject<BasicParticleType> BASH_PARTICLE = PARTICLE_TYPES.register("bash", () -> new BasicParticleType(true));
}
