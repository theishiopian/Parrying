package com.theishiopian.parrying;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("parrying")
public class ParryingMod
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public ParryingMod()
    {
        MinecraftForge.EVENT_BUS.addListener(EventHandler::OnAttackedEvent);
    }
}
