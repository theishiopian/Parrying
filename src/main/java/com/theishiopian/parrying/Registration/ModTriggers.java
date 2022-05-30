package com.theishiopian.parrying.Registration;

import com.theishiopian.parrying.Advancements.SimpleTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public class ModTriggers
{
    public static SimpleTrigger campfire;
    public static SimpleTrigger snipe;
    public static SimpleTrigger deflect;
    public static SimpleTrigger deflectTrident;
    public static SimpleTrigger poke;
    public static SimpleTrigger stagger;
    public static SimpleTrigger bigBash;
    public static SimpleTrigger retribution;
    public static SimpleTrigger bacon;
    public static SimpleTrigger instakill;
    public static SimpleTrigger rally;
    public static SimpleTrigger hussars;
    public static SimpleTrigger vibe;
    public static SimpleTrigger quiver_over_stack;
    public static SimpleTrigger kevin;

    public static void Init()
    {
        campfire =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:lit")));
        snipe =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:snipe")));
        deflect =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:deflect")));
        deflectTrident =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:deflect_trident")));
        poke =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:poke")));
        stagger =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:stagger")));
        bigBash =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:big_bash")));
        retribution =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:retribution")));
        bacon =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:bacon")));
        instakill =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:instakill")));
        rally =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:rally")));
        hussars =  CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:hussars")));
        vibe = CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:vibe")));
        kevin = CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:kevin")));
        quiver_over_stack = CriteriaTriggers.register(new SimpleTrigger(new ResourceLocation("parrying:quiver_over_stack")));
    }
}
