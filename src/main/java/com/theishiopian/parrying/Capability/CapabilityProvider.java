package com.theishiopian.parrying.Capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CapabilityProvider<C extends IPersistentCapability<C>> implements ICapabilitySerializable<CompoundTag>
{
    private final LazyOptional<C> capabilityHandler;
    private final Lazy<Capability<C>> instance;

    public CapabilityProvider(C capabilityIn)
    {
        capabilityHandler = LazyOptional.of(() -> capabilityIn);
        instance = capabilityIn::getDefaultInstance;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        return instance.get() == cap ? this.capabilityHandler.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        return this.capabilityHandler.orElseThrow(IllegalStateException::new).serializeNBT(nbt);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        this.capabilityHandler.orElseThrow(IllegalStateException::new).deserializeNBT(nbt);
    }
}
