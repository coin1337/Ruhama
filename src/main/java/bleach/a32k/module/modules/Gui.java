package bleach.a32k.module.modules;

import java.awt.*;
import bleach.a32k.gui.*;
import net.minecraft.util.math.*;
import bleach.a32k.module.*;
import bleach.a32k.settings.*;
import java.util.*;
import java.util.List;

public class Gui extends Module
{
    public static int arrayListEnd;
    private static final List<SettingBase> settings;
    
    public Gui() {
        super("Gui", 0, Category.RENDER, "The Ingame ruhama gui", Gui.settings);
        this.getWindows().add(new TextWindow(2, 150, "Arraylist"));
    }
    
    @Override
    public void onOverlay() {
        this.getWindows().get(0).clearText();
        int color = new Color((int)this.getSettings().get(0).toSlider().getValue(), (int)this.getSettings().get(1).toSlider().getValue(), (int)this.getSettings().get(2).toSlider().getValue()).getRGB();
        final String s = "Ruhama Client 0.8";
        this.getWindows().get(0).addText(new AdvancedText(s, true, color));
        if (this.getSettings().get(3).toToggle().state) {
            final int age = (int)(System.currentTimeMillis() / 20L % 510L);
            color = new Color(255, MathHelper.clamp((age > 255) ? (510 - age) : age, 0, 255), MathHelper.clamp(255 - ((age > 255) ? (510 - age) : age), 0, 255)).getRGB();
        }
        else {
            color = new Color((int)this.getSettings().get(4).toSlider().getValue(), (int)this.getSettings().get(5).toSlider().getValue(), (int)this.getSettings().get(6).toSlider().getValue()).getRGB();
        }
        final List<Module> arrayList = ModuleManager.getModules();
        arrayList.remove(this);
        arrayList.sort((a, b) -> Integer.compare(this.mc.fontRenderer.getStringWidth(b.getName()), this.mc.fontRenderer.getStringWidth(a.getName())));
        for (final Module m : arrayList) {
            if (!m.isToggled()) {
                continue;
            }
            this.getWindows().get(0).addText(new AdvancedText(m.getName(), true, color));
        }
    }
    
    static {
        Gui.arrayListEnd = 160;
        settings = Arrays.asList(new SettingSlider(0.0, 255.0, 235.0, 0, "Ruhama R: "), new SettingSlider(0.0, 255.0, 235.0, 0, "Ruhama G: "), new SettingSlider(0.0, 255.0, 235.0, 0, "Ruhama B: "), new SettingToggle(true, "RainbowList"), new SettingSlider(0.0, 255.0, 235.0, 0, "List R: "), new SettingSlider(0.0, 255.0, 235.0, 0, "List G: "), new SettingSlider(0.0, 255.0, 235.0, 0, "List B: "));
    }
}
