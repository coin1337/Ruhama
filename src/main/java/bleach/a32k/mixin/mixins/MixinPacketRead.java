package bleach.a32k.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import io.netty.channel.*;
import net.minecraft.network.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import bleach.a32k.module.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ NetworkManager.class })
public class MixinPacketRead
{
    @Inject(method = { "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V" }, at = { @At("HEAD") }, cancellable = true)
    public void channelRead0(final ChannelHandlerContext p_channelRead0_1_, final Packet<?> p_channelRead0_2_, final CallbackInfo info) {
        if (ModuleManager.onPacketRead(p_channelRead0_2_)) {
            info.cancel();
        }
    }
}
