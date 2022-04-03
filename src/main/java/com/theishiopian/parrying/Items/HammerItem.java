package com.theishiopian.parrying.Items;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HammerItem extends BludgeonItem
{
    public HammerItem(Tier itemTier, int baseDamage, float baseSpeed, float baseAP, Properties properties)
    {
        super(itemTier, baseDamage, baseSpeed, baseAP, properties);
    }

    public float getDestroySpeed(ItemStack pStack, BlockState pState)
    {
        if (pState.is(BlockTags.MINEABLE_WITH_PICKAXE) && !(pState.getBlock() instanceof OreBlock || pState.getBlock() instanceof RedStoneOreBlock))
        {
            return 100.0F;
        }
        else
        {
            return 1;
        }
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state)
    {
        return false;
    }
}
