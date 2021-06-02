package com.theishiopian.parrying.Config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config
{
    public static final ForgeConfigSpec COMMON;

    public static final ForgeConfigSpec.DoubleValue parryAngle;
    public static final ForgeConfigSpec.DoubleValue bashAngle;

    public static final ForgeConfigSpec.IntValue bashBaseCooldown;
    public static final ForgeConfigSpec.IntValue bashMissCooldown;
    public static final ForgeConfigSpec.IntValue bashTargets;

    public static final ForgeConfigSpec.BooleanValue deflectionEnabled;
    public static final ForgeConfigSpec.BooleanValue riposteEnabled;
    public static final ForgeConfigSpec.BooleanValue bashingEnabled;
    public static final ForgeConfigSpec.BooleanValue fragileEnabled;
    public static final ForgeConfigSpec.BooleanValue phasingEnabled;
    public static final ForgeConfigSpec.BooleanValue isFragileTreasure;
    public static final ForgeConfigSpec.BooleanValue isPhasingTreasure;

    static
    {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("common");

        parryAngle = builder.comment("The maximum angle that you can be aiming relative to the attacker. " +
                "-1 represents pointing exactly away from the attacker, and 1 represents pointing exactly towards the attacker")
                .defineInRange("parry_angle", 0.95, -1,1);

        bashAngle = builder.comment("The maximum angle that you can be aiming relative to the target. " +
                "-1 represents pointing exactly away from the attacker, and 1 represents pointing exactly towards the attacker")
                .defineInRange("bash_angle", 0.85, -1,1);

        bashBaseCooldown = builder.comment("The base cooldown, in ticks, for a successful shield bash. Each " +
                "target adds one second to the cooldown. There are 20 ticks in a second.")
                .defineInRange("bash_base_cooldown", 80, 0, Integer.MAX_VALUE);

        bashMissCooldown = builder.comment("The cooldown, in ticks, for missing a shield bash. There are 20 ticks in a second.")
                .defineInRange("bash_miss_cooldown", 20, 0, Integer.MAX_VALUE);

        bashTargets = builder.comment("The number of targets a shield bash can hit. Each level of Bashing adds 1 to this number")
                .defineInRange("bash_targets", 3, 1, Integer.MAX_VALUE);

        deflectionEnabled = builder.comment("Whether or not the deflection enchantment is enabled").define("deflection_enabled", true);
        riposteEnabled = builder.comment("Whether or not the riposte enchantment is enabled").define("riposte_enabled", true);
        bashingEnabled = builder.comment("Whether or not the bashing enchantment is enabled").define("bashing_enabled", true);
        fragileEnabled = builder.comment("Whether or not the fragile curse is enabled").define("fragile_enabled", true);
        phasingEnabled = builder.comment("Whether or not the phasing curse is enabled").define("phasing_enabled", true);
        isFragileTreasure = builder.comment("Whether or not the fragile curse is treasure only").define("is_fragile_treasure", true);
        isPhasingTreasure = builder.comment("Whether or not the phasing curse is treasure only").define("is_phasing_treasure", true);

        COMMON = builder.build();
    }
}