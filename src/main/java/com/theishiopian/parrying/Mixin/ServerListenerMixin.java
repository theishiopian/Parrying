package com.theishiopian.parrying.Mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerListenerMixin
{
    @ModifyConstant(method = "handleInteract", constant = @Constant(doubleValue = 36.0D))
    private double ModifyRange(double constant)
    {
        return 256.0D;
    }
}
