package com.theishiopian.parrying;

import com.theishiopian.parrying.Client.Render.RenderDagger;
import com.theishiopian.parrying.Client.Render.RenderSpear;
import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Handler.ClientEvents;
import com.theishiopian.parrying.Handler.CommonEvents;
import com.theishiopian.parrying.Network.*;
import com.theishiopian.parrying.Recipes.CustomBrewingRecipe;
import com.theishiopian.parrying.Recipes.EnabledCondition;
import com.theishiopian.parrying.Registration.*;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * This is the main mod class. There's not much to say about it.
 */
@Mod(ParryingMod.MOD_ID)
public class ParryingMod
{
    public static final String MOD_ID = "parrying";
    public static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation netName = new ResourceLocation(MOD_ID, "network");
    public static final SimpleChannel channel;
    private static final int VERSION = 16;  //protocol version, bump whenever adding new network packets or changing existing ones.
                                            // last change: added fireball packet

    static
    {
        channel = NetworkRegistry.ChannelBuilder.named(netName)
                .clientAcceptedVersions(s -> Objects.equals(s, String.valueOf(VERSION)))
                .serverAcceptedVersions(s -> Objects.equals(s, String.valueOf(VERSION)))
                .networkProtocolVersion(() -> String.valueOf(VERSION))
                .simpleChannel();

        channel.messageBuilder(LeftClickPacket.class, 1)
                .decoder(LeftClickPacket::fromBytes)
                .encoder(LeftClickPacket::toBytes)
                .consumer(LeftClickPacket::handle)
                .add();

        channel.messageBuilder(DodgePacket.class, 2)
                .decoder(DodgePacket::fromBytes)
                .encoder(DodgePacket::toBytes)
                .consumer(DodgePacket::handle)
                .add();

        channel.messageBuilder(DualWieldPacket.class, 3)
                .decoder(DualWieldPacket::fromBytes)
                .encoder(DualWieldPacket::toBytes)
                .consumer(DualWieldPacket::handle)
                .add();

        channel.messageBuilder(SyncDefPacket.class, 4, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncDefPacket::fromBytes)
                .encoder(SyncDefPacket::toBytes)
                .consumer(SyncDefPacket::handle)
                .add();

        channel.messageBuilder(QuiverAdvPacket.class, 5)
                .decoder(QuiverAdvPacket::fromBytes)
                .encoder(QuiverAdvPacket::toBytes)
                .consumer(QuiverAdvPacket::handle)
                .add();

        channel.messageBuilder(UseScabbardPacket.class, 6)
                .decoder(UseScabbardPacket::fromBytes)
                .encoder(UseScabbardPacket::toBytes)
                .consumer(UseScabbardPacket::handle)
                .add();

        channel.messageBuilder(GameplayStatusPacket.class, 7)
                .decoder(GameplayStatusPacket::fromBytes)
                .encoder(GameplayStatusPacket::toBytes)
                .consumer(GameplayStatusPacket::handle)
                .add();

        channel.messageBuilder(SyncFireballPacket.class, 8, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncFireballPacket::fromBytes)
                .encoder(SyncFireballPacket::toBytes)
                .consumer(SyncFireballPacket::handle)
                .add();
    }

    public ParryingMod()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(CommonEvents::OnAddPackFinders);

        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnRegisterCapabilities);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnRegisterTrades);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnRegisterWanderingTrades);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnAttacked);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnEntityJoin);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnEntityLeave);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnPlayerAttackTarget);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnProjectileImpact);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnHurtEvent);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnWorldTick);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnLivingTick);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnPlayerJoin);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnPlayerLeave);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnDeath);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnHitBlock);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnArrowScan);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnPotionEffectAdded);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnRightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnHeal);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnEatStew);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnLivingKnockback);

        ModTriggers.Init();
        ModItems.ITEMS.register(bus);
        ModItems.VANILLA_ITEM_OVERRIDES.register(bus);
        ModLootModifiers.GLM.register(bus);
        ModParticles.PARTICLE_TYPES.register(bus);
        ModSoundEvents.SOUND_EVENTS.register(bus);
        ModEnchantments.ENCHANTMENTS.register(bus);
        ModEffects.EFFECTS.register(bus);
        ModPotions.POTIONS.register(bus);
        ModEntities.ENTITY_TYPES.register(bus);
        ModAttributes.ATTRIBUTES.register(bus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        {
            bus.addListener(ClientEvents::OnRegisterParticlesEvent);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::ClientSetup);
        });

        var eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::CommonSetup);
    }

    @OnlyIn(Dist.CLIENT)
    public void ClientSetup(FMLClientSetupEvent event)
    {
        ModItems.RegisterOverrides();
        ModItems.RegisterColorHandlers();//dyeable items

        MinecraftForge.EVENT_BUS.addListener(ClientEvents::OnLeftMouse);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::OnKeyPressed);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::OnAttack);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::OnTooltip);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::RenderOverlays);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::OnHandRendered);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::OnClientTick);
        EntityRenderers.register(ModEntities.SPEAR.get(), RenderSpear::new);
        EntityRenderers.register(ModEntities.DAGGER.get(), RenderDagger::new);
    }

    public void CommonSetup(FMLCommonSetupEvent event)
    {
        //here, I am registering new crafting conditions
        //first I make a new EnabledCondition, and then I make a Serializer that is "inside" that object
        //you can get the enclosing object (EnabledCondition) via "EnabledCondition.this", at least locally
        //hopefully we can replace this nonsense with feature flags in 1.20
        CraftingHelper.register(new EnabledCondition("maces_enabled", Config.maceEnabled).new Serializer());
        CraftingHelper.register(new EnabledCondition("hammers_enabled", Config.hammerEnabled).new Serializer());
        CraftingHelper.register(new EnabledCondition("flails_enabled", Config.flailEnabled).new Serializer());
        CraftingHelper.register(new EnabledCondition("spears_enabled", Config.spearEnabled).new Serializer());
        CraftingHelper.register(new EnabledCondition("daggers_enabled", Config.daggerEnabled).new Serializer());
        CraftingHelper.register(new EnabledCondition("quivers_enabled", Config.quiverEnabled).new Serializer());
        CraftingHelper.register(new EnabledCondition("scabbards_enabled", Config.scabbardEnabled).new Serializer());
        CraftingHelper.register(new EnabledCondition("bandoliers_enabled", Config.bandolierEnabled).new Serializer());
        CraftingHelper.register(new EnabledCondition("is_chainmail_craftable", Config.isChainmailCraftable).new Serializer());

        BrewingRecipeRegistry.addRecipe(new CustomBrewingRecipe(Potions.AWKWARD, Items.CHORUS_FRUIT, ModPotions.COALESCENCE.get()));
        BrewingRecipeRegistry.addRecipe(new CustomBrewingRecipe(ModPotions.COALESCENCE.get(), Items.REDSTONE, ModPotions.COALESCENCE_LONG.get()));
        BrewingRecipeRegistry.addRecipe(new CustomBrewingRecipe(ModPotions.IMMORTALITY.get(), Items.REDSTONE, ModPotions.IMMORTALITY_LONG.get()));
        BrewingRecipeRegistry.addRecipe(new CustomBrewingRecipe(Potions.AWKWARD, Items.GOLDEN_APPLE, ModPotions.VITALITY.get()));
        BrewingRecipeRegistry.addRecipe(new CustomBrewingRecipe(ModPotions.VITALITY.get(), Items.REDSTONE, ModPotions.VITALITY_LONG.get()));
        BrewingRecipeRegistry.addRecipe(new CustomBrewingRecipe(Potions.AWKWARD, Items.WITHER_ROSE, ModPotions.DECAY.get()));
        BrewingRecipeRegistry.addRecipe(new CustomBrewingRecipe(ModPotions.DECAY.get(), Items.REDSTONE, ModPotions.DECAY_LONG.get()));BrewingRecipeRegistry.addRecipe(new CustomBrewingRecipe(Potions.AWKWARD, Items.WITHER_ROSE, ModPotions.DECAY.get()));
        BrewingRecipeRegistry.addRecipe(new CustomBrewingRecipe(ModPotions.INSTABILITY.get(), Items.REDSTONE, ModPotions.INSTABILITY_LONG.get()));
        BrewingRecipeRegistry.addRecipe(new CustomBrewingRecipe(ModPotions.STABILITY.get(), Items.REDSTONE, ModPotions.STABILITY_LONG.get()));

        PotionBrewing.addContainerRecipe(Items.POTION, Items.HONEYCOMB, ModItems.OIL.get());//todo may want to move all potion stuff here
    }
}