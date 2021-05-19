package com.theishiopian.parrying;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BashPacket
{
    public void toBytes(PacketBuffer buffer)
    {

    }

    public static BashPacket fromBytes(PacketBuffer buffer)
    {
        return new BashPacket();
    }

    public static void handle(BashPacket packet, Supplier<NetworkEvent.Context> context)
    {
        ServerPlayerEntity player = context.get().getSender();

        if(player != null)
        {
            //ParryingMod.LOGGER.info();
            Entity target = RayTracer.getFistEntityInLine(player.level, player.getEyePosition(1), player.getEyePosition(1).add(player.getViewVector(1).scale(1)), player);
            ParryingMod.LOGGER.info(player.isBlocking());
           if(target != null && player.isBlocking())
           {
                ParryingMod.LOGGER.info("Bashing");
                ItemStack main = player.getMainHandItem();
                ItemStack off = player.getOffhandItem();
                ItemStack shield = null;
                Hand hand = Hand.OFF_HAND;
                if(main.getItem() instanceof ShieldItem)
                {
                    shield = main;
                    hand = Hand.MAIN_HAND;
                }
                if(off.getItem() instanceof ShieldItem)
                {
                    shield = off;
                    hand = Hand.OFF_HAND;
                }

                final Hand lHand = hand;

                if(shield != null)
                {
                    DamageSource source = new DamageSource("generic");

                    player.level.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundCategory.PLAYERS, 1,1);
                    player.swing(hand);
                    player.getCooldowns().addCooldown(shield.getItem(), 100);
                    player.stopUsingItem();
                    player.causeFoodExhaustion(0.5f);

                    shield.hurtAndBreak(1, player, (playerEntity) ->
                    {
                        playerEntity.broadcastBreakEvent(lHand);
                    });

                    target.hurt(source, 1);
                    ((LivingEntity)target).knockback(0.5f, -player.getViewVector(1).x, -player.getViewVector(1).z);
                    target.hurtMarked = true;
                }
           }
        }

        context.get().setPacketHandled(true);
    }
}
