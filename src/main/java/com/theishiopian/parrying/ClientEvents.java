package com.theishiopian.parrying;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;

public class ClientEvents
{
    public static void OnRegisterParticles(ParticleFactoryRegisterEvent event)
    {
        Minecraft.getInstance().particleEngine.register(ModParticles.PARRY_PARTICLE.get(), ParryParticle.Factory::new);
    }

    public static void OnLeftClick(InputEvent.MouseInputEvent event)
    {
        if(Minecraft.getInstance().options.keyAttack.isDown())
        {
            ParryingMod.channel.sendToServer(new BashPacket());
        }
    }
}
