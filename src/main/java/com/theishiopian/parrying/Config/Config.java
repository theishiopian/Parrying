package com.theishiopian.parrying.Config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config
{
    public static final ForgeConfigSpec COMMON;

    //parrying
    public static final ForgeConfigSpec.BooleanValue parryEnabled;
    public static final ForgeConfigSpec.DoubleValue parryAngle;

    //bashing
    public static final ForgeConfigSpec.BooleanValue bashEnabled;
    public static final ForgeConfigSpec.DoubleValue bashAngle;
    public static final ForgeConfigSpec.IntValue bashBaseCooldown;
    public static final ForgeConfigSpec.IntValue bashMissCooldown;
    public static final ForgeConfigSpec.IntValue bashTargets;

    //backstab
    public static final ForgeConfigSpec.BooleanValue backStabEnabled;
    public static final ForgeConfigSpec.DoubleValue backStabAngle;
    public static final ForgeConfigSpec.DoubleValue backStabDamageMultiplier;
    public static final ForgeConfigSpec.IntValue backStabMaxHealth;

    //dodge
    public static final ForgeConfigSpec.BooleanValue dodgeEnabled;
    public static final ForgeConfigSpec.DoubleValue dodgePower;
    public static final ForgeConfigSpec.IntValue dodgeTriggerDelay;
    public static final ForgeConfigSpec.DoubleValue dodgeCooldown;

    //enchants
    public static final ForgeConfigSpec.BooleanValue deflectionEnchantEnabled;
    public static final ForgeConfigSpec.BooleanValue riposteEnchantEnabled;
    public static final ForgeConfigSpec.BooleanValue fragileCurseEnabled;
    public static final ForgeConfigSpec.BooleanValue phasingCurseEnabled;
    public static final ForgeConfigSpec.BooleanValue isFragileTreasure;
    public static final ForgeConfigSpec.BooleanValue isPhasingTreasure;
    public static final ForgeConfigSpec.BooleanValue bashingEnchantEnabled;

    static
    {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("common");

        //PARRY
        parryEnabled = builder.comment("Whether parrying is enabled or not.").define("parry_enabled", true);

        parryAngle = builder.comment("The maximum angle that you can be aiming relative to the attacker. " +
                "-1 represents pointing exactly away from the attacker, and 1 represents pointing exactly towards the attacker")
                .defineInRange("parry_angle", 0.95, -1,1);

        //BASH

        bashEnabled = builder.comment("Whether shield bashing is enabled or not.").define("bash_enabled", true);

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

        //BACKSTAB

        backStabEnabled = builder.comment("Whether backstabbing is enabled or not").define("backstab_enabled", true);

        backStabAngle = builder.comment("The maximum angle that you can be aiming relative to the target. " +
                "-1 represents pointing exactly away from the target, and 1 represents pointing exactly towards the target")
                .defineInRange("backstab_angle", 0.85, -1,1);

        backStabDamageMultiplier = builder.comment("The amount to multiply incoming damage by with a successful backstab.")
                .defineInRange("backstab_multiplier", 3, 1d, 99999d);

        backStabMaxHealth = builder.comment("Any entity with health greater than this is immune to backstab")
                .defineInRange("backstab_max_health", 20, 1, 99999);

        //DODGE
        dodgeEnabled = builder.comment("Whether dodging is enabled or not.").define("dodge_enabled", true);

        dodgePower = builder.comment("How much power does the dodge have?").defineInRange("dodge_power", 0.5, 0, 99999);

        dodgeTriggerDelay = builder.comment("How many game ticks can you wait before pressing the button a second time")
                .defineInRange("dodge_trigger_delay", 9, 1, 10);

        dodgeCooldown = builder.comment("The time in seconds before you can dodge again").defineInRange("dodge_cooldown", 2, 0, 99999d);

        //ENCHANT
        deflectionEnchantEnabled = builder.comment("Whether or not the deflection enchantment is enabled").define("deflection_enabled", true);
        riposteEnchantEnabled = builder.comment("Whether or not the riposte enchantment is enabled").define("riposte_enabled", true);
        bashingEnchantEnabled = builder.comment("Whether or not the bashing enchantment is enabled").define("bashing_enabled", true);
        fragileCurseEnabled = builder.comment("Whether or not the fragile curse is enabled").define("fragile_enabled", true);
        phasingCurseEnabled = builder.comment("Whether or not the phasing curse is enabled").define("phasing_enabled", true);
        isFragileTreasure = builder.comment("Whether or not the fragile curse is treasure only").define("is_fragile_treasure", true);
        isPhasingTreasure = builder.comment("Whether or not the phasing curse is treasure only").define("is_phasing_treasure", true);

        COMMON = builder.build();
    }
}