package bleach.a32k.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import io.netty.channel.*;
import java.util.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import bleach.a32k.settings.*;
import net.minecraft.network.*;
import io.netty.buffer.*;
import bleach.a32k.module.*;
import java.util.zip.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ NettyCompressionDecoder.class })
public class MixinNettyDecoder
{
    @Shadow
    private Inflater inflater;
    @Shadow
    private int threshold;
    
    @Inject(method = { "decode(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Ljava/util/List;)V" }, at = { @At("HEAD") }, cancellable = true)
    protected void decode(final ChannelHandlerContext p_decode_1_, final ByteBuf p_decode_2_, final List<Object> p_decode_3_, final CallbackInfo info) throws Exception {
        final Module m = ModuleManager.getModuleByName("AntiChunkBan");
        if (!m.isToggled() || m.getSettings().get(0).toMode().mode != 0) {
            return;
        }
        info.cancel();
        if (p_decode_2_.readableBytes() != 0) {
            final PacketBuffer packetbuffer = new PacketBuffer(p_decode_2_);
            final int i = packetbuffer.readVarInt();
            if (i == 0) {
                p_decode_3_.add(packetbuffer.readBytes(packetbuffer.readableBytes()));
            }
            else {
                final byte[] abyte = new byte[packetbuffer.readableBytes()];
                packetbuffer.readBytes(abyte);
                this.inflater.setInput(abyte);
                final byte[] abyte2 = new byte[i];
                this.inflater.inflate(abyte2);
                p_decode_3_.add(Unpooled.wrappedBuffer(abyte2));
                this.inflater.reset();
            }
        }
    }
}
