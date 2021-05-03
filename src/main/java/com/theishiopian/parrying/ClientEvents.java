package com.theishiopian.parrying;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;

public class ClientEvents
{
    public static void OnRegisterParticles(ParticleFactoryRegisterEvent event)
    {
        ParryingMod.LOGGER.info("HEY LISTEN !!!!!!!!!!!");
        Minecraft.getInstance().particleEngine.register(ParryingMod.PARRY_PARTICLE.get(), ParryParticle.Factory::new);
    }
}
