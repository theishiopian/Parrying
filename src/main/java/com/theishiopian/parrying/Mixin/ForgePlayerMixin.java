package com.theishiopian.parrying.Mixin;

import com.theishiopian.parrying.Items.SpearItem;
import com.theishiopian.parrying.Registration.ModEnchantments;
import com.theishiopian.parrying.Utility.Debug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.extensions.IForgePlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Player.class)
public class ForgePlayerMixin implements IForgePlayer
{
    @Override
    public boolean canHit(Entity entity, double padding)
    {
        Player player = ((Player) (Object) this);
        ItemStack weapon = player.getMainHandItem();
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.JOUSTING.get(), weapon);
        double range = getAttackRange();

        if(weapon.getItem() instanceof SpearItem && level > 0)
        {
            double total = range + level + padding;
            boolean close = isCloseEnough(entity, total);
            Debug.log(close);
            return close;
        }

        return isCloseEnough(entity, getAttackRange() + padding);//default forge method, can't reference super because of a bug
    }
}
