package com.theishiopian.parrying.Enchantment;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Items.ScabbardItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public class SwiftStrikeEnchantment extends Enchantment
{
    public SwiftStrikeEnchantment()
    {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public int getMinCost(int in) {
        return 25;
    }

    public int getMaxCost(int in) {
        return 35;
    }

    public int getMaxLevel() {
        return 1;
    }

    public boolean checkCompatibility(@NotNull Enchantment toCheck)
    {
        return super.checkCompatibility(toCheck);
    }

    public boolean canEnchant(ItemStack toEnchant)
    {
        return toEnchant.getItem() instanceof ScabbardItem && Config.swiftStrikeEnabled.get();
    }

    public boolean canApplyAtEnchantingTable(@NotNull ItemStack toEnchant)
    {
        return canEnchant(toEnchant);
    }
}