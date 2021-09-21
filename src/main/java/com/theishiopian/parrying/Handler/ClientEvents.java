package com.theishiopian.parrying.Handler;

import com.theishiopian.parrying.Client.BashParticle;
import com.theishiopian.parrying.Client.ParryParticle;
import com.theishiopian.parrying.Mechanics.DualWielding;
import com.theishiopian.parrying.Mechanics.Util;
import com.theishiopian.parrying.Network.DodgePacket;
import com.theishiopian.parrying.Network.LeftClickPacket;
import com.theishiopian.parrying.Network.SwingPacket;
import com.theishiopian.parrying.ParryingMod;
import com.theishiopian.parrying.Registration.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class ClientEvents
{
    public static final KeyBinding dodgeKey = new KeyBinding("key.parrying.dodge", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "key.categories.movement");

    static
    {
        ClientRegistry.registerKeyBinding(dodgeKey);
    }

    public static void OnRegisterParticlesEvent(ParticleFactoryRegisterEvent event)
    {
        Minecraft.getInstance().particleEngine.register(ModParticles.PARRY_PARTICLE.get(), ParryParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.STAB_PARTICLE.get(), ParryParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.BASH_PARTICLE.get(), BashParticle.Factory::new);
    }

    public static void OnLeftMouse(InputEvent.MouseInputEvent event)
    {
        if (Minecraft.getInstance().screen == null && Minecraft.getInstance().options.keyAttack.isDown())
        {
            ParryingMod.channel.sendToServer(new LeftClickPacket());
        }
    }

    //used to register a player as dual wielding
    //may want to put in an event here
    public static void OnPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side == LogicalSide.CLIENT)
        {
            if(Util.IsWeapon(event.player.getMainHandItem()) && Util.IsWeapon(event.player.getOffhandItem()))
            {
                DualWielding.IsDualWielding = true;
            }
            else
            {
                DualWielding.IsDualWielding = false;
                DualWielding.CurrentHand =Hand.MAIN_HAND;//reset hand
            }
        }
    }

    /*
     *this method is used to ensure that things like dodging and dual wield don't occur when, say, the inventory is open
     */
    private static boolean IsGameplayInProgress()
    {
        return Minecraft.getInstance().screen == null && !Minecraft.getInstance().isPaused() && Minecraft.getInstance().player != null;
    }

    public static void OnClick(InputEvent.ClickInputEvent event)
    {
        if(IsGameplayInProgress() && DualWielding.IsDualWielding)
        {
            event.setSwingHand(false);
            event.setCanceled(true);

            assert Minecraft.getInstance().player != null;
            PlayerEntity player = Minecraft.getInstance().player;

            RayTraceResult hit = Minecraft.getInstance().hitResult;

            EntityRayTraceResult target = hit instanceof EntityRayTraceResult ? (EntityRayTraceResult) hit : null;

            if(DualWielding.CurrentHand == Hand.OFF_HAND)
            {
                player.swing(Hand.OFF_HAND);

                ParryingMod.channel.sendToServer(new SwingPacket(false));
                DualWielding.CurrentHand = Hand.MAIN_HAND;
            }
            else
            {
                player.swing(Hand.MAIN_HAND);
                ParryingMod.channel.sendToServer(new SwingPacket(true));
                DualWielding.CurrentHand = Hand.OFF_HAND;
            }
        }
    }

    public static void OnKeyPressed(InputEvent.KeyInputEvent event)
    {
        if (IsGameplayInProgress() && dodgeKey.isDown())
        {
            if (event.getKey() == Minecraft.getInstance().options.keyAttack.getKey().getValue())
            {
                ParryingMod.channel.sendToServer(new LeftClickPacket());
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