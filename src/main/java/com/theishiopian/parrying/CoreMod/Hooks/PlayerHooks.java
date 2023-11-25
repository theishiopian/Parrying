package com.theishiopian.parrying.CoreMod.Hooks;

import com.theishiopian.parrying.Config.Config;
import com.theishiopian.parrying.Items.ScopedCrossbow;
import com.theishiopian.parrying.Mechanics.DualWieldingMechanic;
import com.theishiopian.parrying.Registration.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public abstract class PlayerHooks
{
    public static Optional<Float> ModifyAttackStrength(Player player)
    {
        if(Config.dualWieldEnabled.get())
        {
            if(DualWieldingMechanic.IsDualWielding(player))
            {
                float mainSpeed = (float) player.getMainHandItem().
                        getAttributeModifiers(EquipmentSlot.MAINHAND).
                        get(Attributes.ATTACK_SPEED).stream().findFirst().get().getAmount();

                float offSpeed = (float) player.getOffhandItem().
                        getAttributeModifiers(EquipmentSlot.MAINHAND).
                        get(Attributes.ATTACK_SPEED).stream().findFirst().get().getAmount();

                float speedMod = (mainSpeed + offSpeed) / 2;//average speed
                float diff = Math.abs(mainSpeed - offSpeed);//penalty for difference in speeds

                return Optional.of((float) ((1.0D / (Attributes.ATTACK_SPEED.getDefaultValue() + speedMod + 0.2f - diff)) * 20.0D));
            }
        }

        return Optional.empty();
    }

    public static Optional<Boolean> ModifyScopingStatus(Player player)
    {
        boolean isUsingScopedWeapon = player.isUsingItem() && player.getUseItem().is(ModItems.SCOPED_CROSSBOW.get());//todo interface
        if(isUsingScopedWeapon)
        {
            boolean hasProjectile = ScopedCrossbow.isCharged(player.getUseItem());
            if(hasProjectile) return Optional.of(true);
        }

        return Optional.empty();
    }
}
