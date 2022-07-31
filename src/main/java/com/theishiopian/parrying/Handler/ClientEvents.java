package com.theishiopian.parrying.Handler;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.theishiopian.parrying.Client.BashParticle;
import com.theishiopian.parrying.Client.ParryParticle;
import com.theishiopian.parrying.Client.SliceParticle;
import com.theishiopian.parrying.Client.StabParticle;
import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Items.ScabbardItem;
import com.theishiopian.parrying.Items.ScopedCrossbow;
import com.theishiopian.parrying.Items.SpearItem;
import com.theishiopian.parrying.Mechanics.DualWieldingMechanic;
import com.theishiopian.parrying.Mechanics.ParryingMechanic;
import com.theishiopian.parrying.Network.DodgePacket;
import com.theishiopian.parrying.Network.DualWieldPacket;
import com.theishiopian.parrying.Network.LeftClickPacket;
import com.theishiopian.parrying.Network.UseScabbardPacket;
import com.theishiopian.parrying.ParryingMod;
import com.theishiopian.parrying.Registration.*;
import com.theishiopian.parrying.Utility.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class ClientEvents
{
    public static final KeyMapping dodgeKey = new KeyMapping("key.parrying.dodge", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "key.categories.movement");
    public static final KeyMapping drawKey = new KeyMapping("key.parrying.draw", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.gameplay");

    static
    {
        ClientRegistry.registerKeyBinding(dodgeKey);
    }

    public static void OnHandRendered(RenderHandEvent event)
    {
        Player player = Minecraft.getInstance().player;
        assert player != null;
        ItemStack mainHandItem = player.getMainHandItem();

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
    public static void RenderOverlays(RenderGameOverlayEvent.Post event)
    {
        if(IsGameplayInProgress(true) && event.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            Player player = Minecraft.getInstance().player;
            assert player != null : "Null player in overlay renderer!";
            if(Config.parryEnabled.get() && ParryingMechanic.ClientDefense < 1)
            {
                PoseStack matrixStack = event.getMatrixStack();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1F, 1F, 1F, 0.5F);
                RenderSystem.setShaderTexture(0, ModUtil.GENERAL_ICONS);
                Window window = event.getWindow();
                int x = (window.getGuiScaledWidth() / 2) - 8;
                int y = (window.getGuiScaledHeight() / 2) + 16;
                int stunOffset = player.hasEffect(ModEffects.STUNNED.get()) ? 16 : 0;
                Screen.blit(matrixStack, x, y, 0, stunOffset, 16, 16, 64, 64);
                int posOffset = (int)(16 + y - (16* ParryingMechanic.ClientDefense));
                int uvOffset = (int)(16 * ParryingMechanic.ClientDefense);
                int sizeOffset = (int)(16 * ParryingMechanic.ClientDefense) + 1;
                //stack, position, uv, size, texture size
                Screen.blit(matrixStack, x, posOffset, 16, (15 - uvOffset) + stunOffset, 16, sizeOffset, 64, 64);
            }

            if(Config.dualWieldEnabled.get() && DualWieldingMechanic.IsDualWielding(player))
            {
                PoseStack matrixStack = event.getMatrixStack();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1F, 1F, 1F, 0.5F);
                RenderSystem.setShaderTexture(0, ModUtil.GENERAL_ICONS);
                Window window = event.getWindow();
                boolean offhand = DualWieldingMechanic.CurrentHand == InteractionHand.OFF_HAND;
                int x = (window.getGuiScaledWidth() / 2) - (8 + (offhand ? 16 : -16));
                int y = (window.getGuiScaledHeight() / 2) - 8;

                Screen.blit(matrixStack, x, y, offhand ? 32 : 48, 0, 16, 16, 64, 64);
            }
        }
    }

    /**
     * This method is used to ensure mechanics don't occur when, say, we are in the main menu
     * @param noInventory whether to consider the player's inventory being open as something to cancel mechanics
     * @return is gameplay in progress?
     */
    private static boolean IsGameplayInProgress(boolean noInventory)
    {
        boolean a = Minecraft.getInstance().screen == null;
        boolean b = Minecraft.getInstance().level != null;
        boolean c = !Minecraft.getInstance().isPaused();
        boolean d = Minecraft.getInstance().player != null;

        return (a || !noInventory) && b && c && d;
    }

    public static void OnTooltip(ItemTooltipEvent event)
    {
        if(!IsGameplayInProgress(false)) return;

        if(event.getItemStack().getItem() instanceof SpearItem)
        {
            int lvl = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.JOUSTING.get(), event.getItemStack());

            if(lvl > 0)
            {
                event.getToolTip().add(new TranslatableComponent("tooltip.parrying.jousting", lvl * 2).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
            }
        }

        if (Registry.ITEM.isKnownTagName(ModTags.TWO_HANDED_WEAPONS) && Config.twoHandedEnabled.get() && event.getItemStack().is(ModTags.TWO_HANDED_WEAPONS))
        {
            event.getToolTip().add(new TranslatableComponent("tooltip.parrying.two_handed").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED)));
        }

        if(event.getItemStack().is(ModItems.SCABBARD.get()))
        {
            event.getToolTip().addAll(ScabbardItem.GetTooltipComponents(event.getPlayer(), event.getItemStack(), event.getFlags()));
        }
    }

    public static void OnRegisterParticlesEvent(ParticleFactoryRegisterEvent event)
    {
        Minecraft.getInstance().particleEngine.register(ModParticles.PARRY_PARTICLE.get(), ParryParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.STAB_PARTICLE.get(), StabParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.BASH_PARTICLE.get(), BashParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.SLICE_PARTICLE.get(), SliceParticle.Factory::new);
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

    @OnlyIn(Dist.CLIENT)
    public static void OnAttack(InputEvent.ClickInputEvent event)
    {
        if(IsGameplayInProgress(true) && event.isAttack())
        {
            assert Minecraft.getInstance().player != null;//gameplay check should take care of this. I hope.

            Player player = Minecraft.getInstance().player;

            if(Config.dualWieldEnabled.get())
            {
                if(DualWieldingMechanic.IsDualWielding(player))
                {
                    event.setSwingHand(false);
                    event.setCanceled(true);

                    Entity target = Minecraft.getInstance().crosshairPickEntity;

                    int targetID = Integer.MIN_VALUE;

                    if(target != null)targetID = target.getId();

                    if(DualWieldingMechanic.CurrentHand == InteractionHand.OFF_HAND)
                    {
                        player.swing(InteractionHand.OFF_HAND, false);

                        ParryingMod.channel.sendToServer(new DualWieldPacket(false, targetID));
                        DualWieldingMechanic.CurrentHand = InteractionHand.MAIN_HAND;
                    }
                    else
                    {
                        player.swing(InteractionHand.MAIN_HAND, false);
                        ParryingMod.channel.sendToServer(new DualWieldPacket(true, targetID));
                        DualWieldingMechanic.CurrentHand = InteractionHand.OFF_HAND;
                    }

                    player.resetAttackStrengthTicker();
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

        if(IsGameplayInProgress(true) && drawKey.isDown())
        {
            ParryingMod.channel.sendToServer(new UseScabbardPacket());
        }
    }
}