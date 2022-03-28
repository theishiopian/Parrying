package com.theishiopian.parrying.Mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * This mixin increases the maximum possible interaction range from 6 to 16.
 * For some reason, this is hardcoded in vanilla. Rather than recreate the entire logic, I decided to simply extend the range, for simplicity's sake.
 * While I can't find any reason this would cause problems, it's not impossible, so keep an eye on this thing.
 * In case it turns out necessary to rewrite the whole thing, take a look at create's extendo-grip.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerListenerMixin
{
    @ModifyConstant(method = "handleInteract", constant = @Constant(doubleValue = 36.0D))
    private double ModifyRange(double constant)
    {
        return 256.0D;
    }
}
