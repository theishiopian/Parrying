package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.ParryingMod;
import net.minecraft.stats.IStatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.MOD, modid = ParryingMod.MOD_ID)
public class ModStats
{
    public static ResourceLocation parry;

    @SubscribeEvent
    public static void registerStats(RegistryEvent.Register<StatType<?>> event)
    {
        parry = new ResourceLocation(ParryingMod.MOD_ID, "parry");
        Registry.register(Registry.CUSTOM_STAT, parry, parry);
        Stats.CUSTOM.get(parry, IStatFormatter.DEFAULT);
    }
}