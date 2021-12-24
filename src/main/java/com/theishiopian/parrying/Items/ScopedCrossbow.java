package com.theishiopian.parrying.Items;

import com.theishiopian.parrying.Utility.Debug;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ScopedCrossbow extends CrossbowItem
{
    public ScopedCrossbow(Properties properties)
    {
        super(properties);
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        ItemStack crossbow = player.getItemInHand(hand);
        if (!isCharged(crossbow))
        {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
            player.startUsingItem(hand);

            return InteractionResultHolder.consume(crossbow);
        }
        else if(!player.getProjectile(crossbow).isEmpty())
        {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(crossbow);
        }
        else
        {
            return InteractionResultHolder.fail(crossbow);
        }
    }

    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
    @Override
    public void releaseUsing(ItemStack crossbow, Level world, LivingEntity pEntityLiving, int pTimeLeft)
    {
        Debug.log("released");
        if(isCharged(crossbow))
        {
            Debug.log("charged");
            performShooting(world, pEntityLiving, InteractionHand.MAIN_HAND, crossbow, getShootingPower(crossbow), 1.0F);
            setCharged(crossbow, false);
        }
        else
        {
            super.releaseUsing(crossbow, world, pEntityLiving, pTimeLeft);
        }
    }
}