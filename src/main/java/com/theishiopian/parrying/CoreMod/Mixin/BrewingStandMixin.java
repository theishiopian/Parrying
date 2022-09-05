package com.theishiopian.parrying.CoreMod.Mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandMixin
{
    @ModifyConstant(method = "serverTick", constant = @Constant(intValue = 400))
    private static int ModifyBrewTime(int constant)
    {
        return 120;//TODO config
    }

    @Redirect(method = "serverTick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;fuel:I", opcode = Opcodes.GETFIELD))
    private static int RedirectFuelIntakeCheck(BrewingStandBlockEntity instance)
    {
        return 1;//todo config
    }

    @Redirect(method = "canPlaceItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean RedirectAutomatedBlazePowderCheck(ItemStack instance, Item pItem)
    {
        return false;//todo config
    }
}
