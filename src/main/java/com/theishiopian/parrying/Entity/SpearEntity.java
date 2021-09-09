package com.theishiopian.parrying.Entity;

import com.theishiopian.parrying.ParryingMod;
import com.theishiopian.parrying.Registration.ModEntities;
import com.theishiopian.parrying.Registration.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SpearEntity extends AbstractArrowEntity
{
    public ItemStack spearItem = new ItemStack(ModItems.TEST_SPEAR.get());//TODO item
    private static final DataParameter<Boolean> ID_FOIL = EntityDataManager.defineId(SpearEntity.class, DataSerializers.BOOLEAN);
    private boolean hasImpacted;

    public SpearEntity(EntityType<? extends SpearEntity> type, World world)
    {
        super(type, world);
    }

    public SpearEntity(World world, LivingEntity owner, ItemStack item)
    {
        super(ModEntities.SPEAR.get(), owner, world);
        this.spearItem = item.copy();
        this.entityData.set(ID_FOIL, item.hasFoil());
    }

    @OnlyIn(Dist.CLIENT)
    public SpearEntity(World world, double x, double y, double z)
    {
        super(ModEntities.SPEAR.get(), x, y, z, world);
    }

    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ID_FOIL, false);
    }

    public void tick()
    {
        if (this.inGroundTime > 4 && !hasImpacted)
        {
            this.hasImpacted = true;
            ParryingMod.LOGGER.info("impact");
        }

        Entity owner = this.getOwner();//may be unneeded

        super.tick();
    }

    protected @NotNull ItemStack getPickupItem()
    {
        return this.spearItem.copy();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isFoil()
    {
        return this.entityData.get(ID_FOIL);
    }

    @Nullable
    protected EntityRayTraceResult findHitEntity(@NotNull Vector3d position, @NotNull Vector3d projection)
    {
        return this.hasImpacted ? null : super.findHitEntity(position, projection);
    }

    protected void onHitEntity(EntityRayTraceResult p_213868_1_)
    {
        Entity entity = p_213868_1_.getEntity();
        LivingEntity living = entity instanceof LivingEntity ? (LivingEntity)entity : null;
        float damage = 8.0F;//alter for enchants

        Entity owner = this.getOwner();
        DamageSource src = owner == null ? new DamageSource("spear") : new EntityDamageSource("spear.player", owner);
        this.hasImpacted = true;
        if (entity.hurt(src, damage))
        {
            if (entity.getType() == EntityType.ENDERMAN)
            {
                return;
            }

            if (living !=null)
            {
                if (owner instanceof LivingEntity)
                {
                    EnchantmentHelper.doPostHurtEffects(living, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)owner, living);
                }

                this.doPostHurtEffects(living);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));

        this.playSound(SoundEvents.TRIDENT_HIT, 1.0f, 1.0F);
    }

    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent()
    {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    public void playerTouch(@NotNull PlayerEntity player)
    {
        Entity owner = this.getOwner();
        if (owner == null || owner.getUUID() == player.getUUID())
        {
            super.playerTouch(player);
        }
    }

    public void readAdditionalSaveData(@NotNull CompoundNBT tag)
    {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Spear", 10))
        {
            this.spearItem = ItemStack.of(tag.getCompound("Spear"));
        }

        this.hasImpacted = tag.getBoolean("DealtDamage");
    }

    public void addAdditionalSaveData(@NotNull CompoundNBT tag)
    {
        super.addAdditionalSaveData(tag);
        tag.put("Spear", this.spearItem.save(new CompoundNBT()));
        tag.putBoolean("DealtDamage", this.hasImpacted);
    }

    public void tickDespawn()
    {
        if (this.pickup != AbstractArrowEntity.PickupStatus.ALLOWED)
        {
            super.tickDespawn();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(double x, double y, double z)
    {
        return true;
    }
}
