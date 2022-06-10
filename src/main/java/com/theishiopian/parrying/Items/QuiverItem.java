package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Network.QuiverAdvPacket;
import com.theishiopian.parrying.ParryingMod;
import com.theishiopian.parrying.Utility.ParryModUtil;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
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

    public static void AddRandomArrows(ItemStack quiver, ArrowItem item, Potion tippedEffect, float chance, int maxCount)
    {
        float random = ParryModUtil.random.nextFloat();
        if(random > chance) return;
        ItemStack stack = new ItemStack(item, ParryModUtil.random.nextInt(maxCount) + 1);
        if(tippedEffect != null && item instanceof TippedArrowItem)
        {
            PotionUtils.setPotion(stack, tippedEffect);
        }
        QuiverItem.addItem(quiver, stack, null);
    }

    public static ItemStack PeekFirstStack(ItemStack quiver)
    {
        QuiverCapability c = getCapability(quiver);
        if(c == null) return ItemStack.EMPTY;
        c.Deflate();
        return c.stacksList.get(0);
    }

    public static boolean DropAllItems(ItemStack quiver, Player player)
    {
        QuiverCapability c = getCapability(quiver);
        if(c == null || GetItemCount(quiver) == 0)return false;
        c.Deflate();

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
                removeOneStack(quiverStack).ifPresent((toInsert) -> addItem(quiverStack, pSlot.safeInsert(toInsert), pPlayer));
            }
            else if (toStackOnto.is(ItemTags.ARROWS))
            {
                int amountToTake = (256 - getTotalWeight(quiverStack)) / getWeightOfItem(toStackOnto);
                addItem(quiverStack, pSlot.safeTake(toStackOnto.getCount(), amountToTake, pPlayer), pPlayer);
            }
            else return false;
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
                Optional<ItemStack> removed = removeOneStack(pStack);
                if(removed.isPresent())
                {
                    playRemoveOneSound(pPlayer);
                    pAccess.set(removed.get());
                }
            }
            else if(!pOther.is(ItemTags.ARROWS))
            {
                return false;
            }
            else
            {
                pAccess.set(addItem(pStack, pOther, pPlayer));
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
        c.Deflate();

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

    private static ItemStack addItem(ItemStack quiverStack, ItemStack stackToInsert, @Nullable Player player)
    {
        int startingCount = stackToInsert.getCount();
        QuiverCapability c =  QuiverItem.getCapability(quiverStack);
        if(c == null)return stackToInsert;
        c.Deflate();
        if(stackToInsert.isEmpty() || !stackToInsert.is(ItemTags.ARROWS) || c.IsFull())return stackToInsert;

        for (ItemStack itemStack : c.stacksList)
        {
            if(c.IsFull())break;
            if(ItemStack.isSameItemSameTags(itemStack, stackToInsert))
            {
                //TODO these loops are stupid, but im too tired to do the math right now
                while(itemStack.getCount() < itemStack.getMaxStackSize())
                {
                    if(c.IsFull())break;
                    stackToInsert.shrink(1);
                    itemStack.grow(1);
                }
            }
        }

        if(!stackToInsert.isEmpty() && !c.IsFull())
        {
            ItemStack s = stackToInsert.copy();
            s.setCount(1);
            c.stacksList.add(s);
            stackToInsert.shrink(1);

            //TODO these loops are stupid, but im too tired to do the math right now
            while(!stackToInsert.isEmpty())
            {
                if(c.IsFull())break;
                c.stacksList.get(c.stacksList.size() - 1).grow(1);
                stackToInsert.shrink(1);
            }
        }

        if(c.IsFull())
        {
            HashSet<MobEffect> effects = new HashSet<>();

            for (ItemStack itemStack : c.stacksList)
            {
                effects.add(PotionUtils.getMobEffects(itemStack).get(0).getEffect());
            }

            if(effects.size() >= 8)
            {
                ParryingMod.channel.sendToServer(new QuiverAdvPacket());
            }
        }

        if(player != null && player.level.isClientSide && startingCount != stackToInsert.getCount())
        {
            playInsertSound(player);
        }

        return stackToInsert.copy();
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
        c.Deflate();

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
        }//TODO cache count to reduce overhead

        public boolean IsFull() {return GetItemCount() == 256;}

        public NonNullList<ItemStack> getNonnullStackList()
        {
            NonNullList<ItemStack> stacks = NonNullList.withSize(this.stacksList.size(), ItemStack.EMPTY);
            for (int i = 0; i < stacks.size(); i++)
            {
                stacks.set(i, this.stacksList.get(i));
            }
            return stacks;
        }

        public void Deflate()
        {
            stacksList.removeIf(itemStack -> itemStack.isEmpty() || itemStack.is(Items.AIR));
        }

        @Override
        public CompoundTag serializeNBT()
        {
            ListTag nbtTagList = new ListTag();
            for (ItemStack itemStack : stacksList)
            {
                if (!itemStack.isEmpty() && !itemStack.is(Items.AIR))
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

