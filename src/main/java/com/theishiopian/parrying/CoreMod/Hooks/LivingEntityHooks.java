package com.theishiopian.parrying.CoreMod.Hooks;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Mechanics.DeltaPositionMechanic;
import com.theishiopian.parrying.Registration.ModEffects;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class LivingEntityHooks
{
    public static Optional<Boolean> ModifyDeathProtectionCheck(DamageSource source, LivingEntity entity)
    {
        if(Config.undyingRework.get())
        {
            if(source.isBypassInvul()) return Optional.of(false);

            var totem = findTotem(entity);

            if (totem != null)
            {
                if (entity instanceof ServerPlayer serverplayer)
                {
                    serverplayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING), 1);
                    CriteriaTriggers.USED_TOTEM.trigger(serverplayer, totem);
                }

                entity.setHealth(2.0F);
                entity.removeAllEffects();
                entity.addEffect(new MobEffectInstance(ModEffects.IMMORTALITY.get(), 600));
                entity.level.broadcastEntityEvent(entity, (byte)35);
            }

            return Optional.of(totem != null);
        }

        return Optional.empty();
    }

    @Nullable
    private static ItemStack findTotem(LivingEntity entity)
    {
        ItemStack totem = null;

        if(Config.undyingWorksFromInventory.get() && entity instanceof Player player)
        {
            ItemStack itemToScan;
            for(int i = 46; i >= 0; i--)
            {
                //check offhand first
                itemToScan = i == 46 ? player.getOffhandItem() : player.getInventory().getItem(i);

                if(itemToScan.is(Items.TOTEM_OF_UNDYING))
                {
                    totem = itemToScan.copy();
                    itemToScan.shrink(1);
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

        return totem;
    }

    public static void PreTick(LivingEntity entity)
    {
        if(entity.level.isClientSide)return;

        var tracker = DeltaPositionMechanic.velocityTracker.get(entity.getUUID());

        if(tracker != null)
        {
            if(tracker.oldPos != null)
            {
                tracker.oldDeltaPosition = tracker.deltaPosition;
                tracker.deltaPosition = entity.position().subtract(tracker.oldPos).length();
            }
        }
    }

    public static void PostTick(LivingEntity entity)
    {
        if(entity.level.isClientSide)return;

        var tracker = DeltaPositionMechanic.velocityTracker.get(entity.getUUID());

        if(tracker != null)
        {
            var oldSpeed = tracker.oldDeltaPosition * 20;
            if(entity.hasEffect(ModEffects.INSTABILITY.get()) && entity.horizontalCollision)
            {
                entity.hurt(DamageSource.FLY_INTO_WALL, (float) oldSpeed);
            }

            tracker.oldPos = entity.position();
        }
    }
}