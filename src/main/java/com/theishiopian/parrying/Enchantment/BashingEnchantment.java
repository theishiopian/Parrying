package com.theishiopian.parrying.Enchantment;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public class BashingEnchantment extends Enchantment
{
    public  BashingEnchantment()
    {
        super(Rarity.RARE, EnchantmentCategory.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    public int getMinCost(int in)
    {
        return 10 + in * 5;
    }

    public int getMaxCost(int in) {
        return this.getMinCost(in) + 20;
    }

    public int getMaxLevel() {
        return 3;
    }
    
    public boolean checkCompatibility(@NotNull Enchantment toCheck)
    {
        return true;
    }

    public boolean canEnchant(ItemStack toEnchant)
    {
        return toEnchant.getItem() instanceof ShieldItem && Config.bashingEnchantEnabled.get();
    }

    public boolean canApplyAtEnchantingTable(@NotNull ItemStack toEnchant)
    {
        return canEnchant(toEnchant);
    }
}