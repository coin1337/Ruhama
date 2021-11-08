package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import net.minecraft.network.play.client.*;
import bleach.a32k.settings.*;
import java.util.*;

public class Aura extends Module
{
    private static final List<SettingBase> settings;
    private int delay;
    
    public Aura() {
        super("Aura", 0, Category.COMBAT, "Attacks Players", Aura.settings);
        this.delay = 0;
    }
    
    @Override
    public void onUpdate() {
        ++this.delay;
        final int reqDelay = (int)Math.round(20.0 / this.getSettings().get(5).toSlider().getValue());
        if (this.getSettings().get(0).toToggle().state && !(this.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) && !(this.mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe)) {
            return;
        }
        for (final EntityPlayer e : this.mc.world.playerEntities) {
            if (this.mc.player.getDistance(e) <= this.getSettings().get(4).toSlider().getValue() && e.getHealth() > 0.0f && e != this.mc.player && e != this.mc.player.getRidingEntity() && e != this.mc.getRenderViewEntity()) {
                if (!this.mc.player.canEntityBeSeen(e) && !this.getSettings().get(2).toToggle().state) {
                    continue;
                }
                if (((this.delay <= reqDelay && reqDelay != 0) || this.getSettings().get(1).toToggle().state) && (this.mc.player.getCooledAttackStrength(this.mc.getRenderPartialTicks()) < 1.0f || !this.getSettings().get(1).toToggle().state)) {
                    continue;
                }
                if (this.getSettings().get(3).toToggle().state) {
                    final Random rng = new Random();
                    final double n = 1.282622531E-314;
                    final double n2 = 1.282622531E-314 + 1.282622531E-314 * (1.0 + rng.nextInt(rng.nextBoolean() ? 34 : 43));
                    final double[] array2;
                    final double[] array = array2 = new double[] { 1.531232163E-314 + n2, 0.0, 1.135895857E-315 + n2, 0.0 };
                    for (final double d : array2) {
                        this.mc.player.connection.sendPacket(new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + d, this.mc.player.posZ, false));
                    }
                }
                this.mc.player.connection.sendPacket(new CPacketUseEntity(e, EnumHand.MAIN_HAND));
                this.mc.playerController.attackEntity(this.mc.player, e);
                this.mc.player.swingArm(EnumHand.MAIN_HAND);
                this.delay = 0;
            }
        }
    }
    
    static {
        settings = Arrays.asList(new SettingToggle(true, "WeaponFilter"), new SettingToggle(true, "1.9 Delay"), new SettingToggle(true, "Thru Walls"), new SettingToggle(true, "Crits"), new SettingSlider(0.0, 6.0, 4.5, 2, "Range: "), new SettingSlider(0.0, 20.0, 8.0, 0, "CPS: "));
    }
}
