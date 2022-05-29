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
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

//TODO bug fixes
//TODO implement jonathan's event to allow arrows to be withdrawn by bows etc
//TODO item model overrides based on count
//TODO fix crafting
//TODO investigate dyeing? how does leather armor work?
public class QuiverItem extends Item
{
    public static void registerCapability(RegisterCapabilitiesEvent event)
    {
        event.register(QuiverCapability.class);
    }
    public QuiverItem(Properties pProperties)
    {
        super(pProperties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new QuiverCapability();
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static QuiverCapability getCapability(ItemStack quiver)
    {
        LazyOptional<IItemHandler> handler = quiver.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (handler.isPresent() && handler.orElse(null) instanceof QuiverCapability capability) return capability;
        return null;
    }
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

    @Override
    public boolean overrideStackedOnOther(@NotNull ItemStack quiverStack, @NotNull Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer)
    {
        QuiverCapability c = getCapability(quiverStack);
        if(c == null)return false;

        if (pAction != ClickAction.SECONDARY)
        {
            return false;
        }
        else
        {
            ItemStack toStackOnto = pSlot.getItem();
            if (toStackOnto.isEmpty() && c.count > 0)
            {
                Debug.log(c.count);
                this.playRemoveOneSound(pPlayer);
                removeOneStack(quiverStack).ifPresent((toInsert) -> addItem(quiverStack, pSlot.safeInsert(toInsert)));
            }
            else if (toStackOnto.getItem().canFitInsideContainerItems())
            {
                Debug.log(c.count);
                int amountToTake = (256 - getTotalWeight(quiverStack)) / getWeightOfItem(toStackOnto);
                if (addItem(quiverStack, pSlot.safeTake(toStackOnto.getCount(), amountToTake, pPlayer)) > 0)
                {
                    this.playInsertSound(pPlayer);
                }
            }

            return true;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(@NotNull ItemStack pStack, @NotNull ItemStack pOther, @NotNull Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer, @NotNull SlotAccess pAccess)
    {
        if (pAction == ClickAction.SECONDARY && pSlot.allowModification(pPlayer))
        {
            if (pOther.isEmpty())
            {
                removeOneStack(pStack).ifPresent((p_186347_) ->
                {
                    this.playRemoveOneSound(pPlayer);
                    pAccess.set(p_186347_);
                });
            }
            else
            {
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
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand)
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

    public boolean isBarVisible(@NotNull ItemStack pStack)
    {
        return getTotalWeight(pStack) > 0;
    }

    public int getBarWidth(@NotNull ItemStack pStack)
    {
        return Math.min(1 + 12 * getTotalWeight(pStack) / 256, 13);
    }

    public int getBarColor(@NotNull ItemStack pStack)
    {
        return BAR_COLOR;
    }

    private static int addItem(ItemStack quiverStack, ItemStack stackToInsert)
    {
        if (!stackToInsert.isEmpty() && stackToInsert.getItem().canFitInsideContainerItems() && stackToInsert.is(ItemTags.ARROWS))
        {
            QuiverCapability c =  QuiverItem.getCapability(quiverStack);
            if(c == null || c.count == 256)return 0;

            int currentWeight = getTotalWeight(quiverStack);
            int weightOfInsert = getWeightOfItem(stackToInsert);
            int amountToAdd = Math.min(stackToInsert.getCount(), (256 - currentWeight) / weightOfInsert);
            if (amountToAdd == 0)
            {
                return 0;
            }
            else
            {
                boolean inserted = false;

                for (int i = 0; i < c.stacksList.size(); i++)
                {
                    if(!ItemStack.isSameItemSameTags(c.stacksList.get(i), stackToInsert))continue;
                    if(c.stacksList.get(i).getCount() + stackToInsert.getCount() > stackToInsert.getMaxStackSize())continue;

                    c.stacksList.get(i).grow(amountToAdd);

                    inserted = true;
                }

                if(!inserted)
                {
                    ItemStack toAdd = stackToInsert.copy();
                    toAdd.setCount(amountToAdd);
                    c.stacksList.add(toAdd);
                }

                c.count += amountToAdd;

                return amountToAdd;
            }
        }
        else
        {
            return 0;
        }
    }

    //technically unneeded. at least for a quiver...
    private static int getWeightOfItem(ItemStack pStack)
    {
        return 64 / pStack.getMaxStackSize();
    }

    private static int getTotalWeight(ItemStack pStack)
    {
        QuiverCapability c = QuiverItem.getCapability(pStack);
        if(c == null)return 0;

        return c.stacksList.stream().mapToInt(stack -> getWeightOfItem(stack) * stack.getCount()).sum();
    }

    private static Optional<ItemStack> removeOneStack(ItemStack quiverStack)
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
            c.count -= stackToRemove.getCount();
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

    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack quiverStack)
    {
        QuiverCapability c = QuiverItem.getCapability(quiverStack);
        if(c ==  null) return Optional.empty();
        return Optional.of(new BundleTooltip(c.getNonnullStackList(), getTotalWeight(quiverStack)));
    }

    public void appendHoverText(@NotNull ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced)
    {
        pTooltipComponents.add((new TranslatableComponent("filter.parrying.arrows")).withStyle(ChatFormatting.DARK_RED));
        pTooltipComponents.add((new TranslatableComponent("item.minecraft.bundle.fullness", getTotalWeight(pStack), 256)).withStyle(ChatFormatting.GRAY));
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

    static class QuiverCapability implements ICapabilityProvider, INBTSerializable<CompoundTag>
    {
        public int count = 0;
        public ArrayList<ItemStack> stacksList = new ArrayList<>();

        public QuiverCapability()
        {
            super();
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
            for (ItemStack itemStack : stacksList)
            {
                if (!itemStack.isEmpty())
                {
                    CompoundTag itemTag = new CompoundTag();
                    itemStack.save(itemTag);
                    nbtTagList.add(itemTag);
                }
            }
            CompoundTag nbt = new CompoundTag();
            nbt.put("Items", nbtTagList);
            nbt.putInt("Count", count);
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
                stacksList.add(ItemStack.of(itemTags));
            }
            count = nbt.getInt("Count");
        }

        //borrowed from immersive engineering, modified for use here
        public static <T> LazyOptional<T> constantOptional(T val)
        {
            LazyOptional<T> result = LazyOptional.of(() -> Objects.requireNonNull(val));
            result.resolve();
            return result;
        }

        private final LazyOptional<QuiverCapability> quiverCapabilityLazyOptional = constantOptional(this);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
        {
            return quiverCapabilityLazyOptional.cast();
        }
    }
}

