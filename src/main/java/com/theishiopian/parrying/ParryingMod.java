package com.theishiopian.parrying;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Handler.ClientEvents;
import com.theishiopian.parrying.Handler.CommonEvents;
import com.theishiopian.parrying.Network.DodgePacket;
import com.theishiopian.parrying.Network.LeftClickPacket;
import com.theishiopian.parrying.Registration.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

@Mod(ParryingMod.MOD_ID)
public class ParryingMod
{
    public static final String MOD_ID = "parrying";
    public static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation netName = new ResourceLocation(MOD_ID, "network");
    public static SimpleChannel channel;
    private static final int VERSION = 2;//protocol version

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
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::ArrowParryEvent);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnHurtEvent);
        MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnWorldTick);
        //MinecraftForge.EVENT_BUS.addListener(CommonEvents::OnTick);

        ModParticles.PARTICLE_TYPES.register(bus);
        ModSoundEvents.SOUND_EVENTS.register(bus);
        ModEnchantments.ENCHANTMENTS.register(bus);
        ModEffects.EFFECTS.register(bus);
        ModItems.ITEMS.register(bus);
        ModAttributes.ATTRIBUTES.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        {
            bus.addListener(ClientEvents::OnRegisterParticlesEvent);
            MinecraftForge.EVENT_BUS.addListener(ClientEvents::OnClick);
            MinecraftForge.EVENT_BUS.addListener(ClientEvents::OnKeyPressed);
        });
    }
}