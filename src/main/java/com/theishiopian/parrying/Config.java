package com.theishiopian.parrying;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config
{
    public static final ForgeConfigSpec COMMON;

    public static final ForgeConfigSpec.DoubleValue parryAngle;
    public static final ForgeConfigSpec.DoubleValue bashAngle;

    public static final ForgeConfigSpec.IntValue bashBaseCooldown;
    public static final ForgeConfigSpec.IntValue bashMissCooldown;
    public static final ForgeConfigSpec.IntValue bashTargets;

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

        COMMON = builder.build();
    }
}
