package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Registration.ModItems;
import com.theishiopian.parrying.Registration.ModTags;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    public static int GetCount(ItemStack bandolier)
    {
        BundleItemCapability c = getActualCapability(bandolier);

        if(c == null) return 0;

        return c.stacksList.size();
    }

    public static void findItemInBandolier(Player player)
    {
        if(!itemsToGive.containsKey(player.getUUID())) return;

        var toProvide = itemsToGive.get(player.getUUID());//todo use this for context enchant
        var bandolier = BandolierItem.findBundleItem(player, ModItems.BANDOLIER.get());
        var newItem =  AbstractBundleItem.takeFirstStack(bandolier);//todo add smarts here with enchantment, sometimes its not the first item

        if(newItem.isEmpty()) return;

        ItemStack oldItemInHand = null;

        if(toProvide.slot != null)
        {
            oldItemInHand = player.getItemBySlot(toProvide.slot).copy();//replace any previous item in hand
        }

        //give player new item
        if(toProvide.slot != null) player.setItemSlot(toProvide.slot, newItem.copy());
        else player.getInventory().add(newItem.copy());

        player.getCooldowns().addCooldown(newItem.getItem(), 20);//todo add enchant to reduce this cooldown
        player.inventoryMenu.sendAllDataToRemote();

        if(oldItemInHand != null) player.getInventory().add(oldItemInHand);
    }
}