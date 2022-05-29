package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Utility.Debug;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
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
        if (handler.isPresent() && handler.orElse(null) instanceof QuiverCapability capability) return capability;
        return null;
    }
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

    @Override
    public boolean overrideStackedOnOther(ItemStack pStack, Slot pSlot, ClickAction pAction, Player pPlayer)
    {
        if (pAction != ClickAction.SECONDARY)
        {
            return false;
        }
        else
        {
            ItemStack itemstack = pSlot.getItem();
            if (itemstack.isEmpty())
            {
                this.playRemoveOneSound(pPlayer);
                removeOne(pStack).ifPresent((p_150740_) -> addItem(pStack, pSlot.safeInsert(p_150740_)));
            }
            else if (itemstack.getItem().canFitInsideContainerItems())
            {
                int i = (64 - getContentWeight(pStack)) / getWeight(itemstack);
                int j = addItem(pStack, pSlot.safeTake(itemstack.getCount(), i, pPlayer));
                if (j > 0)
                {
                    this.playInsertSound(pPlayer);
                }
            }

            return true;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess)
    {
        if (pAction == ClickAction.SECONDARY && pSlot.allowModification(pPlayer))
        {
            Debug.log("VALID");
            if (pOther.isEmpty())
            {
                removeOne(pStack).ifPresent((p_186347_) ->
                {
                    this.playRemoveOneSound(pPlayer);
                    pAccess.set(p_186347_);
                });
            }
            else
            {
                Debug.log("trying to add item");
                int i = addItem(pStack, pOther);
                if (i > 0)
                {
                    this.playInsertSound(pPlayer);
                    pOther.shrink(i);
                }
            }

            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand)
    {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if (dropContents(itemstack, pPlayer))
        {
            this.playDropContentsSound(pPlayer);
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
        } else
        {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    public boolean isBarVisible(ItemStack pStack)
    {
        return getContentWeight(pStack) > 0;
    }

    public int getBarWidth(ItemStack pStack)
    {
        return Math.min(1 + 12 * getContentWeight(pStack) / 256, 13);
    }

    public int getBarColor(ItemStack pStack)
    {
        return BAR_COLOR;
    }

    private static int addItem(ItemStack quiverStack, ItemStack stackToInsert)
    {
        if (!stackToInsert.isEmpty() && stackToInsert.getItem().canFitInsideContainerItems())
        {
            QuiverCapability c =  QuiverItem.getCapability(quiverStack);
            if(c == null || c.count ==256)return 0;

            int currentWeight = getContentWeight(quiverStack);
            int weightOfInsert = getWeight(stackToInsert);
            int k = Math.min(stackToInsert.getCount(), (256 - currentWeight) / weightOfInsert);
            if (k == 0)
            {
                return 0;
            }
            else
            {
                boolean inserted = false;

                for (int i = 0; i < c.stacksList.size(); i++)
                {
                    if(!ItemStack.isSameItemSameTags(c.stacksList.get(i), stackToInsert))continue;

                    c.stacksList.get(i).grow(k);

                    inserted = true;
                }

                if(!inserted)
                {
                    ItemStack toAdd = stackToInsert.copy();
                    toAdd.setCount(k);
                    c.stacksList.add(toAdd);
                }

                c.count += k;

                return k;
            }
        }
        else
        {
            return 0;
        }
    }

    private static int getWeight(ItemStack pStack)
    {
        return 64 / pStack.getMaxStackSize();
    }

    private static int getContentWeight(ItemStack pStack)
    {
        QuiverCapability c = QuiverItem.getCapability(pStack);
        if(c == null)return 0;

        return c.stacksList.stream().mapToInt(stack -> getWeight(stack) * stack.getCount()).sum();
    }

    private static Optional<ItemStack> removeOne(ItemStack quiverStack)
    {
        QuiverCapability c = QuiverItem.getCapability(quiverStack);

        if(c == null)return Optional.empty();

        if (c.count == 0)
        {
            return Optional.empty();
        }
        else
        {
            ItemStack stackToRemove = c.stacksList.remove(0);
            if(!stackToRemove.isEmpty())c.count -= stackToRemove.getCount();
            return Optional.of(stackToRemove);
        }
    }

    private static boolean dropContents(ItemStack quiverStack, Player pPlayer)
    {
        QuiverCapability c = QuiverItem.getCapability(quiverStack);
        if (c == null || c.count == 0)
        {
            return false;
        }
        else
        {
            if (pPlayer instanceof ServerPlayer)
            {
                for (int i = 0; i < c.stacksList.size(); i++)
                {
                    pPlayer.drop(c.stacksList.get(0), true);
                }
            }

            c.count = 0;

            return true;
        }
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack quiverStack)
    {
        QuiverCapability c = QuiverItem.getCapability(quiverStack);
        if(c ==  null) return Optional.empty();
        return Optional.of(new BundleTooltip(c.getNonnullStackList(), getContentWeight(quiverStack)));
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        pTooltipComponents.add((new TranslatableComponent("item.minecraft.bundle.fullness", getContentWeight(pStack), 256)).withStyle(ChatFormatting.GRAY));
    }

    public void onDestroyed(ItemEntity pItemEntity)
    {
        Level level = pItemEntity.level;
        if(!level.isClientSide)
        {
            QuiverCapability c = QuiverItem.getCapability(pItemEntity.getItem());
            if(c == null || c.count == 0)return;

            c.stacksList.forEach((stack) -> level.addFreshEntity(new ItemEntity(level, pItemEntity.getX(), pItemEntity.getY(), pItemEntity.getZ(), stack)));
        }
    }

    private void playRemoveOneSound(Entity entity)
    {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity)
    {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

    private void playDropContentsSound(Entity entity)
    {
        entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

    public static class QuiverCapability implements ICapabilityProvider, INBTSerializable<CompoundTag>, IItemHandler
    {
        public int count = 0;
        public final int max = 256;

        public ArrayList<ItemStack> stacksList = new ArrayList<>();

        public QuiverCapability(ItemStack quiver)
        {
            super();
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
        {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, LazyOptional.of(() -> this));
        }

        public NonNullList<ItemStack> getNonnullStackList()
        {
            NonNullList<ItemStack> stacks = NonNullList.withSize(this.stacksList.size(), ItemStack.EMPTY);
            for (int i = 0; i < stacks.size(); i++)
            {
                stacks.set(i, this.stacksList.get(i));
            }
            return stacks;
        }

        @Override
        public CompoundTag serializeNBT()
        {
            ListTag nbtTagList = new ListTag();
            for (int i = 0; i < stacksList.size(); i++)
            {
                if (!stacksList.get(i).isEmpty())
                {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putInt("Slot", i);
                    stacksList.get(i).save(itemTag);
                    nbtTagList.add(itemTag);
                }
            }
            CompoundTag nbt = new CompoundTag();
            nbt.put("Items", nbtTagList);
            nbt.putInt("Size", stacksList.size());
            return nbt;


        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
            stacksList = new ArrayList<>();
            for (int i = 0; i < tagList.size(); i++)
            {
                CompoundTag itemTags = tagList.getCompound(i);
                int slot = itemTags.getInt("Slot");

                ItemStack toAdd = ItemStack.of(itemTags);
                //TODO somewhere in here the list is getting reset, make sure data is copying properly

                stacksList.add(slot, toAdd);
            }
        }

        @Override
        public int getSlots()
        {
            return stacksList.size();
        }

        @NotNull
        @Override
        public ItemStack getStackInSlot(int slot)
        {
            return stacksList.get(slot);
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
        {
            return stacksList.set(slot, stack);
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return getStackInSlot(slot);
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return 256;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack)
        {
            return stack.is(ItemTags.ARROWS);
        }
    }
}

