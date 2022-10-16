package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Capability.CapabilityProvider;
import com.theishiopian.parrying.Capability.IPersistentCapability;
import com.theishiopian.parrying.Network.QuiverAdvPacket;
import com.theishiopian.parrying.ParryingMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.capabilities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class AbstractBundleItem extends Item implements DyeableLeatherItem
{
    public static void registerCapability(RegisterCapabilitiesEvent event)
    {
        event.register(BundleItemCapability.class);
    }

    private final int CAPACITY;
    private final TagKey<Item> FILTER;

    public AbstractBundleItem(Properties pProperties, int capacity, TagKey<Item> filter)
    {
        super(pProperties.stacksTo(1));
        CAPACITY = capacity;
        FILTER = filter;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new CapabilityProvider<>(new BundleItemCapability(CAPACITY, FILTER)) {};
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    private static AbstractBundleItem.BundleItemCapability getActualCapability(ItemStack bundle)
    {
        return bundle.getCapability(BundleItemCapability.INSTANCE).orElse(null);
    }
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

    @Override
    public CompoundTag getShareTag(ItemStack stack)
    {
        CompoundTag tag = super.getShareTag(stack)==null? stack.getOrCreateTag() : super.getShareTag(stack);
        BundleItemCapability c = getActualCapability(stack);
        if(c!=null && tag != null)
        {
            tag.put("storage", c.serializeNBT(new CompoundTag()));
        }

        return tag;
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundTag nbt)
    {
        BundleItemCapability c = getActualCapability(stack);
        if(c!=null && nbt != null)
        {
            c.deserializeNBT(nbt.getCompound("storage"));
        }
        super.readShareTag(stack, nbt);
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player)
    {
        if(!player.isCrouching())return super.onDroppedByPlayer(item, player);
        if(!AbstractBundleItem.DropAllItems(item, player))return super.onDroppedByPlayer(item, player);
        return false;
    }

    public static int GetItemCount(ItemStack bundle)
    {
        BundleItemCapability c = getActualCapability(bundle);
        if(c == null) return 0;
        return c.GetItemCount();
    }

    public static void AddLootArrows(ItemStack bundle, LootTable table, LootContext context)
    {
        List<ItemStack> items = table.getRandomItems(context);

        for (ItemStack item : items)
        {
            AbstractBundleItem.addItem(bundle, item, null);
        }
    }

    public static ItemStack PeekFirstStack(ItemStack bundle)
    {
        BundleItemCapability c = getActualCapability(bundle);
        if(c == null) return ItemStack.EMPTY;
        c.Deflate();
        return c.stacksList.get(0);
    }

    public static boolean DropAllItems(ItemStack bundle, Player player)
    {
        BundleItemCapability c = getActualCapability(bundle);
        if(c == null || GetItemCount(bundle) == 0)return false;
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
    public boolean overrideStackedOnOther(@NotNull ItemStack bundle, @NotNull Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer)
    {
        BundleItemCapability c = getActualCapability(bundle);
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
                removeOneStack(bundle).ifPresent((toInsert) -> addItem(bundle, pSlot.safeInsert(toInsert), pPlayer));
            }
            else if (toStackOnto.is(c.FILTER))
            {
                int amountToTake = (CAPACITY - getTotalWeight(bundle)) / getWeightOfItem(toStackOnto);
                addItem(bundle, pSlot.safeTake(toStackOnto.getCount(), amountToTake, pPlayer), pPlayer);
            }
            else return false;
            return true;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(@NotNull ItemStack bundle, @NotNull ItemStack pOther, @NotNull Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer, @NotNull SlotAccess pAccess)
    {
        BundleItemCapability c = getActualCapability(bundle);
        if(c == null) return false;
        if (pAction == ClickAction.SECONDARY && pSlot.allowModification(pPlayer))
        {
            if (pOther.isEmpty())
            {
                Optional<ItemStack> removed = removeOneStack(bundle);
                if(removed.isPresent())
                {
                    playRemoveOneSound(pPlayer);
                    pAccess.set(removed.get());
                }
            }
            else if(!pOther.is(c.FILTER))
            {
                return false;
            }
            else
            {
                pAccess.set(addItem(bundle, pOther, pPlayer));
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand)
    {
        ItemStack bundle = pPlayer.getItemInHand(pUsedHand);

        BundleItemCapability c = getActualCapability(bundle);
        if(c == null)return super.use(pLevel, pPlayer, pUsedHand);
        c.Deflate();

        if(c.GetItemCount() > 0)
        {
            c.stacksList.add(c.stacksList.remove(0).copy());
            pPlayer.displayClientMessage(c.stacksList.get(0).getHoverName(), true);
            return InteractionResultHolder.sidedSuccess(bundle, pLevel.isClientSide());
        }
        else return InteractionResultHolder.fail(bundle);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext)
    {
        if(pContext.getLevel().isClientSide)return super.useOn(pContext);
        BlockPos pos = pContext.getClickedPos();
        BlockState block = pContext.getLevel().getBlockState(pos);
        if(block.is(Blocks.WATER_CAULDRON))
        {
            ItemStack bundle = pContext.getItemInHand();
            if(((DyeableLeatherItem)bundle.getItem()).hasCustomColor(bundle))
            {
                ((DyeableLeatherItem)bundle.getItem()).clearColor(bundle);
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
        return Math.min(1 + 12 * getTotalWeight(pStack) / CAPACITY, 13);
    }

    public int getBarColor(@NotNull ItemStack pStack)
    {
        return BAR_COLOR;
    }

    public static ItemStack addItem(ItemStack bundle, ItemStack stackToInsert, @Nullable Player player)
    {
        int startingCount = stackToInsert.getCount();
        BundleItemCapability c =  AbstractBundleItem.getActualCapability(bundle);
        if(c == null)return stackToInsert;
        c.Deflate();
        if(stackToInsert.isEmpty() || !stackToInsert.is(c.FILTER) || c.IsFull())return stackToInsert;

        for (ItemStack itemStack : c.stacksList)
        {
            if(c.IsFull())break;
            if(itemStack.getCount() == itemStack.getMaxStackSize())continue;
            if(ItemStack.isSameItemSameTags(itemStack, stackToInsert))
            {
                //TODO these loops are stupid, but im too tired to do the math right now
                while(itemStack.getCount() < itemStack.getMaxStackSize())
                {
                    if(c.IsFull() || stackToInsert.getCount() == 0)break;
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

            s = c.stacksList.get(c.stacksList.size() - 1);

            //TODO these loops are stupid, but im too tired to do the math right now
            while(!stackToInsert.isEmpty())
            {
                if(c.IsFull())break;
                s.grow(1);
                stackToInsert.shrink(1);
            }
        }

        //TODO move to subclass somehow?
        if(c.IsFull())
        {
            HashSet<MobEffect> effects = new HashSet<>();

            for (ItemStack itemStack : c.stacksList)
            {
                if(itemStack.is(Items.TIPPED_ARROW))effects.add(PotionUtils.getMobEffects(itemStack).get(0).getEffect());
            }

            if(effects.size() >= 8)
            {
                ParryingMod.channel.sendToServer(new QuiverAdvPacket());
            }
        }

        if(player != null && startingCount != stackToInsert.getCount())
        {
            playInsertSound(player);
        }

        return stackToInsert.copy();
    }

    private static int getWeightOfItem(ItemStack pStack)
    {
        return 64 / pStack.getMaxStackSize();
    }

    private static int getTotalWeight(ItemStack pStack)
    {
        BundleItemCapability c = AbstractBundleItem.getActualCapability(pStack);
        if(c == null)return 0;

        return c.stacksList.stream().mapToInt(stack -> getWeightOfItem(stack) * stack.getCount()).sum();
    }

    private static Optional<ItemStack> removeOneStack(ItemStack bundle)
    {
        BundleItemCapability c = AbstractBundleItem.getActualCapability(bundle);

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

    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack bundle)
    {
        BundleItemCapability c = AbstractBundleItem.getActualCapability(bundle);
        if(c ==  null) return Optional.empty();
        return Optional.of(new BundleTooltip(c.getNonnullStackList(), getTotalWeight(bundle)));
    }

    public void appendHoverText(@NotNull ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced)
    {
        pTooltipComponents.add((new TranslatableComponent("filter.parrying.arrows")).withStyle(ChatFormatting.DARK_RED));
        pTooltipComponents.add((new TranslatableComponent("item.minecraft.bundle.fullness", getTotalWeight(pStack), CAPACITY)).withStyle(ChatFormatting.GRAY));
    }

    public void onDestroyed(ItemEntity pItemEntity)
    {
        Level level = pItemEntity.level;
        if(!level.isClientSide)
        {
            BundleItemCapability c = AbstractBundleItem.getActualCapability(pItemEntity.getItem());
            if(c == null || c.GetItemCount() == 0)return;

            c.stacksList.forEach((stack) -> level.addFreshEntity(new ItemEntity(level, pItemEntity.getX(), pItemEntity.getY(), pItemEntity.getZ(), stack)));
        }
    }

    private static void playRemoveOneSound(Entity entity)
    {
        entity.level.playSound(null, entity.blockPosition(), SoundEvents.BUNDLE_REMOVE_ONE, SoundSource.PLAYERS, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertSound(Entity entity)
    {
        entity.level.playSound(null, entity.blockPosition(), SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

    private static void playDropContentsSound(Entity entity)
    {
        entity.level.playSound(null, entity.blockPosition(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 0.8F, 0.8F + entity.getLevel().getRandom().nextFloat() * 0.4F);
    }

    static class BundleItemCapability implements IPersistentCapability<BundleItemCapability>
    {
        private final int CAPACITY;
        private final TagKey<Item> FILTER;

        public ArrayList<ItemStack> stacksList = new ArrayList<>();

        public static final Capability<BundleItemCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

        @Override
        public Capability<BundleItemCapability> getDefaultInstance()
        {
            return INSTANCE;
        }
        public BundleItemCapability(int capacity, TagKey<Item> filter)
        {
            super();
            CAPACITY = capacity;
            FILTER = filter;
        }

        public int GetItemCount()
        {
            return stacksList.stream().mapToInt(ItemStack::getCount).sum();
        }

        public boolean IsFull() {return GetItemCount() == CAPACITY;}

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
        public CompoundTag serializeNBT(CompoundTag nbt)
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
    }
}

