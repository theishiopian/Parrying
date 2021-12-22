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
import net.minecraft.world.entity.SlotAccess;
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

@SuppressWarnings("deprecation")
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
        if (pAction != ClickAction.SECONDARY)
        {
            return false;
        }
        else
        {
            ItemStack itemstack = pSlot.getItem();
            if (itemstack.isEmpty()) {
                this.playRemoveOneSound(pPlayer);
                removeOne(pStack).ifPresent((p_150740_) ->
                        add(pStack, pSlot.safeInsert(p_150740_)));
            }
            else if (itemstack.getItem().canFitInsideContainerItems())
            {
                int i = (MAX_WEIGHT - getContentWeight(pStack)) / getWeight(itemstack);
                int j = add(pStack, pSlot.safeTake(itemstack.getCount(), i, pPlayer));
                if (j > 0)
                {
                    this.playInsertSound(pPlayer);
                }
            }

            return true;
        }
    }

    public boolean overrideOtherStackedOnMe(@NotNull ItemStack pStack, @NotNull ItemStack pOther, @NotNull Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer, @NotNull SlotAccess pAccess)
    {
        if (pAction == ClickAction.SECONDARY && pSlot.allowModification(pPlayer))
        {
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
                int i = add(pStack, pOther);
                if (i > 0)
                {
                    this.playInsertSound(pPlayer);
                    pOther.shrink(i);
                }
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
        if(!pPlayer.isCrouching())
        {
            return InteractionResultHolder.fail(pPlayer.getItemInHand(pUsedHand));
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public int getBarWidth(@NotNull ItemStack pStack)
    {
        return Math.min(1 + 12 * getContentWeight(pStack) / MAX_WEIGHT, 13);
    }

    protected static int add(@NotNull ItemStack bundle, ItemStack stackToInsert)
    {
        if (!stackToInsert.isEmpty() && stackToInsert.is(((AdvancedBundle)bundle.getItem()).filterTag) && stackToInsert.getItem().canFitInsideContainerItems())
        {
            CompoundTag bundleNBT = bundle.getOrCreateTag();

            //create inventory list
            if (!bundleNBT.contains("Items"))bundleNBT.put("Items", new ListTag());

            int currentWeight = getContentWeight(bundle);
            int insertWeight = getWeight(stackToInsert);
            int toAdd = Math.min(stackToInsert.getCount(), (GetMaxWeight(bundle) - currentWeight) / insertWeight);

            Debug.log("Current Weight: " + currentWeight);
            Debug.log("Insert Weight: " + insertWeight);
            Debug.log("To Add: " + toAdd);

            if (toAdd == 0)
            {
                Debug.log("nothing to add");
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
                    Debug.log(match);
                    int stackSize = match.getItem().getMaxStackSize();
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
    }
}
