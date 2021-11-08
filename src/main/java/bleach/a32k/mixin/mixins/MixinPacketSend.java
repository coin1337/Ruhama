package bleach.a32k.mixin.mixins;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.network.*;
import net.minecraft.network.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import bleach.a32k.module.*;
import net.minecraft.network.play.client.*;
import net.minecraft.client.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.block.*;
import bleach.a32k.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ NetHandlerPlayClient.class })
public class MixinPacketSend
{
    @Inject(method = { "sendPacket(Lnet/minecraft/network/Packet;)V" }, at = { @At("HEAD") }, cancellable = true)
    public void sendPacket(final Packet<?> packetIn, final CallbackInfo info) {
        if (ModuleManager.onPacketSend(packetIn)) {
            info.cancel();
        }
        if (packetIn instanceof CPacketPlayerTryUseItemOnBlock) {
            final CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)packetIn;
            if (Minecraft.getMinecraft().player.getHeldItem(packet.getHand()).getItem() instanceof ItemShulkerBox || Minecraft.getMinecraft().player.getHeldItem(packet.getHand()).getItem() == Item.getItemFromBlock(Blocks.HOPPER)) {
                final BlockPos pos = packet.getPos().offset(packet.getDirection());
                System.out.println("Rightclicked at: " + System.currentTimeMillis());
                Ruhama.friendBlocks.put(pos, 300);
            }
        }
    }
}
