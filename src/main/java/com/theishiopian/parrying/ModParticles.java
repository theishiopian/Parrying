package com.theishiopian.parrying;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "parrying");
    public static final RegistryObject<BasicParticleType> PARRY_PARTICLE = PARTICLE_TYPES.register("parry", () -> new BasicParticleType(true));
}
