package com.theishiopian.parrying.Entity;

import com.theishiopian.parrying.Items.DaggerItem;
import com.theishiopian.parrying.Registration.ModEntities;
import com.theishiopian.parrying.Utility.Debug;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
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
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DaggerEntity extends AbstractArrowEntity implements IEntityAdditionalSpawnData
{
    public ItemStack daggerItem;
    private static final DataParameter<Boolean> ID_FOIL = EntityDataManager.defineId(DaggerEntity.class, DataSerializers.BOOLEAN);
    private boolean hasImpacted;

    public DaggerEntity(EntityType<? extends DaggerEntity> type, World world)
    {
        super(type, world);
    }

    public DaggerEntity(World world, LivingEntity owner, ItemStack item)
    {
        super(ModEntities.DAGGER.get(), owner, world);
        this.daggerItem = item.copy();
        Debug.log(daggerItem);
        this.entityData.set(ID_FOIL, item.hasFoil());
    }

    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ID_FOIL, false);
    }

    int ticksSpinning = 0;

    public int GetSpinTicks() {return ticksSpinning;}

    public void tick()
    {
        if (this.inGroundTime > 4 && !hasImpacted)
        {
            this.hasImpacted = true;
        }

        if(!hasImpacted)
        {
            ticksSpinning++;
        }

        //Debug.log(ticksSpinning);

        super.tick();
    }

    protected @NotNull ItemStack getPickupItem()
    {
        return this.daggerItem.copy();
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
        float damage = ((DaggerItem)daggerItem.getItem()).getDamage() * 1.5f;//todo add config

        Entity owner = this.getOwner();
        DamageSource src = owner == null ? new DamageSource("dagger") : new EntityDamageSource("dagger.player", owner);
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
        if (tag.contains("Dagger", 10))
        {
            this.daggerItem = ItemStack.of(tag.getCompound("Dagger"));
        }

        this.hasImpacted = tag.getBoolean("DealtDamage");
    }

    public void addAdditionalSaveData(@NotNull CompoundNBT tag)
    {
        super.addAdditionalSaveData(tag);
        tag.put("Dagger", this.daggerItem.save(new CompoundNBT()));
        tag.putBoolean("DealtDamage", this.hasImpacted);
    }

    public void tickDespawn()
    {
        if (this.pickup != PickupStatus.ALLOWED)
        {
            super.tickDespawn();
        }
    }

    @Override
    public @NotNull IPacket<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(double x, double y, double z)
    {
        return true;
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer)
    {
        Debug.log(this.daggerItem);
        buffer.writeItem(this.daggerItem);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData)
    {
        this.daggerItem = additionalData.readItem();
    }
}
