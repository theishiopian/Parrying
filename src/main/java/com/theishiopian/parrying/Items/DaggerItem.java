package com.theishiopian.parrying.Items;

import com.google.common.collect.ImmutableMultimap;
import com.theishiopian.parrying.Entity.DaggerEntity;
import com.theishiopian.parrying.Registration.ModAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class DaggerItem extends LazyItem
{
    public DaggerItem(IItemTier tier, int baseDamage, float baseSpeed, Properties properties)
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
        enemy.invulnerableTime = 5;
        stack.hurtAndBreak(1, player, (playerIn) -> playerIn.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
        return true;
    }

    public float getDamage()
    {
        return this.attackDamage;
    }

    public @NotNull ActionResult<ItemStack> use(World world, PlayerEntity player, @NotNull Hand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

        player.getCooldowns().addCooldown(this, 10);

        if (!world.isClientSide)
        {
            stack.hurtAndBreak(1, player, (playerEntity) -> playerEntity.broadcastBreakEvent(player.getUsedItemHand()));

            DaggerEntity daggerEntity = new DaggerEntity(world, player, stack.copy());

            daggerEntity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 1F, 1.0F);

            if (player.abilities.instabuild)
            {
                daggerEntity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
            }

            world.addFreshEntity(daggerEntity);

            world.playSound(null, daggerEntity, SoundEvents.TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);

            if (!player.abilities.instabuild)
            {
                stack.shrink(1);
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));

        return ActionResult.sidedSuccess(stack, world.isClientSide());
    }
}
