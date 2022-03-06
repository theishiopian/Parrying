package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.Criteria.BasicTrigger;
import net.minecraft.advancements.CriteriaTriggers;

/**
 * This class registers custom triggers for advancements.
 */
public class ModTriggers
{
    public  static BasicTrigger campfireLight;
    public  static BasicTrigger snipe;

    public static void Init()
    {
        campfireLight = CriteriaTriggers.register(new BasicTrigger("campfire_light"));
        snipe = CriteriaTriggers.register(new BasicTrigger("snipe"));
    }
}
