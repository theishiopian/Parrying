package com.theishiopian.parrying.Items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class FirecrackerItem extends Item
{
    public FirecrackerItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public @NotNull ActionResult<ItemStack> use(@NotNull World world, PlayerEntity player, @NotNull Hand hand)
    {
        //throw firecracker
        //TODO finish entity, add renderer

        return ActionResult.pass(player.getItemInHand(hand));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World world, @NotNull List<ITextComponent> components, @NotNull ITooltipFlag flag)
    {
        //copy code from net.minecraft.item.FireworkRocketItem.java
    }
}
