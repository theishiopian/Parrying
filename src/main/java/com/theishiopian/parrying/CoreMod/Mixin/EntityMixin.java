package com.theishiopian.parrying.CoreMod.Mixin;

import com.theishiopian.parrying.CoreMod.Hooks.EntityHooks;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin
{
    @Inject(method = "push(DDD)V", at = @At("HEAD"), cancellable = true)
    private void InjectIntoPush(double pX, double pY, double pZ, CallbackInfo ci)
    {
        if(EntityHooks.ModifyPush((Entity)(Object)this, pX, pY, pZ)) ci.cancel();
    }

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(DDD)V"), cancellable = true)
    private void InjectIntoCollisionPush(Entity entity, CallbackInfo ci)
    {
        if(EntityHooks.ModifyCollisionPush((Entity)(Object)this)) ci.cancel();
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void InjectIntoTickHead(CallbackInfo ci)
    {
        EntityHooks.PreTick((Entity)(Object)this);
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void InjectIntoTickTail(CallbackInfo ci)
    {
        EntityHooks.PostTick((Entity)(Object)this);
    }
}
