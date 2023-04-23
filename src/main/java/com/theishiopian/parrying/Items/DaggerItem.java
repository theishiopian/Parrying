package com.theishiopian.parrying.Items;

import com.google.common.collect.ImmutableMultimap;
import com.theishiopian.parrying.Entity.DaggerEntity;
import com.theishiopian.parrying.Registration.ModAttributes;
import com.theishiopian.parrying.Utility.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DaggerItem extends LazyItem
{
    public DaggerItem(Tier tier, int baseDamage, float baseSpeed, Properties properties)
    {
        super(tier, properties, baseDamage, baseSpeed);
    }

    @Override
    protected void LazyModifiers()
    {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
        builder.put(ModAttributes.IR.get(), new AttributeModifier(ModAttributes.IR_UUID, "Weapon modifier", 5, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, @NotNull LivingEntity enemy, @NotNull LivingEntity player)
    {
        enemy.invulnerableTime = 15;
        stack.hurtAndBreak(1, player, (playerIn) -> playerIn.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    public float getDamage()
    {
        return this.attackDamage;
    }

    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand)
    {
        //Debug.log("throwing");
        ItemStack dagger = player.getItemInHand(hand);

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 0.5F, 0.4F / (ModUtil.random.nextFloat() * 0.4F + 0.8F));

        if (!world.isClientSide)
        {
            //thanks k1r0s
            if(player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ShieldItem && !player.isCrouching())
            {
                player.startUsingItem(InteractionHand.OFF_HAND);
                return InteractionResultHolder.fail(dagger);
            }

            dagger.hurtAndBreak(1, player, (playerEntity) -> playerEntity.broadcastBreakEvent(player.getUsedItemHand()));

            DaggerEntity daggerEntity = new DaggerEntity(world, player, dagger.copy());

            daggerEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1F, 1.0F);

            if (player.getAbilities().instabuild)
            {
                daggerEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }

            world.addFreshEntity(daggerEntity);

            world.playSound(null, daggerEntity, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);

            if (!player.getAbilities().instabuild)
            {
                dagger.shrink(1);
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));

        return InteractionResultHolder.sidedSuccess(dagger, world.isClientSide());
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return enchantment == Enchantments.SHARPNESS || super.canApplyAtEnchantingTable(stack, enchantment);
    }

    public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player)
    {
        return !player.isCreative();
    }
}
