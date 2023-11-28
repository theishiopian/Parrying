package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Registration.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.TippedArrowRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TippedArrowRecipe.class)
public class TippedArrowRecipeMixin
{
    @Redirect(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0))
    private boolean matches(ItemStack stack, Item item)
    {
        return stack.is(Items.LINGERING_POTION) || stack.is(ModItems.OIL.get());
    }

    @Redirect(method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0))
    private boolean assemble(ItemStack stack, Item item)
    {
        return stack.is(Items.LINGERING_POTION) || stack.is(ModItems.OIL.get());
    }
}
