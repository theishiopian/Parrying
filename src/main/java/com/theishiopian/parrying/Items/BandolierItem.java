package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModItems;
import com.theishiopian.parrying.Registration.ModTags;
import com.theishiopian.parrying.Utility.ModUtil;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class BandolierItem extends AbstractBundleItem
{
    private static class ToProvide
    {
        public ToProvide(ItemStack oldStack, EquipmentSlot slot)
        {
            this.stack = oldStack;
            this.slot = slot;
        }

        public ItemStack stack;
        public EquipmentSlot slot;
    }

    public static void Add(UUID player, ItemStack toProvide, @Nullable EquipmentSlot slot)
    {
        itemsToGive.put(player, new ToProvide(toProvide, slot));
    }

    public static void Remove(UUID player)
    {
        itemsToGive.remove(player);
    }

    public static boolean Has(UUID player)
    {
        return itemsToGive.containsKey(player);
    }

    private static final HashMap<UUID, ToProvide> itemsToGive = new HashMap<>();

    public BandolierItem(Properties pProperties)
    {
        super(pProperties, 512, 64, ModTags.BANDOLIER, new TranslatableComponent("tooltip.parrying.bandolier"));
    }

    public static int GetWeightlessCount(ItemStack bandolier)
    {
        BundleItemCapability c = getActualCapability(bandolier);

        if (c == null) return 0;

        return c.stacksList.size();
    }

    private enum BandolierType
    {
        POTION, WEAPON, OTHER
    }

    public static ItemStack takeBestMatch(ItemStack bandolier, ItemStack toMatch)
    {
        BundleItemCapability c = getActualCapability(bandolier);
        if (c == null) return ItemStack.EMPTY;
        c.deflate();

        ItemStack currentBest = ItemStack.EMPTY;
        int currentBestScore = 0;
        var type = toMatch.getItem() instanceof PotionItem ? BandolierType.POTION : ModUtil.IsWeapon(toMatch) ? BandolierType.WEAPON : BandolierType.OTHER;

        for (ItemStack itemStack : c.stacksList)
        {
            if (itemStack.isEmpty()) continue;

            if (type == BandolierType.POTION && itemStack.getItem() instanceof PotionItem)
            {
                //1
                var isSamePotionType = toMatch.getItem().getClass() == itemStack.getItem().getClass();
                //2
                var isSameEffectType = PotionUtils
                        .getPotion(toMatch)
                        .getEffects().stream().findFirst().get()
                        .getEffect().isBeneficial()
                        == PotionUtils
                        .getPotion(itemStack)
                        .getEffects().stream().findFirst().get()
                        .getEffect().isBeneficial();
                //3
                var isSameEffects = PotionUtils.getMobEffects(toMatch).equals(PotionUtils.getMobEffects(itemStack));
                var score = (isSamePotionType ? 1 : 0) + (isSameEffectType ? 1 : 0) + (isSameEffects ? 1 : 0);
                if (score > currentBestScore)
                {
                    currentBest = itemStack;
                    currentBestScore = score;
                }
            }
            else if (type == BandolierType.WEAPON && ModUtil.IsWeapon(itemStack))
            {
                var isSameWeaponType = toMatch.getItem().getClass() == itemStack.getItem().getClass();
                var isSameMaterial = itemStack.getItem() instanceof TieredItem &&
                        ((TieredItem) toMatch.getItem()).getTier()
                                == ((TieredItem) itemStack.getItem()).getTier();
                var isSameEnchants = EnchantmentHelper.getEnchantments(toMatch).equals(EnchantmentHelper.getEnchantments(itemStack));
                var score = (isSameWeaponType ? 1 : 0) + (isSameMaterial ? 1 : 0) + (isSameEnchants ? 1 : 0);
                if (score > currentBestScore)
                {
                    currentBest = itemStack;
                    currentBestScore = score;
                }
            }

            var other = type == BandolierType.OTHER && itemStack.is(toMatch.getItem());
            if (other || currentBestScore == 3)
            {
                currentBest = itemStack;
                break;
            }
        }

        if(currentBestScore == 0) return takeFirstStack(bandolier);

        var toReturn = currentBest.copy();

        if(!currentBest.isEmpty())
        {
            c.stacksList.remove(currentBest);
            c.deflate();
        }

        return toReturn;
    }

    public static void findItemInBandolier(Player player)
    {
        if (!itemsToGive.containsKey(player.getUUID())) return;

        var toProvide = itemsToGive.get(player.getUUID());
        var bandolier = findBundleItem(player, ModItems.BANDOLIER.get());
        var cLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.CONTEXT.get(), bandolier);
        var newItem = cLevel > 0 ? takeBestMatch(bandolier, toProvide.stack) : takeFirstStack(bandolier);

        if (newItem.isEmpty()) return;

        var hasToProvide = toProvide.slot != null;
        var oldItemInHand = hasToProvide ? player.getItemBySlot(toProvide.slot).copy() : null;

        //give player new item
        if (hasToProvide) player.setItemSlot(toProvide.slot, newItem.copy());
        else player.getInventory().add(newItem.copy());

        var rLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.RAPIDITY.get(), bandolier);

        player.getCooldowns().addCooldown(newItem.getItem(), 20 - (rLevel * 2));
        player.inventoryMenu.sendAllDataToRemote();

        if (oldItemInHand != null) player.getInventory().add(oldItemInHand);
    }
}