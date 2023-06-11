package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Items.BandolierItem;
import com.theishiopian.parrying.Network.GameplayStatusPacket;
import com.theishiopian.parrying.Registration.ModTags;
import com.theishiopian.parrying.Utility.Debug;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
    @Shadow public abstract void setRepairCost(int pCost);

    @Inject(method = "shrink", at = @At("HEAD"))
    private void InjectIntoShrink(int pDecrement, CallbackInfo ci)
    {
        Debug.log("shrink injection");
        var oldStack = (ItemStack)(Object)this;
        Debug.log("old stack: " + oldStack);

        if(oldStack.getCount() - pDecrement == 0 && oldStack.is(ModTags.BANDOLIER))
        {
            Debug.log("bandolier check passed");
            var server = ServerLifecycleHooks.getCurrentServer();
            if(server == null) return;

            for (ServerLevel level : server.getAllLevels())
            {
                if(level.isClientSide) continue;
                var players = level.players();
                var hasFound = false;

                for (ServerPlayer player : players)
                {
                    if(!GameplayStatusPacket.isPlayerPlaying(player)) continue;

                    if(player.getMainHandItem() == oldStack)
                    {
                        Debug.log("found item: " + player.getMainHandItem() + " for player: " + player.getUUID());
                        hasFound = true;
                        BandolierItem.Add(player.getUUID(), oldStack.copy(), EquipmentSlot.MAINHAND);
                        break;
                    }
                    else if(player.getOffhandItem() == oldStack)
                    {
                        Debug.log("found item: " + player.getOffhandItem() + " for player: " + player.getUUID());
                        hasFound = true;
                        BandolierItem.Add(player.getUUID(), oldStack.copy(), EquipmentSlot.OFFHAND);
                        break;
                    }
                    else
                    {
                        //scan inventory
                        NonNullList<ItemStack> inventory = player.getInventory().items;

                        for (ItemStack itemStack : inventory)
                        {
                            if(itemStack == oldStack)
                            {
                                Debug.log("found item: " + itemStack + " for player: " + player.getUUID());
                                hasFound = true;
                                BandolierItem.Add(player.getUUID(), oldStack.copy(), null);
                                break;
                            }
                        }
                    }
                }

                if(hasFound) break;
            }
        }
    }
}
