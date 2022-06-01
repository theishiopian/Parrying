package com.theishiopian.parrying.Enchantment;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Items.QuiverItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class IntrusiveCurse extends Enchantment
{
    public IntrusiveCurse()
    {
        super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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


    public boolean canEnchant(ItemStack toEnchant)
    {
        return toEnchant.getItem() instanceof QuiverItem && Config.phasingCurseEnabled.get();
    }

    public boolean isTreasureOnly() {
        return Config.isIntrusionTreasure.get();
    }
}