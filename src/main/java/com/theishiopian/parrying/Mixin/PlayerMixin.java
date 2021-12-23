package com.theishiopian.parrying.Mixin;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Items.AdvancedBundle;
import com.theishiopian.parrying.Mechanics.DualWielding;
import com.theishiopian.parrying.Registration.ModItems;
import com.theishiopian.parrying.Utility.Debug;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin
{
    @Inject(at = @At("HEAD"), method = "getCurrentItemAttackStrengthDelay", cancellable = true)
    private void InjectIntoGetCurrentItemAttackStrengthDelay(CallbackInfoReturnable<Float> cir)
    {
        if(Config.dualWieldEnabled.get())
        {
            Player player = ((Player)(Object)this);

            if(DualWielding.IsDualWielding(player))
            {
                float mainSpeed = (float) player.getMainHandItem().
                        getAttributeModifiers(EquipmentSlot.MAINHAND).
                        get(Attributes.ATTACK_SPEED).stream().findFirst().get().getAmount();

                float offSpeed = (float) player.getOffhandItem().
                        getAttributeModifiers(EquipmentSlot.MAINHAND).
                        get(Attributes.ATTACK_SPEED).stream().findFirst().get().getAmount();

                float speedMod = (mainSpeed + offSpeed) / 2;//average speed
                float diff = Math.abs(mainSpeed - offSpeed);//penalty for difference in speeds

                cir.setReturnValue((float)((1.0D / (Attributes.ATTACK_SPEED.getDefaultValue() + speedMod + 0.2f - diff)) * 20.0D));
            }
        }
    }

    //super dumb, but the way minecraft's projectile weapons work has forced me to take excessive measures
    //GetProjectile is used both to check if there IS an arrow AND to take one
    //the "better" solution would be to rewrite bundles to use item stacks directly, but frankly that's more work than its worth
    private static boolean IsUsingQuiver = false;

    @Inject(at = @At("HEAD"), method = "getProjectile", cancellable = true)
    private void InjectIntoGetProjectile(ItemStack weapon, CallbackInfoReturnable<ItemStack> cir)
    {
        if(Config.quiverMixinEnabled.get() && !((Player) (Object) this).level.isClientSide())
        {
            if(weapon.getItem() instanceof BowItem || weapon.getItem() instanceof CrossbowItem)
            {
                for(int i = 0; i < ((Player) (Object) this).getInventory().getContainerSize(); ++i)
                {
                    ItemStack potentialQuiver = ((Player) (Object) this).getInventory().getItem(i);

                    if(potentialQuiver.getItem().equals(ModItems.QUIVER.get()))
                    {
                        if(AdvancedBundle.getStackCount(potentialQuiver) > 0)
                        {
                            ItemStack taken = AdvancedBundle.TakeFirstItem(potentialQuiver);

                            if(!IsUsingQuiver)
                            {
                                IsUsingQuiver = true;
                                AdvancedBundle.add(potentialQuiver, taken);
                            }
                            else
                            {
                                if(weapon.getItem() instanceof BowItem)
                                {
                                    int charge = weapon.getUseDuration() - ((Player) (Object) this).getUseItemRemainingTicks();

                                    if(charge < 3)
                                    {
                                        AdvancedBundle.add(potentialQuiver, taken);
                                    }

                                    Debug.log(charge);
                                }
                                IsUsingQuiver = false;
                            }

                            cir.setReturnValue(AdvancedBundle.GetItems(potentialQuiver).get(0));
                        }
                    }
                }
            }
        }
    }
}