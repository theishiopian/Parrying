package com.theishiopian.parrying.Items.Capability;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class QuiverCapability implements IItemHandler, INBTSerializable<CompoundTag>
{
    private NonNullList<ItemStack> stacks;
    private int count = 0;

    public QuiverCapability()
    {
        stacks = NonNullList.of(ItemStack.EMPTY);//added to and removed from as needed
    }

    public void setSize(int size)
    {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public int getSlots()
    {
        return count / 64;
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return stacks.get(slot);
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        Trim();
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;


        if(slot >= stacks.size())
        {
            if(count + stack.getCount() < 256)
            {
                count += stack.getCount();
                stacks.add(ItemStack.EMPTY);
            }
            else return stack;
        }

        ItemStack existingArrow = this.stacks.get(slot);

        if(existingArrow.getCount() < 64 && existingArrow.is(stack.getItem()))
        {
            if(existingArrow.getCount() + stack.getCount() <= 64)
            {
                existingArrow.grow(stack.getCount());
                stacks.set(slot, existingArrow);
                count += 64;
                return ItemStack.EMPTY;
            }
            else
            {
                int remainder = 64 - (existingArrow.getCount() + stack.getCount());
                existingArrow.setCount(64);
                count += remainder;
                stacks.set(slot, existingArrow);
                stack.setCount(remainder);
            }
        }

        return insertItem(slot + 1, stack, false);
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        ItemStack existingArrow = this.stacks.get(slot);
        ItemStack remainder = existingArrow.split(amount);

        stacks.set(slot, existingArrow);
        count -= remainder.getCount();

        Trim();

        return remainder;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        return stack.is(ItemTags.ARROWS);
    }

    private void Trim()
    {
        stacks.removeIf(ItemStack::isEmpty);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        Trim();
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < stacks.size(); i++)
        {
            if (!stacks.get(i).isEmpty())
            {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                stacks.get(i).save(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", stacks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        Trim();
        setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : stacks.size());
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++)
        {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < stacks.size())
            {
                stacks.set(slot, ItemStack.of(itemTags));
            }
        }
    }
}
