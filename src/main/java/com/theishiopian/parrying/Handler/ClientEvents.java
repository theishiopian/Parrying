package com.theishiopian.parrying.Handler;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.theishiopian.parrying.Client.BashParticle;
import com.theishiopian.parrying.Client.ParryParticle;
import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Mechanics.DualWielding;
import com.theishiopian.parrying.Network.DodgePacket;
import com.theishiopian.parrying.Network.LeftClickPacket;
import com.theishiopian.parrying.Network.SwingPacket;
import com.theishiopian.parrying.ParryingMod;
import com.theishiopian.parrying.Registration.ModParticles;
import com.theishiopian.parrying.Registration.ModTags;
import com.theishiopian.parrying.Utility.ParryModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class ClientEvents
{
    public static final KeyMapping dodgeKey = new KeyMapping("key.parrying.dodge", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "key.categories.movement");

    static
    {
        ClientRegistry.registerKeyBinding(dodgeKey);
    }

    /**
     * This code will be used to render the defense meter. It is currently unfinished and will not be called
     * @param event the render event
     */
    public static void RenderDefense(RenderGameOverlayEvent.Post event)
    {
        if(IsGameplayInProgress() && event.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            Minecraft mc = Minecraft.getInstance();
            PoseStack stack = event.getMatrixStack();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1F, 1F, 1F, 0.5F);
            RenderSystem.setShaderTexture(0, ParryModUtil.GENERAL_ICONS);

            Window window = event.getWindow();
            int x = window.getGuiScaledWidth() / 2 + 20;
            int y = window.getGuiScaledHeight() / 2 - 8;
            int offset = 0;
            //Screen.blit(stack, x, y, offset * 16, 0, 16, 16, 256, 256);
            //TODO blit background first, then blit fill. scale fill using vHeight
        }
    }

    public static void OnTooltip(ItemTooltipEvent event)
    {
        //this MAY break when reloading resource packs, need more information
        if(event.getPlayer() != null && IsGameplayInProgress() && Config.twoHandedEnabled.get())
        {
            if(event.getItemStack().is(ModTags.TWO_HANDED_WEAPONS))
            {
                event.getToolTip().add(new TranslatableComponent("tag.parrying.two_handed").setStyle(Style.EMPTY.withColor((TextColor.fromLegacyFormat(ChatFormatting.RED)))));
            }
        }
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
            Player player = Minecraft.getInstance().player;

            if(Config.dualWieldEnabled.get())
            {
                if(DualWielding.IsDualWielding(player))
                {
                    event.setSwingHand(false);
                    event.setCanceled(true);

                    if(DualWielding.CurrentHand == InteractionHand.OFF_HAND)
                    {
                        player.swing(InteractionHand.OFF_HAND, false);

                        ParryingMod.channel.sendToServer(new SwingPacket(false));
                        DualWielding.CurrentHand = InteractionHand.MAIN_HAND;
                    }
                    else
                    {
                        player.swing(InteractionHand.MAIN_HAND, false);
                        ParryingMod.channel.sendToServer(new SwingPacket(true));
                        DualWielding.CurrentHand = InteractionHand.OFF_HAND;
                    }

                    player.resetAttackStrengthTicker();
                }
                else
                {
                    if(player.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(ForgeMod.REACH_DISTANCE.get()))
                    {
                        EntityHitResult target = ParryModUtil.GetAttackTargetWithRange(player.getMainHandItem(), player);
                        if(target != null)Minecraft.getInstance().hitResult = target;
                    }
                }
            }
        }
    }

    public static void OnKeyPressed(InputEvent.KeyInputEvent event)
    {
        if (IsGameplayInProgress() && event.getKey() == Minecraft.getInstance().options.keyAttack.getKey().getValue())
        {
            ParryingMod.channel.sendToServer(new LeftClickPacket());
        }

        if (IsGameplayInProgress() && dodgeKey.isDown())
        {
            boolean left = Minecraft.getInstance().options.keyLeft.isDown();
            boolean right = Minecraft.getInstance().options.keyRight.isDown();
            boolean back = Minecraft.getInstance().options.keyDown.isDown();


            if(left || right || back)ParryingMod.channel.sendToServer(new DodgePacket(left, right, back));
        }
    }
}