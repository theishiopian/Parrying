package com.theishiopian.parrying.Enchantment;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.jetbrains.annotations.NotNull;

public class SplashProtectionEnchantment extends Enchantment
{
    public SplashProtectionEnchantment()
    {
        super(Rarity.RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD});
    }

    public int getMinCost(int in) {
        return 4;
    }

    public int getMaxCost(int in) {
        return  6;
    }

    public int getMaxLevel() {
        return 4;
    }
    
    public boolean checkCompatibility(@NotNull Enchantment toCheck)
    {
        return !(toCheck instanceof ProtectionEnchantment);
    }

    public boolean canEnchant(ItemStack toEnchant)
    {
        return Config.splashProtectionEnabled.get() && toEnchant.getItem() instanceof ArmorItem;
    }

    public boolean canApplyAtEnchantingTable(@NotNull ItemStack toEnchant)
    {
        return canEnchant(toEnchant);
    }
}