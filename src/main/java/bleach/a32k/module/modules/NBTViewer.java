package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import net.minecraft.util.text.*;
import net.minecraft.entity.*;
import bleach.a32k.settings.*;
import java.util.*;

public class NBTViewer extends Module
{
    private static final List<SettingBase> settings;
    
    public NBTViewer() {
        super("NBTViewer", 0, Category.MISC, "Shows nbt when hovering over a entity", NBTViewer.settings);
    }
    
    @Override
    public void onOverlay() {
        final Entity e = this.mc.objectMouseOver.entityHit;
        if (e == null) {
            return;
        }
        final String[] text = e.serializeNBT().toString().split("(?=((\\{)|(?<=\\G.{100})))");
        int count = 30;
        boolean color1 = true;
        for (final String s : text) {
            String s2 = "";
            for (final Character c : s.toCharArray()) {
                if (c.toString().contains("{")) {
                    color1 = !color1;
                }
                s2 = s2 + (color1 ? TextFormatting.LIGHT_PURPLE.toString() : TextFormatting.DARK_PURPLE.toString()) + c;
            }
            this.mc.fontRenderer.drawStringWithShadow(s2, 40.0f, (float)count, -1);
            count += 10;
        }
    }
    
    static {
        settings = Arrays.asList(new SettingToggle(false, "Mobs Only"));
    }
}
