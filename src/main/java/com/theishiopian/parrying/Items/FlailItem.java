package com.theishiopian.parrying.Items;

import com.google.common.collect.ImmutableMultimap;
import com.theishiopian.parrying.Registration.ModAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class FlailItem extends APItem
{
    private final float shieldPen;

    public FlailItem(Tier itemTier, int baseDamage, float baseSpeed, float baseAP, float baseSP, Properties properties)
    {
        super(itemTier, baseDamage, baseSpeed, baseAP, properties);
        shieldPen = baseSP;
    }

    @SuppressWarnings("unused")
    @Override
    public void LazyModifiers()
    {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", this.attackSpeed, AttributeModifier.Operation.ADDITION));
        builder.put(ModAttributes.AP.get(), new AttributeModifier(ModAttributes.AP_UUID, "Weapon modifier", this.armorPenetration, AttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(ModAttributes.SP.get(), new AttributeModifier(ModAttributes.SP_UUID, "Weapon modifier", this.shieldPen, AttributeModifier.Operation.MULTIPLY_BASE));
        this.defaultModifiers = builder.build();
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction)
    {
        if(toolAction == ToolActions.SWORD_SWEEP)
        {
            return true;
        }

        return super.canPerformAction(stack, toolAction);
    }
}
