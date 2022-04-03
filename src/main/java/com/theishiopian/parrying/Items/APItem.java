package com.theishiopian.parrying.Items;

import com.google.common.collect.ImmutableMultimap;
import com.theishiopian.parrying.Registration.ModAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

//it's not deprecated if vanilla uses it
public class APItem extends LazyItem
{
    protected final float armorPenetration;
    public APItem(Tier itemTier, int baseDamage, float baseSpeed, float baseAP, Item.Properties properties)
    {
        super(itemTier, properties, baseDamage, baseSpeed);

        this.armorPenetration = baseAP;
    }

    public float getDamage()
    {
        return this.attackDamage;
    }

    public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player)
    {
        return !player.isCreative();
    }

    public boolean hurtEnemy(ItemStack stack, @NotNull LivingEntity enemy, @NotNull LivingEntity player)
    {
        stack.hurtAndBreak(1, player, (playerIn) -> playerIn.broadcastBreakEvent(EquipmentSlot.MAINHAND));

        return true;
    }

    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level world, BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity player)
    {
        if (state.getDestroySpeed(world, pos) != 0.0F)
        {
            stack.hurtAndBreak(2, player, (playerIn) -> playerIn.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }

        return true;
    }

    //shockingly lazy
    @Override
    public void LazyModifiers()
    {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", this.attackSpeed, AttributeModifier.Operation.ADDITION));
        builder.put(ModAttributes.AP.get(), new AttributeModifier(ModAttributes.AP_UUID, "Weapon modifier", this.armorPenetration, AttributeModifier.Operation.MULTIPLY_BASE));
        this.defaultModifiers = builder.build();
    }
}
