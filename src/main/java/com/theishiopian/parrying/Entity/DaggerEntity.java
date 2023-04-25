package com.theishiopian.parrying.Entity;

import com.theishiopian.parrying.Items.DaggerItem;
import com.theishiopian.parrying.Items.OilPotionItem;
import com.theishiopian.parrying.Registration.ModEntities;
import com.theishiopian.parrying.Registration.ModSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
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

public class DaggerEntity extends AbstractArrow implements IEntityAdditionalSpawnData
{
    public ItemStack daggerItem;
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(DaggerEntity.class, EntityDataSerializers.BOOLEAN);
    private boolean hasImpacted;

    public DaggerEntity(EntityType<? extends DaggerEntity> type, Level world)
    {
        super(type, world);
    }

    public DaggerEntity(Level world, LivingEntity owner, ItemStack item)
    {
        super(ModEntities.DAGGER.get(), owner, world);
        this.daggerItem = item.copy();
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
        if (this.inGroundTime > 1 && !hasImpacted)
        {
            this.hasImpacted = true;
        }

        if(!hasImpacted)
        {
            ticksSpinning++;
        }

        super.tick();
    }

    protected @NotNull ItemStack getPickupItem()
    {
        return this.daggerItem.copy();
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

    public boolean GetHasImpacted()
    {
        return hasImpacted;
    }

    protected void onHitEntity(EntityHitResult hitResult)
    {
        Entity entity = hitResult.getEntity();
        LivingEntity living = entity instanceof LivingEntity ? (LivingEntity)entity : null;
        float damage = ((DaggerItem)daggerItem.getItem()).getDamage() * 1.5f;//todo add config

        Entity owner = this.getOwner();
        DamageSource src = owner == null ? new IndirectEntityDamageSource("dagger", this, this.getOwner()).setProjectile(): new IndirectEntityDamageSource("dagger.player", this, this.getOwner()).setProjectile();

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

                var effects = PotionUtils.getMobEffects(daggerItem);

                for (MobEffectInstance effect : effects)
                {
                    var e = effect.getEffect();
                    var dur = effect.getDuration() * OilPotionItem.DURATION_MOD;
                    var amp = effect.getAmplifier();

                    living.level.playSound(null, living.blockPosition(), ModSoundEvents.CLEANSE.get(), SoundSource.PLAYERS, 0.4F, 0.8F + living.getLevel().getRandom().nextFloat() * 0.2F);

                    if(e.isInstantenous())
                    {
                        e.applyInstantenousEffect(this, owner, living, amp, 1);
                    }
                    else
                    {
                        living.addEffect(new MobEffectInstance(e, (int) dur, amp, effect.isAmbient(),effect.isVisible()), living);
                    }
                }

                daggerItem.removeTagKey("CustomPotionColor");
                daggerItem.removeTagKey("Potion");
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
        if (tag.contains("Dagger", 10))
        {
            this.daggerItem = ItemStack.of(tag.getCompound("Dagger"));
        }

        this.hasImpacted = tag.getBoolean("DealtDamage");
    }

    public void addAdditionalSaveData(@NotNull CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.put("Dagger", this.daggerItem.save(new CompoundTag()));
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
    public Packet<?> getAddEntityPacket()
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
        buffer.writeItem(this.daggerItem);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData)
    {
        this.daggerItem = additionalData.readItem();
    }
}
