package com.theishiopian.parrying.Entity;

import com.theishiopian.parrying.Items.SpearItem;
import com.theishiopian.parrying.Registration.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SpearEntity extends AbstractArrow implements IEntityAdditionalSpawnData
{
    public ItemStack spearItem;
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(SpearEntity.class, EntityDataSerializers.BOOLEAN);
    private boolean hasImpacted;

    public SpearEntity(EntityType<? extends SpearEntity> type, Level world)
    {
        super(type, world);
    }

    public SpearEntity(Level world, LivingEntity owner, ItemStack item)
    {
        super(ModEntities.SPEAR.get(), owner, world);
        this.spearItem = item.copy();
        this.entityData.set(ID_FOIL, item.hasFoil());
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
        }

        super.tick();
    }

    protected @NotNull ItemStack getPickupItem()
    {
        return this.spearItem.copy();
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unused")
    public boolean isFoil()
    {
        return this.entityData.get(ID_FOIL);
    }

    @Nullable
    protected EntityHitResult findHitEntity(@NotNull Vec3 position, @NotNull Vec3 projection)
    {
        return this.hasImpacted ? null : super.findHitEntity(position, projection);
    }

    protected void onHitEntity(EntityHitResult p_213868_1_)
    {
        Entity entity = p_213868_1_.getEntity();
        LivingEntity living = entity instanceof LivingEntity ? (LivingEntity)entity : null;
        float damage = ((SpearItem)spearItem.getItem()).getDamage() * 1.5f;//todo add config

        Entity owner = this.getOwner();
        DamageSource src = owner == null ? new IndirectEntityDamageSource("spear", this, this.getOwner()).setProjectile(): new IndirectEntityDamageSource("spear.player", this, this.getOwner()).setProjectile();
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

    public void readAdditionalSaveData(@NotNull CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Spear", 10))
        {
            this.spearItem = ItemStack.of(tag.getCompound("Spear"));
        }

        this.hasImpacted = tag.getBoolean("DealtDamage");
    }

    public void addAdditionalSaveData(@NotNull CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.put("Spear", this.spearItem.save(new CompoundTag()));
        tag.putBoolean("DealtDamage", this.hasImpacted);
    }

    public void tickDespawn()
    {
        if (this.pickup != AbstractArrow.Pickup.ALLOWED)
        {
            super.tickDespawn();
        }
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(double x, double y, double z)
    {
        return true;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer)
    {
        buffer.writeItem(this.spearItem);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData)
    {
        this.spearItem = additionalData.readItem();
    }
}
