package com.theishiopian.parrying.CoreMod.Hooks;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class LivingEntityHooks
{
    public static Optional<Boolean> ModifyDeathProtectionCheck(DamageSource source, LivingEntity entity)
    {
        if(Config.undyingRework.get())
        {
            if(source.isBypassInvul()) return Optional.of(false);

            ItemStack totem = null;

            if(Config.undyingWorksFromInventory.get() && entity instanceof Player player)
            {
                for (ItemStack item : player.getInventory().items)
                {
                    if(item.is(Items.TOTEM_OF_UNDYING))
                    {
                        totem = item.copy();
                        item.shrink(1);
                        break;
                    }
                }
            }
            else
            {
                for(InteractionHand interactionhand : InteractionHand.values())
                {
                    ItemStack itemInHand = entity.getItemInHand(interactionhand);
                    if (itemInHand.is(Items.TOTEM_OF_UNDYING))
                    {
                        totem = itemInHand.copy();
                        itemInHand.shrink(1);
                        break;
                    }
                }
            }

            if (totem != null)
            {
                if (entity instanceof ServerPlayer serverplayer)
                {
                    serverplayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING), 1);
                    CriteriaTriggers.USED_TOTEM.trigger(serverplayer, totem);
                }

                entity.setHealth(2.0F);
                entity.removeAllEffects();
                //todo immortality
                entity.level.broadcastEntityEvent(entity, (byte)35);
            }

            return Optional.of(totem != null);
        }

        return Optional.empty();
    }
}