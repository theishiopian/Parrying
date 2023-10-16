package com.theishiopian.parrying.CoreMod.Mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Registration.ModEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FoodData.class)
public class FoodDataMixin
{
    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.0f, ordinal = 2))
    private float ModifySatHeal(float constant)//disables sat healing
    {
        return Config.noSatHeal.get() ? Integer.MAX_VALUE : 0;
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 1.0f, ordinal = 0))
    private float ModifySatDrain(float constant, @Local LocalRef<Player> player)
    {
        return player.get().hasEffect(ModEffects.STUFFED.get()) ? 0 : 1;
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 1, ordinal = 0))
    private int ModifyHungerDrain(int constant, @Local LocalRef<Player> player)
    {
        return player.get().hasEffect(ModEffects.STUFFED.get()) ? 0 : 1;
    }
}
