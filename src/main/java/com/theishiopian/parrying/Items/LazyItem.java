package com.theishiopian.parrying.Items;

import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.TieredItem;
import org.jetbrains.annotations.NotNull;

/**
 * This class defines an item that only builds its attribute modifiers at runtime.
 * This is a workaround for the registration order of attributes in forge.
 */
@SuppressWarnings("deprecation")//it's not deprecated if vanilla still uses it
public abstract class LazyItem extends TieredItem
{
    protected Multimap<Attribute, AttributeModifier> defaultModifiers;
    protected int baseDamage;
    protected float baseSpeed;
    protected final float attackDamage, attackSpeed;
    public LazyItem(IItemTier tier, Properties properties, int baseDamage, float baseSpeed)
    {
        super(tier, properties);
        this.baseDamage = baseDamage;
        this.baseSpeed = baseSpeed;
        this.attackDamage = (float)baseDamage + tier.getAttackDamageBonus();
        this.attackSpeed = baseSpeed;

    }

    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlotType slotType)
    {
        if(this.defaultModifiers == null)
        {
            LazyModifiers();
        }

        return slotType == EquipmentSlotType.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slotType);
    }

    protected abstract void LazyModifiers();
}
