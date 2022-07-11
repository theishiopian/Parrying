package com.theishiopian.parrying.Capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;

public interface IPersistentCapability<C>
{
    Capability<C> getDefaultInstance();

    CompoundTag serializeNBT(CompoundTag nbt);

    void deserializeNBT(CompoundTag nbt);
}
