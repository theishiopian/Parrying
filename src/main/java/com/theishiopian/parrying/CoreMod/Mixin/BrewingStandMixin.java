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
}
