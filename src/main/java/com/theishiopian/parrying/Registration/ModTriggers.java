package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.Advancement.SimpleTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public class ModTriggers
{
    public static SimpleTrigger campfire;
    public static SimpleTrigger snipe;
    public static SimpleTrigger deflect;

    public static void Init()
    {
        campfire =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:lit")));
        snipe =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:snipe")));
        deflect =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:deflect")));
    }
}
