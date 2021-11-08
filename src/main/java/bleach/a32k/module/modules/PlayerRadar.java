package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.math.*;
import java.math.*;
import net.minecraft.entity.*;
import bleach.a32k.gui.*;
import net.minecraft.util.text.*;
import bleach.a32k.settings.*;
import java.util.*;

public class PlayerRadar extends Module
{
    private static final List<SettingBase> settings;
    
    public PlayerRadar() {
        super("PlayerRadar", 0, Category.RENDER, "Shows nearby people", PlayerRadar.settings);
        this.getWindows().add(new TextWindow(100, 150, "PlayerRadar"));
    }
    
    @Override
    public void onOverlay() {
        int c = Gui.arrayListEnd + 10;
        this.getWindows().get(0).clearText();
        for (final EntityPlayer e : this.mc.world.playerEntities) {
            if (e == this.mc.player) {
                continue;
            }
            int color = 0;
            try {
                color = ((e.getHealth() + e.getAbsorptionAmount() > 20.0f) ? 2158832 : MathHelper.hsvToRGB((e.getHealth() + e.getAbsorptionAmount()) / 20.0f / 3.0f, 1.0f, 1.0f));
            }
            catch (Exception ex) {}
            final double health = new BigDecimal(e.getHealth() + e.getAbsorptionAmount()).setScale(1, RoundingMode.HALF_UP).doubleValue();
            final double dist = new BigDecimal(e.getDistance(this.mc.player)).setScale(1, RoundingMode.HALF_UP).doubleValue();
            final boolean round = this.getSettings().get(0).toToggle().state;
            final boolean dead = e.getHealth() <= 0.0f;
            if (round) {
                if (dead) {
                    this.getWindows().get(0).addText(new AdvancedText((int)health + " " + e.getName() + " " + (int)dist + "m", true, color));
                }
                else {
                    this.getWindows().get(0).addText(new AdvancedText((int)health + " " + ((this.mc.objectMouseOver.entityHit == e) ? TextFormatting.GOLD.toString() : TextFormatting.GRAY.toString()) + e.getName() + " " + TextFormatting.DARK_GRAY.toString() + (int)dist + "m", true, color));
                }
            }
            else if (dead) {
                this.getWindows().get(0).addText(new AdvancedText(health + " " + e.getName() + " " + dist + "m", true, color));
            }
            else {
                this.getWindows().get(0).addText(new AdvancedText(health + " " + ((this.mc.objectMouseOver.entityHit == e) ? TextFormatting.GOLD.toString() : TextFormatting.GRAY.toString()) + e.getName() + " " + TextFormatting.DARK_GRAY.toString() + dist + "m", true, color));
            }
            c += 10;
        }
        Gui.arrayListEnd = c;
    }
    
    static {
        settings = Arrays.asList(new SettingToggle(false, "Round"));
    }
}
