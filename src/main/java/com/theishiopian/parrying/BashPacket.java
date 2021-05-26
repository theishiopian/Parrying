package com.theishiopian.parrying;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.*;
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

        if(player != null && player.isBlocking())
        {
            List<LivingEntity> list = player.level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(player.position().x + 3, player.position().y + 3, player.position().z + 3,player.position().x - 3, player.position().y - 3, player.position().z - 3));

            list.remove(player);
            Random random = new Random();
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();
            ItemStack shield = null;
            Hand hand = Hand.OFF_HAND;
            if(main.getItem() instanceof ShieldItem)
            {
                shield = main;
                hand = Hand.MAIN_HAND;
            }
            else if(off.getItem() instanceof ShieldItem)
            {
                shield = off;
                hand = Hand.OFF_HAND;
            }

            Comparator<LivingEntity> distCompare = (o1, o2) ->
            {
                double distA = o1.position().distanceTo(player.position());
                double distB = o2.position().distanceTo(player.position());

                return Double.compare(distA, distB);
            };

            if(list.size() > 0 && shield != null)
            {
                list.sort(distCompare);
                int bashes = 0;
                int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.BASHING.get(), shield);
                for (int i = 0; i < list.size(); i++)
                {
                    LivingEntity target = list.get(i);
                    Vector3d dir = (target.position().subtract(player.position())).normalize();
                    double dot = dir.dot(player.getViewVector(1));
                    if(dot > 0.75 && player.position().distanceTo(target.position()) <= 3 && !target.isBlocking())
                    {
                        BashEntity(target, player, shield, hand);
                        bashes++;
                    }

                    if(bashes >= 3 + level)break;
                }

                player.level.playSound(null, player.blockPosition(), ModSoundEvents.SHIELD_BASH.get(), SoundCategory.PLAYERS, 1, random.nextFloat() * 0.5f + 0.5f);
                player.stopUsingItem();
                player.swing(hand);
                player.getCooldowns().addCooldown(shield.getItem(), 80 + 20 * bashes);
            }
            else
            {
                player.stopUsingItem();
                player.swing(hand);
                player.getCooldowns().addCooldown(shield.getItem(), 20);
            }
        }

        context.get().setPacketHandled(true);
    }

    private static void BashEntity(LivingEntity target, PlayerEntity player, ItemStack shield, Hand hand)
    {
        ParryingMod.LOGGER.info("Bashing: " + target);
        DamageSource source = new DamageSource("generic");

        player.causeFoodExhaustion(0.5f);


        shield.hurtAndBreak(1, player, (playerEntity) ->
        {
            playerEntity.broadcastBreakEvent(hand);
        });

        EffectInstance instance = new EffectInstance(ModEffects.STUNNED.get(), 60);
        target.hurt(source, 2);
        (target).addEffect(instance);
        (target).knockback(0.5f, -player.getViewVector(1).x, -player.getViewVector(1).z);
        target.hurtMarked = true;
    }
}
