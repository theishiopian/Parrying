package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Capability.CapabilityProvider;
import com.theishiopian.parrying.Capability.IPersistentCapability;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
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
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractBundleItem extends Item implements DyeableLeatherItem
{
    public static void registerCapability(RegisterCapabilitiesEvent event)
    {
        event.register(BundleItemCapability.class);
    }

    private final int CAPACITY;
    private final int DIVISOR;
    private final TagKey<Item> FILTER;
    private final TranslatableComponent FILTER_TOOLTIP;

    //TODO may want to replace this pattern with something else
    protected static Consumer<BundleItemCapability> POST_ADD;

    public AbstractBundleItem(Properties pProperties, int capacity, int divisor, TagKey<Item> filter, TranslatableComponent filter_tooltip)
    {
        super(pProperties.stacksTo(1));
        CAPACITY = capacity;
        FILTER = filter;
        FILTER_TOOLTIP = filter_tooltip;
        DIVISOR = divisor;
    }

    public AbstractBundleItem(Properties pProperties, int capacity, TagKey<Item> filter, TranslatableComponent filter_tooltip)
    {
        this(pProperties, capacity, 1, filter, filter_tooltip);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new CapabilityProvider<>(new BundleItemCapability(CAPACITY, FILTER)) {};
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    protected static AbstractBundleItem.BundleItemCapability getActualCapability(ItemStack bundle)
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
        if(!AbstractBundleItem.dropAllItems(item, player))return super.onDroppedByPlayer(item, player);
        return false;
    }

    public static void addLoot(ItemStack bundle, LootTable table, LootContext context)
    {
        List<ItemStack> items = table.getRandomItems(context);

        for (ItemStack item : items)
        {
            AbstractBundleItem.addItem(bundle, item, null);
        }
    }

    public static ItemStack peekFirstStack(ItemStack bundle)
    {
        BundleItemCapability c = getActualCapability(bundle);
        if(c == null) return ItemStack.EMPTY;
        c.deflate();
        return c.stacksList.get(0);
    }

    public static ItemStack takeFirstStack(ItemStack bundle)
    {
        BundleItemCapability c = getActualCapability(bundle);
        if(c == null) return ItemStack.EMPTY;
        c.deflate();
        var toTake = c.stacksList.get(0).copy();
        //Debug.log("First item in bandolier is: " + toTake.getItem());
        c.stacksList.set(0, ItemStack.EMPTY);
        c.deflate();
        return toTake;
    }

    public static boolean dropAllItems(ItemStack bundle, Player player)
    {
        BundleItemCapability c = getActualCapability(bundle);
        if(c == null || c.isEmpty())return false;
        c.deflate();

        playDropContentsSound(player);

        for (ItemStack itemStack : c.stacksList)
        {
            player.drop(itemStack.copy(), true);
        }

        c.stacksList.clear();

        return true;
    }

    public static boolean isEmpty(ItemStack bundle)
    {
        BundleItemCapability c = getActualCapability(bundle);

        return c == null || c.isEmpty();
    }

    public static boolean isFull(ItemStack bundle)
    {
        BundleItemCapability c = getActualCapability(bundle);

        return c != null && c.isFull();
    }

    public static int getWeight(ItemStack bundle)
    {
        BundleItemCapability c = getActualCapability(bundle);

        return c == null ? 0 : c.getWeight();
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
            if (toStackOnto.isEmpty() && !c.isEmpty())
            {
                playRemoveOneSound(pPlayer);
                removeOneStack(bundle).ifPresent((toInsert) -> addItem(bundle, pSlot.safeInsert(toInsert), pPlayer));
            }
            else if (toStackOnto.is(c.FILTER))
            {
                int amountToTake = (CAPACITY - c.getWeight()) / c.getWeightOfItem(toStackOnto);
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
        c.deflate();

        if(!c.isEmpty())
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

    public boolean isBarVisible(@NotNull ItemStack bundle)
    {
        BundleItemCapability c = AbstractBundleItem.getActualCapability(bundle);
        if(c == null)return false;
        return !c.isEmpty();
    }

    public int getBarWidth(@NotNull ItemStack bundle)
    {
        BundleItemCapability c = AbstractBundleItem.getActualCapability(bundle);
        if(c == null)return 0;
        return Math.min(1 + 12 * c.getWeight() / CAPACITY, 13);
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
        c.deflate();
        if(stackToInsert.isEmpty() || !stackToInsert.is(c.FILTER) || c.isFull())return stackToInsert;

        for (ItemStack itemStack : c.stacksList)
        {
            if(c.isFull())break;
            if(itemStack.getCount() == itemStack.getMaxStackSize())continue;
            if(ItemStack.isSameItemSameTags(itemStack, stackToInsert))
            {
                //TODO these loops are stupid, but im too tired to do the math right now
                while(itemStack.getCount() < itemStack.getMaxStackSize())
                {
                    if(c.isFull() || stackToInsert.getCount() == 0)break;
                    stackToInsert.shrink(1);
                    itemStack.grow(1);
                }
            }
        }

        if(!stackToInsert.isEmpty() && c.hasRoom())
        {
            ItemStack s = stackToInsert.copy();
            s.setCount(1);
            c.stacksList.add(s);
            stackToInsert.shrink(1);

            s = c.stacksList.get(c.stacksList.size() - 1);

            //TODO these loops are stupid, but im too tired to do the math right now
            while(!stackToInsert.isEmpty())
            {
                if(c.isEmpty())break;
                s.grow(1);
                stackToInsert.shrink(1);
            }
        }

        if(player != null && startingCount != stackToInsert.getCount())
        {
            playInsertSound(player);
        }

        if(POST_ADD != null)
        {
            POST_ADD.accept(c);
        }

        return stackToInsert.copy();
    }

    private static Optional<ItemStack> removeOneStack(ItemStack bundle)
    {
        BundleItemCapability c = AbstractBundleItem.getActualCapability(bundle);

        if(c == null)return Optional.empty();
        c.deflate();

        if (c.isEmpty())
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
        return Optional.of(new BundleTooltip(c.getNonnullStackList(), c.getWeight()));
    }

    public void appendHoverText(@NotNull ItemStack bundle, Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced)
    {
        BundleItemCapability c = AbstractBundleItem.getActualCapability(bundle);
        if(c ==  null) return;
        pTooltipComponents.add((FILTER_TOOLTIP).withStyle(ChatFormatting.DARK_RED));
        pTooltipComponents.add((new TranslatableComponent("item.minecraft.bundle.fullness", c.getWeight()/DIVISOR, CAPACITY/DIVISOR)).withStyle(ChatFormatting.GRAY));
    }

    public void onDestroyed(ItemEntity pItemEntity)
    {
        Level level = pItemEntity.level;
        if(!level.isClientSide)
        {
            BundleItemCapability c = AbstractBundleItem.getActualCapability(pItemEntity.getItem());
            if(c == null || c.isEmpty())return;
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

    protected static class BundleItemCapability implements IPersistentCapability<BundleItemCapability>
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

        public NonNullList<ItemStack> getNonnullStackList()
        {
            NonNullList<ItemStack> stacks = NonNullList.withSize(this.stacksList.size(), ItemStack.EMPTY);
            for (int i = 0; i < stacks.size(); i++)
            {
                stacks.set(i, this.stacksList.get(i));
            }
            return stacks;
        }

        public boolean isFull()
        {
            return getWeight() == CAPACITY;
        }

        public boolean isEmpty()
        {
            return getWeight() == 0;
        }

        public boolean hasRoom()
        {
            return getWeight() < CAPACITY;
        }

        public int getWeight()
        {
            return stacksList.stream().mapToInt(stack -> getWeightOfItem(stack) * stack.getCount()).sum();
        }

        public int getWeightOfItem(ItemStack stack)
        {
            return 64 / stack.getMaxStackSize();
        }

        public void deflate()
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

