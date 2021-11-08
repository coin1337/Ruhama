package bleach.a32k.gui;

import net.minecraft.client.gui.*;
import bleach.a32k.module.*;
import java.util.*;

public abstract class ModuleWindow
{
    public FontRenderer font;
    public List<Module> modList;
    public LinkedHashMap<Module, Boolean> mods;
    public String name;
    public int len;
    public int posX;
    public int posY;
    public int mouseX;
    public int mouseY;
    public int prevmX;
    public int prevmY;
    public int keyDown;
    public boolean lmDown;
    public boolean rmDown;
    public boolean lmHeld;
    public boolean dragging;
    
    public ModuleWindow(final List<Module> mods, final String name, final int len, final int posX, final int posY) {
        this.modList = new ArrayList<Module>();
        this.mods = new LinkedHashMap<Module, Boolean>();
        this.modList = mods;
        for (final Module m : mods) {
            this.mods.put(m, false);
        }
        this.name = name;
        this.len = len;
        this.posX = posX;
        this.posY = posY;
    }
    
    public abstract void draw(final int p0, final int p1, final int p2);
    
    public void setPos(final int x, final int y) {
        this.posX = x;
        this.posY = y;
    }
    
    public int[] getPos() {
        return new int[] { this.posX, this.posY };
    }
    
    public void onLmPressed() {
        this.lmDown = true;
        this.lmHeld = true;
    }
    
    public void onLmReleased() {
        this.lmHeld = false;
    }
    
    public void onRmPressed() {
        this.rmDown = true;
    }
    
    public void onKeyPressed(final int key) {
        this.keyDown = key;
    }
    
    protected boolean mouseOver(final int minX, final int minY, final int maxX, final int maxY) {
        return this.mouseX > minX && this.mouseX < maxX && this.mouseY > minY && this.mouseY < maxY;
    }
    
    protected String cutText(final String text, final int leng) {
        String text2 = text;
        for (int i = 0; i < text.length(); ++i) {
            if (this.font.getStringWidth(text2) < this.len - 2) {
                return text2;
            }
            text2 = text2.replaceAll(".$", "");
        }
        return "";
    }
}
