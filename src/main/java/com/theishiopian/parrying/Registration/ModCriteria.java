package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.Criteria.CampfireLightTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public class ModCriteria
{
    public static CampfireLightTrigger campfireLight;

    public static void Init()
    {
        campfireLight = CriteriaTriggers.register(new CampfireLightTrigger());
    }
}
