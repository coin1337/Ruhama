package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.util.text.*;
import bleach.a32k.settings.*;
import java.util.*;

public class AutoLog extends Module
{
    private static final List<SettingBase> settings;
    
    public AutoLog() {
        super("AutoLog", 0, Category.COMBAT, "Automatically Logs out when ___", AutoLog.settings);
    }
    
    @Override
    public void onUpdate() {
        if (this.mc.player.capabilities.isCreativeMode || this.mc.isIntegratedServerRunning()) {
            return;
        }
        if (this.getSettings().get(0).toToggle().state && this.mc.player.getHealth() < this.getSettings().get(1).toSlider().getValue()) {
            this.logOut("Logged Out At " + this.mc.player.getHealth() + " Health");
            return;
        }
        if (this.getSettings().get(2).toToggle().state) {
            final int t = this.getTotems();
            if (t <= (int)this.getSettings().get(3).toSlider().getValue()) {
                this.logOut("Logged Out With " + t + " Totems Left");
                return;
            }
        }
        if (this.getSettings().get(4).toMode().mode != 0) {
            for (final Entity e : this.mc.world.loadedEntityList) {
                if (e instanceof EntityEnderCrystal) {
                    final double d = this.mc.player.getDistance(e);
                    if (d <= this.getSettings().get(5).toSlider().getValue() && (this.getSettings().get(4).toMode().mode == 1 || (this.getSettings().get(4).toMode().mode == 2 && this.getTotems() <= (int)this.getSettings().get(3).toSlider().getValue()) || (this.getSettings().get(4).toMode().mode == 3 && this.mc.player.getHealth() < this.getSettings().get(1).toSlider().getValue()))) {
                        this.logOut("Logged Out " + d + " Blocks Away From A Crystal");
                        return;
                    }
                    continue;
                }
            }
        }
        if (this.getSettings().get(6).toToggle().state) {
            for (final EntityPlayer e2 : this.mc.world.playerEntities) {
                if (e2.getName() != this.mc.player.getName() && this.mc.player.getDistance(e2) <= this.getSettings().get(7).toSlider().getValue()) {
                    this.logOut("Logged Out " + this.mc.player.getDistance(e2) + " Blocks Away From A Player (" + e2.getName() + ")");
                }
            }
        }
    }
    
    private int getTotems() {
        int c = 0;
        for (int i = 0; i < 45; ++i) {
            if (this.mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                ++c;
            }
        }
        return c;
    }
    
    private void logOut(final String reason) {
        this.mc.player.connection.getNetworkManager().closeChannel(new TextComponentString(reason));
        this.setToggled(false);
    }
    
    static {
        settings = Arrays.asList(new SettingToggle(false, "Health"), new SettingSlider(0.0, 20.0, 5.0, 0, "Health: "), new SettingToggle(true, "Totems"), new SettingSlider(0.0, 6.0, 0.0, 0, "Totems: "), new SettingMode("Crystal: ", "None", "Near", "Near+No Totem", "Near+Health"), new SettingSlider(0.0, 8.0, 4.0, 2, "CrystalRange: "), new SettingToggle(false, "Nearby Player"), new SettingSlider(0.0, 100.0, 20.0, 1, "Range: "));
    }
}
