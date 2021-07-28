package com.theishiopian.parrying.Handler;

import com.theishiopian.parrying.Network.BashPacket;
import com.theishiopian.parrying.Network.DodgePacket;
import com.theishiopian.parrying.Registration.ModParticles;
import com.theishiopian.parrying.*;
import com.theishiopian.parrying.Client.BashParticle;
import com.theishiopian.parrying.Client.ParryParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class ClientEvents
{
    public static KeyBinding dodgeKey = new KeyBinding("key.dodge", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "key.parrying.dodge");

    public static void ClientSetup(final FMLClientSetupEvent event)
    {
        ClientRegistry.registerKeyBinding(dodgeKey);
    }

    public static void OnRegisterParticlesEvent(ParticleFactoryRegisterEvent event)
    {
        Minecraft.getInstance().particleEngine.register(ModParticles.PARRY_PARTICLE.get(), ParryParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.STAB_PARTICLE.get(), ParryParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.BASH_PARTICLE.get(), BashParticle.Factory::new);
    }

    public static void OnClick(InputEvent.MouseInputEvent event)
    {
        if (Minecraft.getInstance().screen == null && Minecraft.getInstance().options.keyAttack.isDown())
        {
            ParryingMod.channel.sendToServer(new BashPacket());
        }
    }

    public static void OnKeyPressed(InputEvent.KeyInputEvent event)
    {
        if (Minecraft.getInstance().screen == null && !Minecraft.getInstance().isPaused() && Minecraft.getInstance().player != null && dodgeKey.isDown())
        {
            if (event.getKey() == Minecraft.getInstance().options.keyAttack.getKey().getValue())
            {
                ParryingMod.channel.sendToServer(new BashPacket());
            }

            if (event.getKey() == Minecraft.getInstance().options.keyDown.getKey().getValue() && event.getAction() == 1)
            {
                ParryingMod.channel.sendToServer(new DodgePacket(2));
            }

            if (event.getKey() == Minecraft.getInstance().options.keyLeft.getKey().getValue() && event.getAction() == 1)
            {
                ParryingMod.channel.sendToServer(new DodgePacket(1));
            }

            if (event.getKey() == Minecraft.getInstance().options.keyRight.getKey().getValue() && event.getAction() == 1)
            {
                ParryingMod.channel.sendToServer(new DodgePacket(3));
            }
        }
    }
}