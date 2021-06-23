package com.theishiopian.parrying.Handler;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Handler.Network.BashPacket;
import com.theishiopian.parrying.Handler.Network.DodgePacket;
import com.theishiopian.parrying.Registration.ModParticles;
import com.theishiopian.parrying.*;
import com.theishiopian.parrying.Client.BashParticle;
import com.theishiopian.parrying.Client.ParryParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.TickEvent;

public class ClientEvents
{
    public static void OnRegisterParticlesEvent(ParticleFactoryRegisterEvent event)
    {
        Minecraft.getInstance().particleEngine.register(ModParticles.PARRY_PARTICLE.get(), ParryParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.STAB_PARTICLE.get(), ParryParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.BASH_PARTICLE.get(), BashParticle.Factory::new);
    }

    public static void OnClick(InputEvent.MouseInputEvent event)
    {
        if(Minecraft.getInstance().options.keyAttack.isDown())
        {
            ParryingMod.channel.sendToServer(new BashPacket());
        }
    }

    public static void OnKeyPressed(InputEvent.KeyInputEvent event)
    {
        if(event.getKey() == Minecraft.getInstance().options.keyAttack.getKey().getValue())
        {
            ParryingMod.channel.sendToServer(new BashPacket());
        }

        if(event.getKey() == Minecraft.getInstance().options.keyDown.getKey().getValue() && event.getAction() == 1)
        {
            if(dodgeBackTime <= 0)
            {
                dodgeBackTime = Config.dodgeTriggerDelay.get();
            }
            else
            {
                dodgeBackTime = 0;
                ParryingMod.channel.sendToServer(new DodgePacket(2));
            }
        }

        if(event.getKey() == Minecraft.getInstance().options.keyLeft.getKey().getValue() && event.getAction() == 1)
        {
            if(dodgeLeftTime <= 0)
            {
                dodgeLeftTime = Config.dodgeTriggerDelay.get();
            }
            else
            {
                dodgeLeftTime = 0;
                ParryingMod.channel.sendToServer(new DodgePacket(1));
            }
        }

        if(event.getKey() == Minecraft.getInstance().options.keyRight.getKey().getValue() && event.getAction() == 1)
        {
            if(dodgeRightTime <= 0)
            {
                dodgeRightTime = Config.dodgeTriggerDelay.get();
            }
            else
            {
                dodgeRightTime = 0;
                ParryingMod.channel.sendToServer(new DodgePacket(3));
            }
        }
    }

    public static int dodgeBackTime = 0;
    public static int dodgeLeftTime = 0;
    public static int dodgeRightTime = 0;

    public static void OnPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.player instanceof ClientPlayerEntity)
        {
            if(dodgeBackTime > 0)
            {
                --dodgeBackTime;
            }

            if(dodgeLeftTime > 0)
            {
                --dodgeLeftTime;
            }

            if(dodgeRightTime > 0)
            {
                --dodgeRightTime;
            }
        }
    }
}