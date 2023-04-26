package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Network.QuiverAdvPacket;
import com.theishiopian.parrying.ParryingMod;
import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Registration.ModItems;
import com.theishiopian.parrying.Registration.ModTriggers;
import com.theishiopian.parrying.Utility.ModUtil;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import javax.annotation.Nullable;
import java.util.HashSet;

public class QuiverItem extends AbstractBundleItem
{
    public QuiverItem(Properties pProperties)
    {
        super(pProperties, 256, ItemTags.ARROWS, new TranslatableComponent("tooltip.parrying.arrows"));

        POST_ADD = (c) ->
        {
            if(c.isFull())
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
        };
    }

    public static @Nullable ItemStack ScanForArrows(Player player)
    {
        if(player.getOffhandItem().is(ItemTags.ARROWS)) return null;
        ItemStack itemToScan;
        ItemStack quiver = ItemStack.EMPTY;
        ItemStack priorityQuiver = ItemStack.EMPTY;
        for(int i = 45; i >= 0; i--)
        {
            itemToScan = player.getInventory().getItem(i);

            if(itemToScan.is(ModItems.QUIVER.get()) )
            {
                if(AbstractBundleItem.isEmpty(itemToScan))continue;

                quiver = itemToScan;

                if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.INTRUSIVE.get(), itemToScan) > 0)
                {
                    priorityQuiver = itemToScan;
                }
            }
        }

        if(!priorityQuiver.isEmpty())quiver = priorityQuiver;

        if(!quiver.isEmpty())
        {
            ItemStack peek = AbstractBundleItem.peekFirstStack(quiver);

            int pLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.PROVIDENCE.get(), quiver);
            float chance = 1f - (pLevel * (1/64f));

            boolean doProvide = !player.level.isClientSide && ModUtil.random.nextFloat() > chance;

            if(doProvide)
            {
                ModTriggers.provide.trigger((ServerPlayer) player);
                //magic sound go brr
                player.level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1, ModUtil.random.nextFloat() * 2f);
            }

            return doProvide ? peek.copy() : peek;
            //event.setProjectileItemStack(doProvide ? peek.copy() : peek);
        }

        return null;
    }
}
