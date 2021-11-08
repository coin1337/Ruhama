package bleach.a32k.gui;

import net.minecraft.client.gui.*;
import org.apache.commons.lang3.tuple.*;
import bleach.a32k.module.*;
import java.util.*;
import java.io.*;

public class NewRuhamaGui extends GuiScreen
{
    public List<ModuleWindow> tabs;
    public static List<MutableTriple<Module, Integer, TextWindow>> textWins;
    
    public NewRuhamaGui() {
        this.tabs = new ArrayList<ModuleWindow>();
    }
    
    public void initWindows() {
        this.tabs.add(new ModuleWindowDark(ModuleManager.getModulesInCat(Category.COMBAT), "Combat", 70, 30, 35));
        this.tabs.add(new ModuleWindowDark(ModuleManager.getModulesInCat(Category.RENDER), "Render", 70, 105, 35));
        this.tabs.add(new ModuleWindowDark(ModuleManager.getModulesInCat(Category.MISC), "Misc", 70, 180, 35));
        this.tabs.add(new ModuleWindowDark(ModuleManager.getModulesInCat(Category.EXPLOITS), "Exploits", 70, 255, 35));
        for (final Module m : ModuleManager.getModules()) {
            int i = 0;
            for (final TextWindow t : m.getWindows()) {
                NewRuhamaGui.textWins.add((MutableTriple<Module, Integer, TextWindow>)new MutableTriple(m, i, t));
                ++i;
            }
        }
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.fontRenderer.drawStringWithShadow("Ruhama Client", 2.0f, 2.0f, 3166352);
        for (final ModuleWindow w : this.tabs) {
            w.draw(mouseX, mouseY, 70);
        }
        for (final MutableTriple<Module, Integer, TextWindow> e : NewRuhamaGui.textWins) {
            ModuleManager.getModuleByName(e.left.getName()).getWindows().set(e.middle, e.right);
            if (ModuleManager.getModuleByName(e.left.getName()).isToggled()) {
                e.right.draw(mouseX, mouseY);
            }
        }
    }
    
    protected boolean mouseOver(final int minX, final int minY, final int maxX, final int maxY, final int mX, final int mY) {
        return mX > minX && mX < maxX && mY > minY && mY < maxY;
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        if (mouseButton == 0) {
            for (final ModuleWindow w : this.tabs) {
                w.onLmPressed();
            }
            for (final MutableTriple<Module, Integer, TextWindow> e : NewRuhamaGui.textWins) {
                if (ModuleManager.getModuleByName(e.left.getName()).isToggled()) {
                    e.right.onLmPressed();
                }
            }
        }
        else if (mouseButton == 1) {
            for (final ModuleWindow w : this.tabs) {
                w.onRmPressed();
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    public void mouseReleased(final int mouseX, final int mouseY, final int state) {
        if (state == 0) {
            for (final ModuleWindow w : this.tabs) {
                w.onLmReleased();
            }
            for (final MutableTriple<Module, Integer, TextWindow> e : NewRuhamaGui.textWins) {
                if (ModuleManager.getModuleByName(e.left.getName()).isToggled()) {
                    e.right.onLmReleased();
                }
            }
        }
        super.mouseReleased(mouseX, mouseY, state);
    }
    
    public void keyTyped(final char typedChar, final int keyCode) throws IOException {
        for (final ModuleWindow w : this.tabs) {
            w.onKeyPressed(keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }
    
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    static {
        NewRuhamaGui.textWins = new ArrayList<MutableTriple<Module, Integer, TextWindow>>();
    }
}
