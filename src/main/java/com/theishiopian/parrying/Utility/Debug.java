package com.theishiopian.parrying.Utility;

import com.theishiopian.parrying.ParryingMod;

public class Debug
{
    public static void log(String message, Object... params)
    {
        ParryingMod.LOGGER.info(message, params);
    }

    public static void log(String message)
    {
        ParryingMod.LOGGER.info(message);
    }

    public static void log(Object message)
    {
        ParryingMod.LOGGER.info(String.valueOf(message));
    }
}
