package com.theishiopian.parrying.Enchantment;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import org.jetbrains.annotations.NotNull;

public class RiposteEnchantment extends Enchantment
{
    public RiposteEnchantment()
    {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public int getMinCost(int in) {
        return 25;
    }

    public int getMaxCost(int in) {
        return 45;
    }

    public int getMaxLevel() {
        return 1;
    }

    public boolean checkCompatibility(@NotNull Enchantment toCheck)
    {
        return !(toCheck instanceof SweepingEdgeEnchantment) && !(toCheck instanceof DeflectingEnchantment) && super.checkCompatibility(toCheck);
    }

    public boolean canEnchant(ItemStack toEnchant)
    {
        return toEnchant.getItem() instanceof SwordItem && Config.riposteEnchantEnabled.get();
    }

    public boolean canApplyAtEnchantingTable(@NotNull ItemStack toEnchant)
    {
        return canEnchant(toEnchant);
    }
}