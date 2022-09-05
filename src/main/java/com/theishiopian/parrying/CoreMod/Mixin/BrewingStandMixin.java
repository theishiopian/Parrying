package com.theishiopian.parrying.CoreMod.Mixin;

import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandMixin
{
    @ModifyConstant(method = "serverTick", constant = @Constant(intValue = 400))
    private static int ModifyBrewTime(int constant)
    {
        return 120;//TODO config
    }

    //TODO change 0 on line 98 to negative infinity to disable fuel intake
    //TODO need to disable line 118 in BrewingStandBlockEntity to disable fuel loss
    //todo set 0 on line 117 to negative infinity to disable fuel check
    //todo redirect on 261 to disable automated blaze input
}
