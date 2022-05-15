package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Utility.Debug;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;


public class QuiverItem extends Item
{
    public QuiverItem(Properties pProperties)
    {
        super(pProperties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new QuiverCapability(stack);
    }

    @Nullable
    public static QuiverCapability getCapability(ItemStack quiver)
    {
        LazyOptional<IItemHandler> handler = quiver.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if(handler.isPresent() && handler.orElse(null) instanceof QuiverCapability capability)return capability;
        return null;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack quiver, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess)
    {
        if(pAction == ClickAction.SECONDARY && pSlot.allowModification(pPlayer))
        {
            QuiverCapability capability = getCapability(quiver);
            if(capability == null)return false;

            if(pOther.isEmpty())
            {
                //take stack
                Debug.log("giving player arrows");
                ItemStack taken = capability.TakeArrows(1);
                Debug.log("taken: " + taken);
                pAccess.set(taken);
                return true;
            }
            else if(pOther.is(ItemTags.ARROWS))
            {
                //insert stack
                Debug.log("taking players arrows");
                int toShrink = capability.AddArrows(pOther);
                pOther.shrink(toShrink);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack pStack)
    {
        QuiverCapability c = getCapability(pStack);
        if(c == null)return false;
        return c.count > 0;
    }

    private final int barColor = Mth.color(0.4F, 0.4F, 1.0F);

    @Override
    public int getBarColor(ItemStack pStack)
    {
        return barColor;
    }

    @Override
    public int getBarWidth(ItemStack pStack)
    {
        QuiverCapability c = getCapability(pStack);
        if(c == null)return 0;
        return Math.min(13 * c.count / 256, 13);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack)
    {
        QuiverCapability capability = getCapability(pStack);

        if(capability == null)return Optional.empty();

//        for (ItemStack stack : capability.getStacks())
//        {
//            Debug.log(stack);
//        }

        return Optional.of(new BundleTooltip(capability.getStacks(), capability.count / capability.max));
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack pStack, Slot pSlot, ClickAction pAction, Player pPlayer)
    {
        if(pAction != ClickAction.SECONDARY)return false;

        ItemStack other = pSlot.getItem();

        if(other.isEmpty())
        {
            //deposit
        }
        else if(other.is(ItemTags.ARROWS))
        {
            //try take stack
        }

        return super.overrideStackedOnOther(pStack, pSlot, pAction, pPlayer);
    }

    public static class QuiverCapability implements ICapabilityProvider, INBTSerializable<CompoundTag>, IItemHandler, IItemHandlerModifiable
    {
        private int count = 0;
        private final int max = 256;

        private NonNullList<ItemStack> stacks = NonNullList.withSize(1, ItemStack.EMPTY);

        public QuiverCapability(ItemStack quiver)
        {
            super();
        }

        public int GetCount(){return count;}

        public int AddArrows(ItemStack arrows)
        {
            Debug.log("adding " + arrows);
            stacks.set(0, arrows.copy());
            Debug.log(stacks.get(0));
            count = arrows.getCount();
            return arrows.getCount();
        }

        public ItemStack TakeArrows(int amount)
        {
            ItemStack stack = stacks.get(0);
            stacks.set(0, ItemStack.EMPTY);
            return stack;
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
        {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, LazyOptional.of(() -> this));
        }

        public NonNullList<ItemStack> getStacks()
        {
            return stacks;
        }

        @Override
        public CompoundTag serializeNBT()
        {
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
            //setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : stacks.size());
            stacks = NonNullList.withSize(1, ItemStack.EMPTY);
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
            //onLoad();
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack)
        {

        }

        @Override
        public int getSlots()
        {
            return 0;
        }

        @NotNull
        @Override
        public ItemStack getStackInSlot(int slot)
        {
            return null;
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
        {
            return null;
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return null;
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return 0;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack)
        {
            return false;
        }
    }
}

