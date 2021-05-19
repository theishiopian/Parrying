package com.theishiopian.parrying;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.SweepingEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public class RiposteEnchantment extends Enchantment
{
    protected RiposteEnchantment()
    {
        super(Rarity.VERY_RARE, EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
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

    public boolean checkCompatibility(Enchantment toCheck)
    {
        return !(toCheck instanceof SweepingEnchantment) && !(toCheck instanceof DeflectingEnchantment) && super.checkCompatibility(toCheck);
    }

    @Override
    public boolean canEnchant(ItemStack stack)
    {
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem;
    }
}
