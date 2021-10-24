package com.theishiopian.parrying.Handler;

import com.theishiopian.parrying.Client.BashParticle;
import com.theishiopian.parrying.Client.ParryParticle;
import com.theishiopian.parrying.Mechanics.DualWielding;
import com.theishiopian.parrying.Network.DodgePacket;
import com.theishiopian.parrying.Network.LeftClickPacket;
import com.theishiopian.parrying.Network.SwingPacket;
import com.theishiopian.parrying.ParryingMod;
import com.theishiopian.parrying.Registration.ModParticles;
import com.theishiopian.parrying.Utility.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.ForgeMod;
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

    /*
     *this method is used to ensure that things like dodging and dual wield don't occur when, say, the inventory is open
     */
    private static boolean IsGameplayInProgress()
    {
        return Minecraft.getInstance().screen == null && !Minecraft.getInstance().isPaused() && Minecraft.getInstance().player != null;
    }

    @OnlyIn(Dist.CLIENT)
    public static void OnClick(InputEvent.ClickInputEvent event)
    {
        if(IsGameplayInProgress() && event.isAttack())
        {
            assert Minecraft.getInstance().player != null;//gameplay check should take care of this. I hope.
            PlayerEntity player = Minecraft.getInstance().player;
            //Debug.log("this should not be on the server");
            if(DualWielding.IsDualWielding(player))
            {
                event.setSwingHand(false);
                event.setCanceled(true);

                if(DualWielding.CurrentHand == Hand.OFF_HAND)
                {
                    player.swing(Hand.OFF_HAND, false);

                    ParryingMod.channel.sendToServer(new SwingPacket(false));
                    DualWielding.CurrentHand = Hand.MAIN_HAND;
                }
                else
                {
                    player.swing(Hand.MAIN_HAND, false);
                    ParryingMod.channel.sendToServer(new SwingPacket(true));
                    DualWielding.CurrentHand = Hand.OFF_HAND;
                }

                player.resetAttackStrengthTicker();
            }
            else
            {
                if(player.getMainHandItem().getAttributeModifiers(EquipmentSlotType.MAINHAND).containsKey(ForgeMod.REACH_DISTANCE.get()))
                {
                    EntityRayTraceResult target = Util.GetAttackTargetWithRange(player.getMainHandItem(), player);
                    if(target != null)Minecraft.getInstance().hitResult = target;
                }
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