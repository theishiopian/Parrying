package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.Config.Config;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(net.minecraft.world.entity.projectile.ThrownPotion.class)
public class ThrownPotionEntityMixin
{
    @Redirect(method = "applySplash", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private List<LivingEntity> RedirectAABB(Level instance, Class aClass, AABB aabb)
    {
        //Debug.log("redirecting");
        List<LivingEntity> targets = instance.getEntitiesOfClass(LivingEntity.class, aabb);
        Entity owner = ((ThrownPotion)(Object)this).getOwner();

        if(Config.noSelfSplash.get() && owner instanceof LivingEntity)
        {
            targets.remove(owner);
        }

        return targets;
    }

    @ModifyConstant(method = "makeAreaOfEffectCloud", constant = @Constant(floatValue = 3.0f))
    private float ModifyLingerCloudRadius(float constant)
    {
        return Config.lingeringRadius.get().floatValue();
    }
}
