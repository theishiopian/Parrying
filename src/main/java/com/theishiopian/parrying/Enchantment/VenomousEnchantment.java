package com.theishiopian.parrying.Enchantment;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Items.DaggerItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public class VenomousEnchantment extends Enchantment
{
    public VenomousEnchantment()
    {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public int getMinCost(int in) {
        return 4 + (in - 1) * 9;
    }

    public int getMaxCost(int in) {
        return this.getMinCost(in) + 16;
    }

    public int getMaxLevel() {
        return 3;
    }

    public boolean checkCompatibility(@NotNull Enchantment toCheck)
    {
        return !(toCheck instanceof TreacheryEnchantment);
    }

    public boolean canEnchant(ItemStack toEnchant)
    {
        return toEnchant.getItem() instanceof DaggerItem && Config.venomousEnabled.get();
    }

    public boolean canApplyAtEnchantingTable(@NotNull ItemStack toEnchant)
    {
        return canEnchant(toEnchant);
    }
}