package com.theishiopian.parrying.Registration;

import net.minecraft.world.damagesource.DamageSource;

//used for static damage sources
public class ModDamageSources
{
    public static final DamageSource BEDROCK = new DamageSource("bedrock");
    public static final DamageSource CLEANSING = new DamageSource("cleansing").bypassArmor().bypassMagic();
}
