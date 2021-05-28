package com.theishiopian.parrying.Enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.SweepingEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public class DeflectingEnchantment extends Enchantment
{
    public DeflectingEnchantment()
    {
        super(Rarity.RARE, EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
    }

    public int getMinCost(int in) {
        return 5 + (in - 1) * 9;
    }

    public int getMaxCost(int in) {
        return this.getMinCost(in) + 15;
    }

    public int getMaxLevel() {
        return 3;
    }
    
    public boolean checkCompatibility(Enchantment toCheck)
    {
        return !(toCheck instanceof SweepingEnchantment) && !(toCheck instanceof RiposteEnchantment) && super.checkCompatibility(toCheck);
    }

    @Override
    public boolean canEnchant(ItemStack stack)
    {
        return stack.getItem() instanceof SwordItem;
    }
}
