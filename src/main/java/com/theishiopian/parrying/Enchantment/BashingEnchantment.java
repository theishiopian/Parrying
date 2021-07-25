package com.theishiopian.parrying.Enchantment;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class BashingEnchantment extends Enchantment
{
    public  BashingEnchantment()
    {
        super(Rarity.RARE, EnchantmentType.BREAKABLE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
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
    
    public boolean checkCompatibility(Enchantment toCheck)
    {
        return true;
    }

    public boolean canEnchant(ItemStack p_92089_1_)
    {
        return p_92089_1_.isShield(null) && Config.bashingEnchantEnabled.get();
    }

    public boolean canApplyAtEnchantingTable(ItemStack p_92089_1_)
    {
        return canEnchant(p_92089_1_);
    }
}