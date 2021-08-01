package com.theishiopian.parrying.Items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.theishiopian.parrying.Registration.ModAttributes;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class APItem extends TieredItem implements IVanishable
{
    //protected static final UUID AP_UUID = UUID.fromString("42f502a6-5bd5-4c7b-9043-3cf5d484b049");

    protected float attackDamage, attackSpeed, armorPenetration;
    protected Multimap<Attribute, AttributeModifier> defaultModifiers;

    public APItem(IItemTier itemTier, int baseDamage, float baseSpeed, float baseAP, Item.Properties properties)
    {
        super(itemTier, properties);

        this.attackDamage = (float)baseDamage + itemTier.getAttackDamageBonus();
        this.attackSpeed = baseSpeed;
        this.armorPenetration = baseAP;
    }

    public float getDamage()
    {
        return this.attackDamage;
    }

    public boolean canAttackBlock(BlockState state, World world, BlockPos pos, PlayerEntity player)
    {
        return !player.isCreative();
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity enemy, LivingEntity player)
    {
        stack.hurtAndBreak(1, player, (playerIn) -> playerIn.broadcastBreakEvent(EquipmentSlotType.MAINHAND));

        return true;
    }

    public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity player)
    {
        if (state.getDestroySpeed(world, pos) != 0.0F)
        {
            stack.hurtAndBreak(2, player, (playerIn) -> playerIn.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
        }

        return true;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType slotType)
    {
        if(this.defaultModifiers == null)
        {
            LazyModifiers();
        }

        return slotType == EquipmentSlotType.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slotType);
    }

    //shockingly lazy
    protected void LazyModifiers()
    {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", this.attackSpeed, AttributeModifier.Operation.ADDITION));
        builder.put(ModAttributes.AP.get(), new AttributeModifier(ModAttributes.AP_UUID, "Weapon modifier", this.armorPenetration, AttributeModifier.Operation.MULTIPLY_BASE));
        this.defaultModifiers = builder.build();
    }
}
