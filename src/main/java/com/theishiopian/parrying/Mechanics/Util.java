package com.theishiopian.parrying.Mechanics;

import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class Util
{
    public static boolean IsWeapon(ItemStack stack)
    {
        return stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE);
    }
}
