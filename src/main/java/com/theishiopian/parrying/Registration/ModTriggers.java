package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.Criteria.BasicTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public class ModTriggers
{
    public  static BasicTrigger campfireLight;

    public static void Init()
    {
        campfireLight = CriteriaTriggers.register(new BasicTrigger("campfire_light"));
    }
}
