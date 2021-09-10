package com.theishiopian.parrying;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Entity.Render.RenderSpear;
import com.theishiopian.parrying.Handler.ClientEvents;
import com.theishiopian.parrying.Handler.CommonEvents;
import com.theishiopian.parrying.Network.DodgePacket;
import com.theishiopian.parrying.Network.LeftClickPacket;
import com.theishiopian.parrying.Recipes.EnabledCondition;
import com.theishiopian.parrying.Registration.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Mod(ParryingMod.MOD_ID)
@SuppressWarnings("deprecation")
public class ParryingMod
{
    public static final String MOD_ID = "parrying";
    public static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation netName = new ResourceLocation(MOD_ID, "network");
    public static final SimpleChannel channel;
    private static final int VERSION = 2;//protocol version, bump whenever adding new network packets

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
    }

    public ParryingMod()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnAttackedEvent);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnArrowImpact);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnHurtEvent);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnWorldTick);

        ModTriggers.Init();
        ModParticles.PARTICLE_TYPES.register(bus);
        ModSoundEvents.SOUND_EVENTS.register(bus);
        ModEnchantments.ENCHANTMENTS.register(bus);
        ModEffects.EFFECTS.register(bus);
        ModItems.ITEMS.register(bus);
        ModEntities.ENTITY_TYPES.register(bus);
        ModAttributes.ATTRIBUTES.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        {
            bus.addListener(ClientEvents::OnRegisterParticlesEvent);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::ClientSetup);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::OnModelBake);
        });

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::CommonSetup);
    }

    @OnlyIn(Dist.CLIENT)
    public void ClientSetup(FMLClientSetupEvent event)
    {
        if(Config.flailEnabled.get())ModItems.RegisterFlailOverrides();

        MinecraftForge.EVENT_BUS.addListener(ClientEvents::OnClick);
        MinecraftForge.EVENT_BUS.addListener(ClientEvents::OnKeyPressed);

        RenderingRegistry.registerEntityRenderingHandler(ModEntities.SPEAR.get(), RenderSpear::new);

        ModelLoader.addSpecialModel(new ModelResourceLocation(ModItems.IRON_SPEAR.get().getRegistryName() + "_handheld", "inventory"));
    }

    @OnlyIn(Dist.CLIENT)
    public void OnModelBake(ModelBakeEvent event)
    {
        //TODO foreach
        Map<ResourceLocation, IBakedModel> map = event.getModelRegistry();

        ResourceLocation spear = ModItems.IRON_SPEAR.get().getRegistryName();
        assert spear != null;
        ResourceLocation spearInventory = new ModelResourceLocation(spear, "inventory");
        ResourceLocation spearHand = new ModelResourceLocation(spear + "_handheld", "inventory");

        IBakedModel spearModelDefault = map.get(spearInventory);
        IBakedModel spearModelHand = map.get(spearHand);

        IBakedModel spearModelWrapper = new IBakedModel()
        {
            @Override
            public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, @NotNull Random random)
            {
                return spearModelDefault.getQuads(state, direction, random);
            }

            @Override
            public boolean useAmbientOcclusion()
            {
                return spearModelDefault.useAmbientOcclusion();
            }

            @Override
            public boolean isGui3d()
            {
                return spearModelDefault.isGui3d();
            }

            @Override
            public boolean usesBlockLight()
            {
                return spearModelDefault.usesBlockLight();
            }

            @Override
            public boolean isCustomRenderer()
            {
                return spearModelDefault.isCustomRenderer();
            }

            @Override
            public @NotNull TextureAtlasSprite getParticleIcon()
            {
                return spearModelDefault.getParticleIcon();
            }

            @Override
            public @NotNull ItemOverrideList getOverrides()
            {
                return spearModelDefault.getOverrides();
            }

            @Override
            public IBakedModel handlePerspective(ItemCameraTransforms.TransformType transformType, MatrixStack mat)
            {
                IBakedModel modelToUse = spearModelDefault;
                if (transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND ||
                    transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND ||
                    transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND ||
                    transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
                {
                    modelToUse = spearModelHand;
                }
                return ForgeHooksClient.handlePerspective(modelToUse, transformType, mat);
            }
        };

        map.put(spearInventory, spearModelWrapper);
    }

    public void CommonSetup(FMLCommonSetupEvent event)
    {
        //here, I am registering new crafting conditions
        //first I make a new EnabledCondition, and then I make a Serializer that is "inside" that object
        //you can get the enclosing object (EnabledCondition) via "EnabledCondition.this", at least locally
        //todo spears
        CraftingHelper.register(new EnabledCondition("maces_enabled", Config.maceEnabled::get).new Serializer());
        CraftingHelper.register(new EnabledCondition("hammers_enabled", Config.hammerEnabled::get).new Serializer());
        CraftingHelper.register(new EnabledCondition("flails_enabled", Config.flailEnabled::get).new Serializer());
    }
}