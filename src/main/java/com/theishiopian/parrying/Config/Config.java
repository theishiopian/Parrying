package com.theishiopian.parrying.Config;

import net.minecraftforge.common.ForgeConfigSpec;

@SuppressWarnings("ALL")
/**
 * This class exists to generate a config file for the mod. The config specs are used both to define and get their respective values.
 */
public class Config
{
    public static final ForgeConfigSpec COMMON;

    //brewing
    public static final ForgeConfigSpec.BooleanValue brewingRequiresFuel;
    public static final ForgeConfigSpec.BooleanValue brewingRecipeOverhaul;
    public static final ForgeConfigSpec.BooleanValue poisonLethal;
    public static final ForgeConfigSpec.BooleanValue witherRework;
    public static final ForgeConfigSpec.BooleanValue modifyThrow;
    public static final ForgeConfigSpec.BooleanValue noSelfSplash;
    public static final ForgeConfigSpec.BooleanValue shieldSplash;
    public static final ForgeConfigSpec.BooleanValue potionSickness;
    public static final ForgeConfigSpec.BooleanValue potionSicknessNausea;
    public static final ForgeConfigSpec.IntValue potionTolerance;
    public static final ForgeConfigSpec.IntValue sipTicks;
    public static final ForgeConfigSpec.IntValue brewingTicks;
    public static final ForgeConfigSpec.DoubleValue lingeringRadius;

    //food
    public static final ForgeConfigSpec.BooleanValue noSatHeal;
    public static final ForgeConfigSpec.BooleanValue milkBucketRework;

    //undying rework
    public static final ForgeConfigSpec.BooleanValue undyingWorksFromInventory;
    public static final ForgeConfigSpec.BooleanValue undyingRework;

    //weapon
    public static final ForgeConfigSpec.BooleanValue maceEnabled;
    public static final ForgeConfigSpec.BooleanValue hammerEnabled;
    public static final ForgeConfigSpec.BooleanValue flailEnabled;
    public static final ForgeConfigSpec.BooleanValue spearEnabled;
    public static final ForgeConfigSpec.BooleanValue daggerEnabled;
    public static final ForgeConfigSpec.BooleanValue quiverEnabled;
    public static final ForgeConfigSpec.BooleanValue bandolierEnabled;
    public static final ForgeConfigSpec.BooleanValue scabbardEnabled;

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
    public static final ForgeConfigSpec.BooleanValue fragileCurseEnabled;
    public static final ForgeConfigSpec.BooleanValue phasingCurseEnabled;
    public static final ForgeConfigSpec.BooleanValue isFragileTreasure;
    public static final ForgeConfigSpec.BooleanValue isPhasingTreasure;
    public static final ForgeConfigSpec.BooleanValue isIntrusionTreasure;
    public static final ForgeConfigSpec.BooleanValue isProvidenceTreasure;
    public static final ForgeConfigSpec.BooleanValue isSwiftStrikeTreasure;
    public static final ForgeConfigSpec.BooleanValue bashingEnchantEnabled;
    public static final ForgeConfigSpec.BooleanValue treacheryEnabled;
    public static final ForgeConfigSpec.BooleanValue venomousEnabled;
    public static final ForgeConfigSpec.BooleanValue joustingEnabled;
    public static final ForgeConfigSpec.BooleanValue providenceEnabled;
    public static final ForgeConfigSpec.BooleanValue contextEnabled;
    public static final ForgeConfigSpec.BooleanValue rapidityEnabled;
    public static final ForgeConfigSpec.BooleanValue swiftStrikeEnabled;
    public static final ForgeConfigSpec.BooleanValue intrusiveCurseEnabled;

    //chainmail crafting
    public static final ForgeConfigSpec.BooleanValue isChainmailCraftable;

    public static  final ForgeConfigSpec.BooleanValue isGoldBuffed;

    //zero g arrows for scoped crossbows
    public static final ForgeConfigSpec.BooleanValue zeroGravityBolts;

    //owners can't attack pets
    public static final ForgeConfigSpec.BooleanValue protectPets;

    public static final ForgeConfigSpec.DoubleValue swiftStrikeAngle;

    public static final ForgeConfigSpec.DoubleValue drawCooldown;

    static
    {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("common");

        brewingRequiresFuel = builder.comment("Whether or not the brewing stand will require and accept fuel. Also controls whether the fuel slot is visible").define("brewing_requires_fuel", false);
        brewingRecipeOverhaul = builder.comment("Whether or not the mod replaces the vanilla brewing recipes").define("brewing_overhaul", true);
        sipTicks = builder.comment("How many ticks a potion takes to drink. Vanilla is 32, mod default is 16").defineInRange("potion_drink_ticks", 16, 1, Integer.MAX_VALUE);
        brewingTicks = builder.comment("How many ticks the brewing stand takes to brew a potion, vanilla is 400, mod default is 120").defineInRange("brewing_ticks", 120, 1, Integer.MAX_VALUE);
        poisonLethal = builder.comment("Whether or not poison is lethal").define("poison_lethal", true);
        witherRework = builder.comment("Whether or not the wither effect is reworked").define("wither_rework", true);
        modifyThrow = builder.comment("Whether or not to modify the throwing force of thrown potions").define("modify_potion_throwing", true);
        noSelfSplash = builder.comment("Whether or not to disable splash potions affecting the thrower").define("no_self_splash", true);
        shieldSplash = builder.comment("Whether or not a shield can block splash potions").define("shield_splash", true);
        potionSickness = builder.comment("Whether or not to dlimit the number of effects on a player at once").define("potion_sickness", true);
        potionSicknessNausea = builder.comment("Whether or not to give nausea to a player who overdoses on potions. Disable in case of motion sickness.").define("potion_sickness_nausea", true);
        potionTolerance = builder.comment("How many effects the player can have before overdosing.").defineInRange("potion_tolerance", 4, 1, 1000);
        undyingRework = builder.comment("Whether or not the totem of undying is reworked.").define("totem_rework", true);
        undyingWorksFromInventory = builder.comment("Whether or not the totem of undying works from the inventory. Also disabled if the rework is disabled.").define("totem_inventory", true);
        noSatHeal = builder.comment("Whether or not saturation healing is disabled").define("no_saturation_healing", true);
        milkBucketRework = builder.comment("Whether or not the milk bucket is reworked").define("milk_bucket_rework", true);
        lingeringRadius = builder.comment("The radius of lingering potion clouds. Vanilla 3, default 4").defineInRange("lingering_radius", 4f, 0, 100);

        //WEAPONS
        maceEnabled = builder.comment("Whether or not the mace is craftable.").define("mace_enabled", true);
        hammerEnabled = builder.comment("Whether or not the hammer is craftable.").define("hammer_enabled", true);
        flailEnabled = builder.comment("Whether or not the flail is craftable.").define("flail_enabled", true);
        spearEnabled = builder.comment("Whether or not the spear is craftable.").define("spear_enabled", true);
        daggerEnabled = builder.comment("Whether or not the dagger is craftable.").define("dagger_enabled", true);
        quiverEnabled = builder.comment("Whether or not the quiver is craftable.").define("quiver_enabled", true);
        bandolierEnabled = builder.comment("Whether or not the bandolier is craftable.").define("quiver_enabled", true);
        scabbardEnabled = builder.comment("Whether or not the scabbard is craftable.").define("scabbard_enabled", true);

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
                .defineInRange("bash_angle", 0.5, -1,1);

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
                .defineInRange("backstab_angle", 0.8, -1,1);

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

        swiftStrikeAngle = builder.comment("The maximum angle that you can be aiming relative to the target. " +
                        "-1 represents pointing exactly away from the attacker, and 1 represents pointing exactly towards the attacker")
                .defineInRange("bash_angle", 0.85, -1,1);

        drawCooldown = builder.comment("The time in seconds before you can draw or sheathe again").defineInRange("draw_cooldown", 1, 0, 99999d);

        bashingEnchantEnabled = builder.comment("Whether or not the bashing enchantment is enabled").define("bashing_enabled", true);
        treacheryEnabled = builder.comment("Whether or not the treachery enchantment is enabled").define("treachery_enabled", true);
        joustingEnabled = builder.comment("Whether or not the jousting enchantment is enabled").define("jousting_enabled", true);
        venomousEnabled = builder.comment("Whether or not the venomous enchantment is enabled").define("venomous_enabled", true);
        providenceEnabled = builder.comment("Whether or not the providence enchantment is enabled").define("providence_enabled", true);
        swiftStrikeEnabled = builder.comment("Whether or not the swift strike enchantment is enabled").define("swift_strike_enabled", true);
        contextEnabled = builder.comment("Whether or not the context enchantment is enabled").define("context_enabled", true);
        rapidityEnabled = builder.comment("Whether or not the rapidity enchantment is enabled").define("rapidity_enabled", true);
        fragileCurseEnabled = builder.comment("Whether or not the fragile curse is enabled").define("fragile_enabled", true);
        phasingCurseEnabled = builder.comment("Whether or not the phasing curse is enabled").define("phasing_enabled", true);
        intrusiveCurseEnabled = builder.comment("Whether or not the intrusion curse is enabled").define("intrusion_enabled", true);
        isFragileTreasure = builder.comment("Whether or not the fragile curse is treasure only").define("is_fragile_treasure", true);
        isPhasingTreasure = builder.comment("Whether or not the phasing curse is treasure only").define("is_phasing_treasure", true);
        isIntrusionTreasure = builder.comment("Whether or not intrusion curse is treasure only").define("is_intrusion_treasure", true);
        isProvidenceTreasure = builder.comment("Whether or not the providence enchantment is treasure only").define("is_providence_treasure", true);
        isSwiftStrikeTreasure = builder.comment("Whether or not the swift strike enchantment is treasure only").define("is_swift_strike_treasure", true);

        isChainmailCraftable = builder.comment("Easy way to disable chainmail crafting without a datapack").define("is_chainmail_craftable", true);
        isGoldBuffed = builder.comment("Is gold equipment buffed?").define("is_gold_buffed", true);

        zeroGravityBolts = builder.comment("Does the scoped crossbow fire arrows with no gravity?").define("zero_gravity_bolts", true);
        protectPets = builder.comment("Are pets immune to damage from their owners?").define("protect_pets", true);

        COMMON = builder.build();
    }
}