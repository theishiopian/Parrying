package com.theishiopian.parrying.Handler;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.theishiopian.parrying.Client.BashParticle;
import com.theishiopian.parrying.Client.ParryParticle;
import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Items.ScopedCrossbow;
import com.theishiopian.parrying.Mechanics.DualWielding;
import com.theishiopian.parrying.Mechanics.ParryingMechanic;
import com.theishiopian.parrying.Network.DodgePacket;
import com.theishiopian.parrying.Network.LeftClickPacket;
import com.theishiopian.parrying.Network.SwingPacket;
import com.theishiopian.parrying.ParryingMod;
import com.theishiopian.parrying.Registration.ModEffects;
import com.theishiopian.parrying.Registration.ModItems;
import com.theishiopian.parrying.Registration.ModParticles;
import com.theishiopian.parrying.Registration.ModTags;
import com.theishiopian.parrying.Utility.Debug;
import com.theishiopian.parrying.Utility.ParryModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
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

    public static void OnHandRendered(RenderHandEvent event)
    {
        Player player = Minecraft.getInstance().player;
        assert player != null;
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offHandItem = player.getOffhandItem();

        if(player.isUsingItem())
        {
            if(event.getHand() != player.getUsedItemHand())
            {
                event.setCanceled(true);
            }
        }
        else if(event.getHand() == InteractionHand.OFF_HAND && (isChargedScopedCrossbow(mainHandItem) || isChargedCrossbow(mainHandItem) || mainHandItem.is(Items.BOW)))
        {
            event.setCanceled(true);
        }
    }

    private static boolean isChargedCrossbow(ItemStack pStack)
    {
        return pStack.is(Items.CROSSBOW) && CrossbowItem.isCharged(pStack);
    }

    private static boolean isChargedScopedCrossbow(ItemStack pStack)
    {
        return pStack.is(ModItems.SCOPED_CROSSBOW.get()) && ScopedCrossbow.isCharged(pStack);
    }

    /**
     * This code will be used to render the defense meter. TODO: move to custom class for rendering events
     * @param event the render event
     */
    public static void RenderDefense(RenderGameOverlayEvent.Post event)
    {
        if(IsGameplayInProgress(true) && event.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            if(ParryingMechanic.ClientDefense < 1)
            {
                PoseStack matrixStack = event.getMatrixStack();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1F, 1F, 1F, 0.5F);
                RenderSystem.setShaderTexture(0, ParryModUtil.GENERAL_ICONS);
                Window window = event.getWindow();
                int x = (window.getGuiScaledWidth() / 2) - 8;
                int y = (window.getGuiScaledHeight() / 2) + 16;
                //stack, position, uv, size, texture size
                Player player = Minecraft.getInstance().player;
                int stunOffset = player != null && player.hasEffect(ModEffects.STUNNED.get()) ? 16 : 0;
                Screen.blit(matrixStack, x, y, 0, stunOffset, 16, 16, 64, 64);
                int posOffset = (int)(16 + y - (16* ParryingMechanic.ClientDefense));
                int uvOffset = (int)(16 * ParryingMechanic.ClientDefense);
                int sizeOffset = (int)(16 * ParryingMechanic.ClientDefense) + 1;
                Screen.blit(matrixStack, x, posOffset, 16, (15 - uvOffset) + stunOffset, 16, sizeOffset, 64, 64);
            }
        }
    }

    public static void OnTooltip(ItemTooltipEvent event)
    {
        Debug.log("entered");
        //this MAY break when reloading resource packs, need more information
        if(!IsGameplayInProgress(false)) return;
        Debug.log("ok to render");
        if(Config.twoHandedEnabled.get() && event.getItemStack().is(ModTags.TWO_HANDED_WEAPONS))
        {
            Debug.log("rendering");
            event.getToolTip().add(new TranslatableComponent("tag.parrying.two_handed").setStyle(Style.EMPTY.withColor((TextColor.fromLegacyFormat(ChatFormatting.RED)))));
        }
    }

    public static void OnRegisterParticlesEvent(ParticleFactoryRegisterEvent event)
    {
        Minecraft.getInstance().particleEngine.register(ModParticles.PARRY_PARTICLE.get(), ParryParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.STAB_PARTICLE.get(), ParryParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.BASH_PARTICLE.get(), BashParticle.Factory::new);
    }

    /**
     * This event is used to trigger a shield bash on the server.
     * It is separate from the OnAttack event because that event does not fire when left-clicking with a shield.
     * @param event the event data.
     */
    public static void OnLeftMouse(InputEvent.MouseInputEvent event)
    {
        if(!IsGameplayInProgress(true)) return;
        if (Minecraft.getInstance().options.keyAttack.isDown())
        {
            ParryingMod.channel.sendToServer(new LeftClickPacket());
        }
    }

    /*
     *this method is used to ensure that things like dodging and dual wield don't occur when, say, the inventory is open
     */
    private static boolean IsGameplayInProgress(boolean noInventory)
    {
        boolean a = Minecraft.getInstance().screen == null;
        boolean b = Minecraft.getInstance().level != null;
        boolean c = !Minecraft.getInstance().isPaused();
        boolean d = Minecraft.getInstance().player != null;

//        Debug.log("GAMEPLAY CHECK");
//        Debug.log(a);
//        Debug.log(b);
//        Debug.log(c);
//        Debug.log(d);
//        Debug.log("END GAMEPLAY CHECK");

        return (a || !noInventory) && b && c && d;
    }

    @OnlyIn(Dist.CLIENT)
    public static void OnAttack(InputEvent.ClickInputEvent event)
    {
        if(IsGameplayInProgress(true) && event.isAttack())
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
        if (IsGameplayInProgress(true) && event.getKey() == Minecraft.getInstance().options.keyAttack.getKey().getValue())
        {
            ParryingMod.channel.sendToServer(new LeftClickPacket());
        }

        if (IsGameplayInProgress(true) && dodgeKey.isDown())
        {
            boolean left = Minecraft.getInstance().options.keyLeft.isDown();
            boolean right = Minecraft.getInstance().options.keyRight.isDown();
            boolean back = Minecraft.getInstance().options.keyDown.isDown();


            if(left || right || back)ParryingMod.channel.sendToServer(new DodgePacket(left, right, back));
        }
    }
}