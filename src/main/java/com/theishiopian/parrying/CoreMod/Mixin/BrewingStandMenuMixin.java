package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.world.inventory.BrewingStandMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BrewingStandMenu.class)
public class BrewingStandMenuMixin
{
    //todo redirect method on line 147 to return false
    //todo make gui retexture

    @ModifyConstant(constant = @Constant(intValue = 17, ordinal = 1), method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V")
    private int ModifyFuelX(int x)
    {
        return Config.brewingRequiresFuel.get() ? 17 : 999;
    }

    @ModifyConstant(constant = @Constant(intValue = 17, ordinal = 2), method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V")
    private int ModifyFuelY(int y)
    {
        return Config.brewingRequiresFuel.get() ? 17 : 999;
    }
}
