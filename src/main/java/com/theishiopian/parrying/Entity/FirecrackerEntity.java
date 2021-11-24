package com.theishiopian.parrying.Entity;

import com.theishiopian.parrying.Registration.ModEntities;
import com.theishiopian.parrying.Registration.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class FirecrackerEntity extends ProjectileItemEntity implements IRendersAsItem
{
    private static final DataParameter<ItemStack> DATA_ID_FIREWORKS_ITEM = EntityDataManager.defineId(FireworkRocketEntity.class, DataSerializers.ITEM_STACK);
    private int life;
    private int lifetime;

    public FirecrackerEntity(EntityType<? extends FirecrackerEntity> type, World world)
    {
        super(type, world);
    }

    public FirecrackerEntity(World world, double x, double y, double z, ItemStack stack)
    {
        super(ModEntities.FIRECRACKER.get(), world);
        this.life = 0;
        this.setPos(x, y, z);
        int i = 1;

        if (!stack.isEmpty() && stack.hasTag())
        {
            this.entityData.set(DATA_ID_FIREWORKS_ITEM, stack.copy());
            i += stack.getOrCreateTagElement("Fireworks").getByte("Fuse");
        }

        this.lifetime = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
    }

    public FirecrackerEntity(World world, @Nullable Entity owner, double x, double y, double z, ItemStack stack)
    {
        this(world, x, y, z, stack);
        this.setOwner(owner);
    }

    @Override
    public void tick()
    {
        super.tick();
        ++this.life;

        if (this.level.isClientSide && this.life % 2 < 2)
        {
            this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() - 0.3D, this.getZ(), this.random.nextGaussian() * 0.05D, -this.getDeltaMovement().y * 0.5D, this.random.nextGaussian() * 0.05D);
        }

        if (!this.level.isClientSide && this.life > this.lifetime)
        {
            this.explode();
        }
    }

    protected @NotNull Item getDefaultItem()
    {
        return ModItems.FIRECRACKER.get();
    }

    protected void defineSynchedData()
    {
        this.entityData.define(DATA_ID_FIREWORKS_ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (result.getType() == RayTraceResult.Type.MISS || !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, result)) {
            super.onHit(result);
        }
    }

    private void explode()
    {
        this.level.broadcastEntityEvent(this, (byte)17);
        //todo define new explosion code
        this.remove();;
    }

    private boolean hasExplosion()
    {
        ItemStack itemstack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
        CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getTagElement("Fireworks");
        ListNBT listnbt = compoundnbt != null ? compoundnbt.getList("Explosions", 10) : null;
        return listnbt != null && !listnbt.isEmpty();
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte eventData)
    {
        if (eventData == 17 && this.level.isClientSide)
        {
            if (!this.hasExplosion())
            {
                for(int i = 0; i < this.random.nextInt(3) + 2; ++i)
                {
                    this.level.addParticle(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05D, 0.005D, this.random.nextGaussian() * 0.05D);
                }
            }
            else
            {
                ItemStack stack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
                CompoundNBT tag = stack.isEmpty() ? null : stack.getTagElement("Fireworks");
                Vector3d delta = this.getDeltaMovement();
                this.level.createFireworks(this.getX(), this.getY(), this.getZ(), delta.x, delta.y, delta.z, tag);
            }
        }

        super.handleEntityEvent(eventData);
    }

    public void addAdditionalSaveData(CompoundNBT data)
    {
        super.addAdditionalSaveData(data);
        data.putInt("Life", this.life);
        data.putInt("LifeTime", this.lifetime);
        ItemStack itemstack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
        if (!itemstack.isEmpty())
        {
            data.put("FireworksItem", itemstack.save(new CompoundNBT()));
        }
    }

    public void readAdditionalSaveData(CompoundNBT data)
    {
        super.readAdditionalSaveData(data);
        this.life = data.getInt("Life");
        this.lifetime = data.getInt("LifeTime");
        ItemStack itemstack = ItemStack.of(data.getCompound("FireworksItem"));
        if (!itemstack.isEmpty())
        {
            this.entityData.set(DATA_ID_FIREWORKS_ITEM, itemstack);
        }
    }

    /**
     * This method is used by the client to get the item to render
     * TODO: do spinning animation as separate model override, use nbt to determine if in entity
     * @return the item to use
     */
    @OnlyIn(Dist.CLIENT)
    public @NotNull ItemStack getItem()
    {
        ItemStack stack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
        return stack.isEmpty() ? new ItemStack(ModItems.FIRECRACKER.get()) : stack;
    }

    public boolean isAttackable()
    {
        return false;
    }

    public @NotNull IPacket<?> getAddEntityPacket()
    {
        return new SSpawnObjectPacket(this);
    }
}