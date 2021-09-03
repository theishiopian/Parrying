package com.theishiopian.parrying.Enchantment;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import org.jetbrains.annotations.NotNull;

public class FragileCurse extends Enchantment
{
    public FragileCurse()
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

    public boolean checkCompatibility(@NotNull Enchantment toCheck)
    {
        return !(toCheck instanceof PhasingCurse);
    }

    public boolean isTreasureOnly() {
        return Config.isFragileTreasure.get();
    }

    public boolean canEnchant(ItemStack toEnchant)
    {
        return toEnchant.getItem() instanceof TieredItem && !(toEnchant.getItem() instanceof ArmorItem) && Config.fragileCurseEnabled.get();
    }
}