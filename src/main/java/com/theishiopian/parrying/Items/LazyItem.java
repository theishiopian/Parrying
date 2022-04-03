package com.theishiopian.parrying.Items;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

/**
 * This class defines an item that only builds its attribute modifiers "just in time".
 * This is a workaround for the registration order of attributes in forge.
 */
@SuppressWarnings("deprecation")//it's not deprecated if vanilla still uses it
public abstract class LazyItem extends TieredItem implements Vanishable
{
    protected Multimap<Attribute, AttributeModifier> defaultModifiers;
    protected final int baseDamage;
    protected final float baseSpeed;
    protected final float attackDamage, attackSpeed;
    public LazyItem(Tier tier, Properties properties, int baseDamage, float baseSpeed)
    {
        super(tier, properties);
        this.baseDamage = baseDamage;
        this.baseSpeed = baseSpeed;
        this.attackDamage = (float)baseDamage + tier.getAttackDamageBonus();
        this.attackSpeed = baseSpeed;
    }

    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot slotType)
    {
        if(this.defaultModifiers == null)
        {
            LazyModifiers();
        }

        return slotType == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slotType);
    }

    //thanks k1r0s
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return enchantment.category == EnchantmentCategory.BREAKABLE || enchantment.category == EnchantmentCategory.VANISHABLE;
    }

    protected abstract void LazyModifiers();
}
