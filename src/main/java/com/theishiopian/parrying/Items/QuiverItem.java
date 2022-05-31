package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Network.QuiverAdvPacket;
import com.theishiopian.parrying.ParryingMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
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
import java.util.*;

public class QuiverItem extends Item implements DyeableLeatherItem
{
    public static void registerCapability(RegisterCapabilitiesEvent event)
    {
        event.register(QuiverCapability.class);
    }
    public QuiverItem(Properties pProperties)
    {
        super(pProperties.stacksTo(1));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new QuiverCapability();
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    private static QuiverCapability getCapability(ItemStack quiver)
    {
        LazyOptional<IItemHandler> handler = quiver.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (handler.isPresent() && handler.orElse(null) instanceof QuiverCapability capability) return capability;
        return null;
    }
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

    @Override
    public CompoundTag getShareTag(ItemStack stack)
    {
        CompoundTag tag = super.getShareTag(stack)==null? stack.getOrCreateTag() : super.getShareTag(stack);
        QuiverCapability c = getCapability(stack);
        if(c!=null && tag != null)
        {
            tag.put("quiver", c.serializeNBT());
        }

        return tag;
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundTag nbt)
    {
        QuiverCapability c = getCapability(stack);
        if(c!=null && nbt != null)
        {
            c.deserializeNBT(nbt.getCompound("quiver"));
        }
        super.readShareTag(stack, nbt);
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player)
    {
        if(!player.isCrouching())return super.onDroppedByPlayer(item, player);
        if(!QuiverItem.DropAllItems(item, player))return super.onDroppedByPlayer(item, player);
        return false;
    }

    public static int GetItemCount(ItemStack quiver)
    {
        QuiverCapability c = getCapability(quiver);
        if(c == null) return 0;
        return c.GetItemCount();
    }



    public static ItemStack PeekFirstStack(ItemStack quiver)
    {
        QuiverCapability c = getCapability(quiver);
        if(c == null) return ItemStack.EMPTY;
        return c.stacksList.get(0);
    }

    public static boolean DropAllItems(ItemStack quiver, Player player)
    {
        QuiverCapability c = getCapability(quiver);
        if(c == null || GetItemCount(quiver) == 0)return false;

        playDropContentsSound(player);

        for (ItemStack itemStack : c.stacksList)
        {
            player.drop(itemStack.copy(), true);
        }

        c.stacksList.clear();

        return true;
    }

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
            if (toStackOnto.isEmpty() && c.GetItemCount() > 0)
            {
                playRemoveOneSound(pPlayer);
                removeOneStack(quiverStack).ifPresent((toInsert) -> addItem(quiverStack, pSlot.safeInsert(toInsert)));
            }
            else if (toStackOnto.is(ItemTags.ARROWS))
            {
                int amountToTake = (256 - getTotalWeight(quiverStack)) / getWeightOfItem(toStackOnto);
                if (addItem(quiverStack, pSlot.safeTake(toStackOnto.getCount(), amountToTake, pPlayer)) > 0)
                {
                    playInsertSound(pPlayer);
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
                    playRemoveOneSound(pPlayer);
                    pAccess.set(p_186347_);
                });
            }
            else if(!pOther.is(ItemTags.ARROWS))
            {
                return false;
            }
            else
            {
                int i = addItem(pStack, pOther);
                if (i > 0)
                {
                    playInsertSound(pPlayer);
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
        ItemStack quiver = pPlayer.getItemInHand(pUsedHand);

        QuiverCapability c = getCapability(quiver);
        if(c == null)return super.use(pLevel, pPlayer, pUsedHand);

        if(c.GetItemCount() > 0)
        {
            c.stacksList.add(c.stacksList.remove(0).copy());
            pPlayer.displayClientMessage(c.stacksList.get(0).getHoverName(), true);
            return InteractionResultHolder.sidedSuccess(quiver, pLevel.isClientSide());
        }
        else return InteractionResultHolder.fail(quiver);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext)
    {
        if(pContext.getLevel().isClientSide)return super.useOn(pContext);
        BlockPos pos = pContext.getClickedPos();
        BlockState block = pContext.getLevel().getBlockState(pos);
        if(block.is(Blocks.WATER_CAULDRON))
        {
            ItemStack quiver = pContext.getItemInHand();
            if(((DyeableLeatherItem)quiver.getItem()).hasCustomColor(quiver))
            {
                ((DyeableLeatherItem)quiver.getItem()).clearColor(quiver);
                LayeredCauldronBlock.lowerFillLevel(block, pContext.getLevel(), pos);
                return InteractionResult.sidedSuccess(pContext.getLevel().isClientSide);
            }
            else return InteractionResult.PASS;
        }
        return super.useOn(pContext);
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
        QuiverCapability c =  QuiverItem.getCapability(quiverStack);
        if(c == null)
        {
            return 0;
        }

        if(c.GetItemCount() == 256)
        {
            return 0;
        }

        if (!stackToInsert.isEmpty() && stackToInsert.getItem().canFitInsideContainerItems() && stackToInsert.is(ItemTags.ARROWS))
        {
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

                if(c.GetItemCount() == 256)
                {
                    HashSet<MobEffect> allEffects = new HashSet<>();
                    for (ItemStack itemStack : c.stacksList)
                    {
                        List<MobEffectInstance> mobEffectInstances = PotionUtils.getMobEffects(itemStack);
                        for (MobEffectInstance mobEffectInstance : mobEffectInstances)
                        {
                            allEffects.add(mobEffectInstance.getEffect());
                        }
                    }

                    if(allEffects.size() >= 8)
                    {
                        //trigger
                        ParryingMod.channel.sendToServer(new QuiverAdvPacket());
                    }
                }

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

        if (c.GetItemCount() == 0)
        {
            return Optional.empty();
        }
        else
        {
            ItemStack stackToRemove = c.stacksList.remove(0);
            return Optional.of(stackToRemove);
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
            if(c == null || c.GetItemCount() == 0)return;

            c.stacksList.forEach((stack) -> level.addFreshEntity(new ItemEntity(level, pItemEntity.getX(), pItemEntity.getY(), pItemEntity.getZ(), stack)));
        }
    }

    private static void playRemoveOneSound(Entity entity)
    {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertSound(Entity entity)
    {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

    private static void playDropContentsSound(Entity entity)
    {
        entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

    static class QuiverCapability implements ICapabilityProvider, INBTSerializable<CompoundTag>
    {
        public ArrayList<ItemStack> stacksList = new ArrayList<>();

        public QuiverCapability()
        {
            super();
        }

        public int GetItemCount()
        {
            return stacksList.stream().mapToInt(ItemStack::getCount).sum();
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

