package com.theishiopian.parrying.Items;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;

public class DaggerItem extends LazyItem //todo may want a superclass for dual wield-able items
{
    public DaggerItem(IItemTier tier, Properties properties, int baseDamage, float baseSpeed)
    {
        super(tier, properties, baseDamage, baseSpeed);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity)
    {
        //todo check for item in other hand
        return super.onEntitySwing(stack, entity);
    }

    @Override
    protected void LazyModifiers()
    {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }
}
