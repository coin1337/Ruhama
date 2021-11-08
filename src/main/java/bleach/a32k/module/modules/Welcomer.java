package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import java.awt.*;
import bleach.a32k.gui.*;
import bleach.a32k.settings.*;
import java.util.*;
import java.util.List;

public class Welcomer extends Module
{
    private static final List<SettingBase> settings;
    
    public Welcomer() {
        super("Welcomer", 0, Category.RENDER, "Welcomes you", Welcomer.settings);
        this.getWindows().add(new TextWindow(50, 12, "Welcomer"));
    }
    
    @Override
    public void onOverlay() {
        final boolean shadow = this.getSettings().get(3).toToggle().state;
        final int color = new Color((int)this.getSettings().get(0).toSlider().getValue(), (int)this.getSettings().get(1).toSlider().getValue(), (int)this.getSettings().get(2).toSlider().getValue()).getRGB();
        this.getWindows().get(0).clearText();
        this.getWindows().get(0).addText(new AdvancedText("Hello " + this.mc.player.getName() + " :^)", shadow, color));
    }
    
    static {
        settings = Arrays.asList(new SettingSlider(0.0, 255.0, 235.0, 0, "Text R: "), new SettingSlider(0.0, 255.0, 235.0, 0, "Text G: "), new SettingSlider(0.0, 255.0, 235.0, 0, "Text B: "), new SettingToggle(false, "Shadow"));
    }
}
