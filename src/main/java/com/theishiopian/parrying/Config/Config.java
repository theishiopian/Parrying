package com.theishiopian.parrying.Config;

import net.minecraftforge.common.ForgeConfigSpec;

@SuppressWarnings("ALL")
/**
 * This class exists to generate a config file for the mod. The config specs are used both to define and get their respective values.
 */
public class Config
{
    public static final ForgeConfigSpec COMMON;

    //weapon
    public static final ForgeConfigSpec.BooleanValue maceEnabled;
    public static final ForgeConfigSpec.BooleanValue hammerEnabled;
    public static final ForgeConfigSpec.BooleanValue flailEnabled;
    public static final ForgeConfigSpec.BooleanValue spearEnabled;
    public static final ForgeConfigSpec.BooleanValue daggerEnabled;
    //public static final ForgeConfigSpec.BooleanValue quiverEnabled;

    //arrow tweaks
    public static final ForgeConfigSpec.BooleanValue flamingArrowGriefing;
    public static final ForgeConfigSpec.BooleanValue apPiercing;
    public static final ForgeConfigSpec.BooleanValue sonicSpectralArrow;
    public static final ForgeConfigSpec.BooleanValue pickyPotionArrows;

    //parrying
    public static final ForgeConfigSpec.BooleanValue parryEnabled;
    public static final ForgeConfigSpec.DoubleValue parryAngle;
    public static final ForgeConfigSpec.DoubleValue parryPenalty;

    //dual wielding
    public static final ForgeConfigSpec.BooleanValue dualWieldEnabled;
    public static final ForgeConfigSpec.BooleanValue twoHandedEnabled;

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
    public static final ForgeConfigSpec.DoubleValue dodgeCooldown;

    //enchants
    public static final ForgeConfigSpec.BooleanValue deflectionEnchantEnabled;
    public static final ForgeConfigSpec.BooleanValue riposteEnchantEnabled;
    public static final ForgeConfigSpec.BooleanValue cripplingEnchantEnabled;
    public static final ForgeConfigSpec.BooleanValue fragileCurseEnabled;
    public static final ForgeConfigSpec.BooleanValue phasingCurseEnabled;
    public static final ForgeConfigSpec.BooleanValue isFragileTreasure;
    public static final ForgeConfigSpec.BooleanValue isPhasingTreasure;
    public static final ForgeConfigSpec.BooleanValue bashingEnchantEnabled;
    public static final ForgeConfigSpec.BooleanValue treacheryEnabled;
    public static final ForgeConfigSpec.BooleanValue venomousEnabled;
    public static final ForgeConfigSpec.BooleanValue joustingEnabled;

    //chainmail crafting
    public static final ForgeConfigSpec.BooleanValue isChainmailCraftable;

    //zero g arrows for scoped crossbows
    public static final ForgeConfigSpec.BooleanValue zeroGravityBolts;

    //owners can't attack pets
    public static final ForgeConfigSpec.BooleanValue protectPets;

    static
    {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("common");

        //WEAPONS
        maceEnabled = builder.comment("Whether or not the mace is craftable.").define("mace_enabled", true);
        hammerEnabled = builder.comment("Whether or not the hammer is craftable.").define("hammer_enabled", true);
        flailEnabled = builder.comment("Whether or not the flail is craftable.").define("flail_enabled", true);
        spearEnabled = builder.comment("Whether or not the spear is craftable.").define("spear_enabled", true);
        daggerEnabled = builder.comment("Whether or not the dagger is craftable.").define("dagger_enabled", true);
        //quiverEnabled = builder.comment("Whether or not the quiver is craftable.").define("quiver_enabled", true);

        flamingArrowGriefing = builder.comment("Can flaming arrows ignite blocks?").define("flaming_arrow_griefing", true);
        apPiercing = builder.comment("Does the piercing enchant cause armor penetrating damage?").define("ap_piercing", true);
        sonicSpectralArrow = builder.comment("Can spectral arrows reveal mobs around their impact point?").define("sonic_spectral_arrows", true);
        pickyPotionArrows = builder.comment("When enabled, potion arrows with no negative effects deal 0 damage").define("picky_potion_arrows", true);

        //PARRY
        parryEnabled = builder.comment("Whether parrying is enabled or not.").define("parry_enabled", true);

        parryAngle = builder.comment("The maximum angle that you can be aiming relative to the attacker. " +
                "-1 represents pointing exactly away from the attacker, and 1 represents pointing exactly towards the attacker"+
                        "0.5 represents 45 degrees")
                .defineInRange("parry_angle", 0.5f, -1,1);

        parryPenalty = builder.comment("The penalty to parry angle for parrying at low attack recharge.")
                .defineInRange("parry_penalty", 0.15, 0,1);

        //DUAL WIELD
        dualWieldEnabled = builder.comment("Whether dual wielding is enabled or not.").define("dual_wielding_enabled", true);
        twoHandedEnabled = builder.comment("Whether certain weapons disable dual wielding").define("two_handed_enabled", true);

        //BASH
        bashEnabled = builder.comment("Whether shield bashing is enabled or not.").define("bash_enabled", true);

        bashAngle = builder.comment("The maximum angle that you can be aiming relative to the target. " +
                "-1 represents pointing exactly away from the attacker, and 1 represents pointing exactly towards the attacker")
                .defineInRange("bash_angle", 0.75, -1,1);

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

        dodgeCooldown = builder.comment("The time in seconds before you can dodge again").defineInRange("dodge_cooldown", 1.5, 0, 99999d);

        //ENCHANT
        deflectionEnchantEnabled = builder.comment("Whether or not the deflection enchantment is enabled").define("deflection_enabled", true);
        riposteEnchantEnabled = builder.comment("Whether or not the riposte enchantment is enabled").define("riposte_enabled", true);

        cripplingEnchantEnabled = builder.comment("Whether or not the crippling enchantment is enabled").define("crippling_enabled", true);
        bashingEnchantEnabled = builder.comment("Whether or not the bashing enchantment is enabled").define("bashing_enabled", true);
        treacheryEnabled = builder.comment("Whether or not the treachery enchantment is enabled").define("treachery_enabled", true);
        joustingEnabled = builder.comment("Whether or not the jousting enchantment is enabled").define("jousting_enabled", true);
        venomousEnabled = builder.comment("Whether or not the venomous enchantment is enabled").define("venomous_enabled", true);
        fragileCurseEnabled = builder.comment("Whether or not the fragile curse is enabled").define("fragile_enabled", true);
        phasingCurseEnabled = builder.comment("Whether or not the phasing curse is enabled").define("phasing_enabled", true);
        isFragileTreasure = builder.comment("Whether or not the fragile curse is treasure only").define("is_fragile_treasure", true);
        isPhasingTreasure = builder.comment("Whether or not the phasing curse is treasure only").define("is_phasing_treasure", true);

        isChainmailCraftable = builder.comment("Easy way to disable chainmail crafting without a datapack").define("is_chainmail_craftable", true);

        zeroGravityBolts = builder.comment("Does the scoped crossbow fire arrows with no gravity?").define("zero_gravity_bolts", true);
        protectPets = builder.comment("Are pets immune to damage from their owners?").define("protect_pets", true);

        COMMON = builder.build();
    }
}