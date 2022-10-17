package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Network.QuiverAdvPacket;
import com.theishiopian.parrying.ParryingMod;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.HashSet;

public class QuiverItem extends AbstractBundleItem
{
    public QuiverItem(Properties pProperties)
    {
        super(pProperties, 256, ItemTags.ARROWS, new TranslatableComponent("filter.parrying.arrows"));

        POST_ADD = (c) ->
        {
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
        };
    }
}
