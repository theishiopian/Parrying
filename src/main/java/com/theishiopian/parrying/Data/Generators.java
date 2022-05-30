package com.theishiopian.parrying.Data;

import com.theishiopian.parrying.ParryingMod;
import com.theishiopian.parrying.Registration.ModItemTagProvider;
import com.theishiopian.parrying.Registration.Utility.Debug;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

/**
 * This class is used to activate data generators. It has no use in build
 */
@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD )
public class Generators
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        Debug.log("EVENT-------------------------------");
        if(event.includeServer())
        {
            Debug.log("SERVER");
            DataGenerator generator = event.getGenerator();
            ExistingFileHelper helper = event.getExistingFileHelper();
            generator.addProvider(new ModItemTagProvider(generator, new BlockTagsProvider(generator, ParryingMod.MOD_ID, helper), event.getExistingFileHelper()));
        }
    }
}
