package com.theishiopian.parrying.Handler;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Network.BashPacket;
import com.theishiopian.parrying.Network.DodgePacket;
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
        if(Minecraft.getInstance().screen == null && Minecraft.getInstance().options.keyAttack.isDown())
        {
            ParryingMod.channel.sendToServer(new BashPacket());
        }
    }

    public static void OnKeyPressed(InputEvent.KeyInputEvent event)
    {
        if(Minecraft.getInstance().screen == null && !Minecraft.getInstance().isPaused() && Minecraft.getInstance().player != null)
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
                    dbc++;
                }
                else if(dbc < 2)
                {
                    dbc++;
                }
                else
                {
                    dodgeBackTime = 0;
                    ParryingMod.channel.sendToServer(new DodgePacket(2));
                    dbc = 0;
                }
            }

            if(event.getKey() == Minecraft.getInstance().options.keyLeft.getKey().getValue() && event.getAction() == 1)
            {
                if(dodgeLeftTime <= 0)
                {
                    dodgeLeftTime = Config.dodgeTriggerDelay.get();
                    dlc++;
                }
                else if(dlc < 2)
                {
                    dlc++;
                }
                else
                {
                    dodgeLeftTime = 0;
                    ParryingMod.channel.sendToServer(new DodgePacket(1));
                    dlc = 0;
                }
            }

            if(event.getKey() == Minecraft.getInstance().options.keyRight.getKey().getValue() && event.getAction() == 1)
            {
                if(dodgeRightTime <= 0)
                {
                    dodgeRightTime = Config.dodgeTriggerDelay.get();
                    drc++;
                }
                else if(drc < 2)
                {
                    drc++;
                }
                else
                {
                    dodgeRightTime = 0;
                    ParryingMod.channel.sendToServer(new DodgePacket(3));
                    drc = 0;
                }
            }
        }
    }

    public static int dodgeBackTime = 0;
    public static int dbc = 0;

    public static int dodgeLeftTime = 0;
    public static int dlc = 0;

    public static int dodgeRightTime = 0;
    public static int drc = 0;



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