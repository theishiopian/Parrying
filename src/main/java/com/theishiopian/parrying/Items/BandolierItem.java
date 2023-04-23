package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModItems;
import com.theishiopian.parrying.Registration.ModTags;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.HashMap;
import java.util.UUID;

public class BandolierItem extends AbstractBundleItem
{
    public static HashMap<UUID, ItemStack> itemsToGive = new HashMap<>();
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

        ItemStack oldStack = itemsToGive.get(player.getUUID());//todo use this for context enchant

        ItemStack itemToScan;
        ItemStack bandolier = ItemStack.EMPTY;
        ItemStack priorityBandolier = ItemStack.EMPTY;
        for(int i = 45; i >= 0; i--)
        {
            itemToScan = player.getInventory().getItem(i);

            if(itemToScan.is(ModItems.BANDOLIER.get()) )
            {
                if(AbstractBundleItem.isEmpty(itemToScan))continue;

                bandolier = itemToScan;

                if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.INTRUSIVE.get(), itemToScan) > 0)
                {
                    priorityBandolier = itemToScan;
                }
            }
        }

        if(!priorityBandolier.isEmpty())bandolier = priorityBandolier;

        BundleItemCapability c = getActualCapability(bandolier);

        if(c != null)
        {
            c.deflate();

            //todo add smarts here with enchant
            var newItem =  c.stacksList.remove(0);
            var oldItemInHand = player.getMainHandItem().copy();

            player.setItemInHand(InteractionHand.MAIN_HAND, newItem.copy());
            player.getCooldowns().addCooldown(newItem.getItem(), 20);

            if(oldItemInHand != ItemStack.EMPTY)
            {
                //todo fix potions destroying new incoming potions
                ItemEntity itemEntity = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), oldItemInHand);
                itemEntity.setNoPickUpDelay();
                player.level.addFreshEntity(itemEntity);
            }
        }
    }
}