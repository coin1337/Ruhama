package bleach.a32k.module.modules;

import bleach.a32k.gui.*;
import bleach.a32k.module.*;
import java.util.*;
import bleach.a32k.settings.*;
import net.minecraft.client.gui.*;

public class ClickGui extends Module
{
    public static NewRuhamaGui clickGui;
    
    public ClickGui() {
        super("ClickGui", 0, Category.RENDER, "Clickgui", null);
    }
    
    @Override
    public void onEnable() {
        this.mc.displayGuiScreen(ClickGui.clickGui);
        this.setToggled(false);
    }
    
    static {
        ClickGui.clickGui = new NewRuhamaGui();
    }
}
