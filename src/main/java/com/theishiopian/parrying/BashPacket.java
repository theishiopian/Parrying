package com.theishiopian.parrying;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Random;
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

                    player.swing(hand);
                    player.getCooldowns().addCooldown(shield.getItem(), 120);
                    player.stopUsingItem();
                    player.causeFoodExhaustion(0.5f);
                    Random random = new Random();
                    player.level.playSound(null, player.blockPosition(), ModSoundEvents.SHIELD_BASH.get(), SoundCategory.PLAYERS, 1, random.nextFloat() * 0.5f + 0.5f);

                    shield.hurtAndBreak(1, player, (playerEntity) ->
                    {
                        playerEntity.broadcastBreakEvent(lHand);
                    });

                    EffectInstance instance = new EffectInstance(ModEffects.STUNNED.get(), 60);
                    target.hurt(source, 2);
                    ((LivingEntity)target).addEffect(instance);
                    ((LivingEntity)target).knockback(0.5f, -player.getViewVector(1).x, -player.getViewVector(1).z);
                    target.hurtMarked = true;
                }
           }
        }

        context.get().setPacketHandled(true);
    }
}
