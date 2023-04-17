package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Items.BandolierItem;
import com.theishiopian.parrying.Registration.ModTags;
import com.theishiopian.parrying.Utility.Debug;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mixin(ItemStack.class)
public class ItemStackMixin
{
    @Inject(method = "shrink", at = @At("HEAD"))
    private void InjectIntoShrink(int pDecrement, CallbackInfo ci)
    {
        var oldStack = (ItemStack)(Object)this;

        if(oldStack.getCount() - pDecrement == 0 && oldStack.is(ModTags.BANDOLIER))
        {
            var server = ServerLifecycleHooks.getCurrentServer();
            if(server == null) return;

            for (ServerLevel level : server.getAllLevels())
            {
                var players = level.players();
                var hasFound = false;

                for (ServerPlayer player : players)
                {
                    for (ItemStack item : player.getInventory().items)
                    {
                        if(oldStack == item)
                        {
                            hasFound = true;
                            BandolierItem.itemsToGive.put(player.getUUID(), oldStack);
                        }
                    }
                }

                if(hasFound) break;
            }
        }
    }
}
