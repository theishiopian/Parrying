package com.theishiopian.parrying.Enchantment;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class PhasingCurse extends Enchantment
{
    public PhasingCurse()
    {
        super(Rarity.VERY_RARE, EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
    }

    public int getMinCost(int in)
    {
        return 25;
    }

    public int getMaxCost(int in) {
        return 50;
    }

    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isCurse()
    {
        return true;
    }
    
    public boolean checkCompatibility(Enchantment toCheck)
    {
        return !(toCheck instanceof FragileCurse);
    }

    public boolean isTreasureOnly() {
        return true;
    }

    public boolean canEnchant(ItemStack p_92089_1_)
    {
        return Config.phasingEnabled.get();
    }
}
