package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import bleach.a32k.settings.*;
import java.util.*;

public class Peek extends Module
{
    private static final List<SettingBase> settings;
    
    public Peek() {
        super("Peek", 0, Category.MISC, "Shows content of stuff", Peek.settings);
    }
    
    static {
        settings = Arrays.asList(new SettingToggle(true, "Map"), new SettingToggle(true, "Book"), new SettingToggle(true, "Shulker Cmd"));
    }
}
