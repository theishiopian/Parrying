package com.theishiopian.parrying.Enchantment;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Items.BandolierItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public class RapidityEnchantment extends Enchantment
{
    public RapidityEnchantment()
    {
        super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public int getMinCost(int in) {
        return 10;
    }

    public int getMaxCost(int in) {
        return 50;
    }

    public int getMaxLevel() {
        return 5;
    }

    public boolean checkCompatibility(@NotNull Enchantment toCheck)
    {
        return super.checkCompatibility(toCheck);
    }

    public boolean canEnchant(ItemStack toEnchant)
    {
        return toEnchant.getItem() instanceof BandolierItem && Config.rapidityEnabled.get();
    }

    public boolean canApplyAtEnchantingTable(@NotNull ItemStack toEnchant)
    {
        return canEnchant(toEnchant);
    }
}