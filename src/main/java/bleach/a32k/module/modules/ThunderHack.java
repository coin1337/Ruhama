package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import net.minecraft.network.*;
import net.minecraft.util.math.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.text.*;
import bleach.a32k.utils.*;
import bleach.a32k.settings.*;
import java.util.*;

public class ThunderHack extends Module
{
    private static final List<SettingBase> settings;
    
    public ThunderHack() {
        super("ThunderHack", 0, Category.EXPLOITS, "Lightning exploit, probably patched idk", ThunderHack.settings);
    }
    
    @Override
    public boolean onPacketRead(final Packet<?> packet) {
        BlockPos newPos = null;
        if (packet instanceof SPacketEffect) {
            final SPacketEffect effect = (SPacketEffect)packet;
            newPos = effect.getSoundPos();
            if (this.mc.player.getPosition().getDistance(effect.getSoundPos().getX(), effect.getSoundPos().getY(), effect.getSoundPos().getZ()) > 500.0 + this.mc.player.posY) {
                newPos = effect.getSoundPos();
            }
        }
        else if (packet instanceof SPacketSoundEffect) {
            final SPacketSoundEffect sound = (SPacketSoundEffect)packet;
            if (this.mc.player.getPosition().getDistance((int)sound.getX(), (int)sound.getY(), (int)sound.getZ()) > 500.0 + this.mc.player.posY) {
                newPos = new BlockPos(sound.getX(), sound.getY(), sound.getZ());
            }
        }
        else if (packet instanceof SPacketSpawnGlobalEntity) {
            final SPacketSpawnGlobalEntity lightning = (SPacketSpawnGlobalEntity)packet;
            newPos = new BlockPos(lightning.getX(), lightning.getY(), lightning.getZ());
        }
        if (newPos != null) {
            RuhamaLogger.log("Thunder struck at: " + TextFormatting.ITALIC + newPos.getX() + TextFormatting.WHITE + ", " + TextFormatting.ITALIC + newPos.getY() + TextFormatting.WHITE + ", " + TextFormatting.ITALIC + newPos.getZ());
            if (this.getSettings().get(0).toToggle().state && this.mc.player.getPosition().getDistance(newPos.getX(), newPos.getY(), newPos.getZ()) > 100.0) {
                this.mc.player.sendChatMessage("> Thunder struck at: " + newPos.getX() + ", " + newPos.getY() + ", " + newPos.getZ());
            }
        }
        return false;
    }
    
    static {
        settings = Arrays.asList(new SettingToggle(false, "Public Chat"));
    }
}
