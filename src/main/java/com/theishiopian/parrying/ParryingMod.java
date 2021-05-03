package com.theishiopian.parrying;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ParryingMod.MOD_ID)
public class ParryingMod
{
    // Directly reference a log4j logger.
    public static final String MOD_ID = "parrying";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "parrying");
    public static final RegistryObject<BasicParticleType> PARRY_PARTICLE = PARTICLE_TYPES.register("parry", () -> new BasicParticleType(true));

    public ParryingMod()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnAttackedEvent);
        PARTICLE_TYPES.register(bus);
        ModSoundEvents.SOUND_EVENTS.register(bus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        {
            bus.addListener(ClientEvents::OnRegisterParticles);
        });
    }
}
