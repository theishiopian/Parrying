package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Utility.Debug;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")//it's not deprecated if mojang uses it
public class AdvancedBundle extends BundleItem
{
    public final Tag.Named<Item> filterTag;
    public final int MAX_WEIGHT;

    public AdvancedBundle(Properties pProperties, int maxWeight, Tag.Named<Item> filterTag)
    {
        super(pProperties.stacksTo(1));
        this.MAX_WEIGHT = maxWeight;
        this.filterTag = filterTag;
    }

    public static int GetMaxWeight(ItemStack potentialBundle)
    {
        if(potentialBundle.getItem() instanceof AdvancedBundle bundle)
        {
            return bundle.MAX_WEIGHT;
        }
        return -1;
    }

    public boolean overrideStackedOnOther(@NotNull ItemStack pStack, @NotNull Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer)
    {
        if (pAction == ClickAction.SECONDARY)
        {
            ItemStack itemToPotentiallyAdd = pSlot.getItem();
            if (itemToPotentiallyAdd.isEmpty())
            {
                this.playRemoveOneSound(pPlayer);
                removeOne(pStack).ifPresent((p_150740_) -> add(pStack, pSlot.safeInsert(p_150740_)));
            }
            else if (itemToPotentiallyAdd.getItem().canFitInsideContainerItems())
            {
                int i = (MAX_WEIGHT - getContentWeight(pStack)) / getWeight(itemToPotentiallyAdd);
                Debug.log(i);
                int addedAmount = add(pStack, pSlot.safeTake(itemToPotentiallyAdd.getCount(), i, pPlayer));
                if (addedAmount > 0)
                {
                    this.playInsertSound(pPlayer);
                }
                else
                {
                    //todo add fail to insert sound
                    Debug.log("fail");
                }
            }
            return true;
        }
        return false;
    }

    public static void RoundRobin(ItemStack bundle)
    {
        ListTag list = bundle.getOrCreateTag().getList("Items", 10);
        if((long) list.size() > 1)
        {
            Optional<net.minecraft.nbt.Tag> tag = Optional.ofNullable(list.get(0));
            list.remove(0);
            list.add(list.size(), tag.get());
        }
    }

    public static ItemStack TakeFirstItem(ItemStack bundle, boolean shouldReduce)
    {
        Optional<ItemStack> toRemovePotential = removeOne(bundle);
        if(toRemovePotential.isPresent())
        {
            ItemStack removed = toRemovePotential.get();
            ItemStack oneRemoved = removed.copy();
            ItemStack remainder = removed.copy();

            oneRemoved.setCount(1);

            AdvancedBundle.add(bundle, remainder);

            return  oneRemoved;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand)
    {
        ItemStack bundle = pPlayer.getItemInHand(pUsedHand);
        if(!pPlayer.isCrouching())
        {
            RoundRobin(bundle);
            pPlayer.displayClientMessage(new TranslatableComponent("bundle.parrying.shuffle"), true);
            return InteractionResultHolder.success(bundle);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public static int getStackCount(ItemStack bundle)
    {
        return GetItems(bundle).size();
    }

    public int getBarWidth(@NotNull ItemStack pStack)
    {
        return Math.min(1 + 12 * getContentWeight(pStack) / MAX_WEIGHT, 13);
    }

    public static int add(@NotNull ItemStack bundle, ItemStack stackToInsert)
    {
        if (!stackToInsert.isEmpty() && !(stackToInsert.getItem() instanceof BundleItem) && stackToInsert.is(((AdvancedBundle) bundle.getItem()).filterTag) && stackToInsert.getItem().canFitInsideContainerItems())
        {
            //Debug.log("bundle");
            CompoundTag bundleNBT = bundle.getOrCreateTag();

            //create inventory list
            if (!bundleNBT.contains("Items")) bundleNBT.put("Items", new ListTag());

            int currentWeight = getContentWeight(bundle);
            int insertWeight = getWeight(stackToInsert);
            int stackSize = stackToInsert.getItem().getMaxStackSize();
            final int toAdd = Math.min(stackToInsert.getCount(), (GetMaxWeight(bundle) - currentWeight) / insertWeight);

            if (toAdd == 0)
            {
                //todo add fail to insert sound
                //Debug.log("fail");
                return 0;
            }
            else
            {
                ListTag itemsList = bundleNBT.getList("Items", 10);

                Optional<CompoundTag> matchingItem = getMatchingItem(stackToInsert, itemsList);

                if (matchingItem.isPresent())
                {
                    CompoundTag matchingData = matchingItem.get();
                    ItemStack match = ItemStack.of(matchingData);

                    if(match.getCount() < stackSize)
                    {
                        if(match.getCount() + toAdd <= stackSize)
                        {
                            match.grow(toAdd);
                        }
                        else
                        {
                            int adding = stackSize - match.getCount();
                            match.grow(adding);
                            InsertNewStack(stackToInsert, toAdd - adding, itemsList);
                        }

                        match.save(matchingData);
                        itemsList.remove(matchingData);
                        itemsList.add(0, matchingData);
                    }
                    else InsertNewStack(stackToInsert, toAdd, itemsList);
                }
                else
                {
                    InsertNewStack(stackToInsert, toAdd, itemsList);
                }

                return toAdd;
            }
        }
        else
        {
            return 0;
        }
    }

    private static void InsertNewStack(ItemStack stackToInsert, int toAdd, ListTag itemsList)
    {
        ItemStack copy = stackToInsert.copy();
        copy.setCount(toAdd);
        CompoundTag copySaveTag = new CompoundTag();
        copy.save(copySaveTag);
        itemsList.add(0, copySaveTag);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced)
    {
        pTooltipComponents.add((new TranslatableComponent("item.minecraft.bundle.fullness", getContentWeight(pStack), MAX_WEIGHT)).withStyle(ChatFormatting.GRAY));
        String filterTag = ((AdvancedBundle)pStack.getItem()).filterTag.getName().getPath();
        pTooltipComponents.add((new TranslatableComponent("filter.parrying." + filterTag)).withStyle(ChatFormatting.RED));
    }

    public static boolean ContainsItems(ItemStack bundle)
    {
        return getContentWeight(bundle) > 0;
    }

    public static List<ItemStack> GetItems(ItemStack bundle)
    {
        return getContents(bundle).collect(Collectors.toList());
    }
}
