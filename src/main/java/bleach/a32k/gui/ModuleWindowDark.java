package bleach.a32k.gui;

import bleach.a32k.module.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.*;
import bleach.a32k.settings.*;
import java.util.*;
import java.util.regex.*;
import org.lwjgl.input.*;
import net.minecraft.util.math.*;

public class ModuleWindowDark extends ModuleWindow
{
    public ModuleWindowDark(final List<Module> mods, final String name, final int len, final int posX, final int posY) {
        super(mods, name, len, posX, posY);
    }
    
    @Override
    public void draw(final int mX, final int mY, final int leng) {
        this.mouseX = mX;
        this.mouseY = mY;
        this.len = leng;
        this.font = Minecraft.getMinecraft().fontRenderer;
        GuiScreen.drawRect(this.posX, this.posY - 10, this.posX + this.len, this.posY, this.mouseOver(this.posX, this.posY - 10, this.posX + this.len, this.posY) ? -1877995504 : -1875890128);
        this.font.drawStringWithShadow(this.name, (float)(this.posX + this.len / 2 - this.font.getStringWidth(this.name) / 2), (float)(this.posY - 9), 7384992);
        if (this.mouseOver(this.posX, this.posY - 10, this.posX + this.len, this.posY) && this.lmDown) {
            this.dragging = true;
        }
        if (!this.lmHeld) {
            this.dragging = false;
        }
        if (this.dragging) {
            this.posX = this.mouseX - (this.prevmX - this.posX);
            this.posY = this.mouseY - (this.prevmY - this.posY);
        }
        this.prevmX = this.mouseX;
        this.prevmY = this.mouseY;
        int count = 0;
        for (final Map.Entry<Module, Boolean> m : new LinkedHashMap<Module, Boolean>(this.mods).entrySet()) {
            try {
                GuiScreen.drawRect(this.posX, this.posY + count * 14, this.posX + this.len, this.posY + 14 + count * 14, this.mouseOver(this.posX, this.posY + count * 14, this.posX + this.len, this.posY + 14 + count * 14) ? 1882206320 : 1879048192);
                this.font.drawStringWithShadow(this.cutText(m.getKey().getName(), this.len), (float)(this.posX + 2), (float)(this.posY + 3 + count * 14), m.getKey().isToggled() ? 7401440 : 12632256);
                GuiScreen.drawRect(m.getValue() ? (this.posX + this.len - 2) : (this.posX + this.len - 1), this.posY + count * 14, this.posX + this.len, this.posY + 14 + count * 14, m.getValue() ? -1619984400 : 1601241072);
                if (this.mouseOver(this.posX, this.posY + count * 14, this.posX + this.len, this.posY + 14 + count * 14)) {
                    GL11.glTranslated(0.0, 0.0, 300.0);
                    final Matcher mat = Pattern.compile("\\b.{1,22}\\b\\W?").matcher(m.getKey().getDesc());
                    int c2 = 0;
                    int c3 = 0;
                    while (mat.find()) {
                        ++c2;
                    }
                    mat.reset();
                    while (mat.find()) {
                        GuiScreen.drawRect(this.posX + this.len + 3, this.posY - 1 + count * 14 - c2 * 10 + c3 * 10, this.posX + this.len + 6 + this.font.getStringWidth(mat.group().trim()), this.posY + count * 14 - c2 * 10 + c3 * 10 + 9, -1879048144);
                        this.font.drawStringWithShadow(mat.group(), (float)(this.posX + this.len + 5), (float)(this.posY + count * 14 - c2 * 10 + c3 * 10), -1);
                        ++c3;
                    }
                    if (this.lmDown) {
                        m.getKey().toggle();
                    }
                    if (this.rmDown) {
                        this.mods.replace(m.getKey(), !m.getValue());
                    }
                    GL11.glTranslated(0.0, 0.0, -300.0);
                }
                if (m.getValue()) {
                    for (final SettingBase s : m.getKey().getSettings()) {
                        ++count;
                        if (s instanceof SettingMode) {
                            this.drawModeSetting(s.toMode(), this.posX, this.posY + count * 14);
                        }
                        if (s instanceof SettingToggle) {
                            this.drawToggleSetting(s.toToggle(), this.posX, this.posY + count * 14);
                        }
                        if (s instanceof SettingSlider) {
                            this.drawSliderSetting(s.toSlider(), this.posX, this.posY + count * 14);
                        }
                        GuiScreen.drawRect(this.posX + this.len - 1, this.posY + count * 14, this.posX + this.len, this.posY + 14 + count * 14, -1619984400);
                    }
                    ++count;
                    this.drawBindSetting(m.getKey(), this.keyDown, this.posX, this.posY + count * 14);
                    GuiScreen.drawRect(this.posX + this.len - 1, this.posY + count * 14, this.posX + this.len, this.posY + 14 + count * 14, -1619984400);
                }
                ++count;
            }
            catch (Exception e421) {
                int e420 = 10;
                for (final StackTraceElement e422 : e421.getStackTrace()) {
                    this.font.drawStringWithShadow(e422.toString(), 10.0f, (float)e420, 16719904);
                    e420 += 10;
                }
            }
        }
        this.lmDown = false;
        this.rmDown = false;
        this.keyDown = -1;
    }
    
    public void drawBindSetting(final Module m, final int key, final int x, final int y) {
        GuiScreen.drawRect(x, y, x + this.len, y + 14, 1879048192);
        if (key != -1 && this.mouseOver(x, y, x + this.len, y + 14)) {
            m.getKey().setKeyCode((key != 211 && key != 1) ? key : 0);
        }
        String name = Keyboard.getKeyName(m.getKey().getKeyCode());
        if (name == null) {
            name = "KEY" + m.getKey();
        }
        if (name.isEmpty()) {
            name = "NONE";
        }
        this.font.drawStringWithShadow("Bind: " + name + (this.mouseOver(x, y, x + this.len, y + 14) ? "..." : ""), (float)(x + 2), (float)(y + 3), this.mouseOver(x, y, x + this.len, y + 14) ? 13616079 : 13623503);
    }
    
    public void drawModeSetting(final SettingMode s, final int x, final int y) {
        GuiScreen.drawRect(x, y, x + this.len, y + 14, 1879048192);
        this.font.drawStringWithShadow(s.text + s.modes[s.mode], (float)(x + 2), (float)(y + 3), this.mouseOver(x, y, x + this.len, y + 14) ? 13616079 : 13623503);
        if (this.mouseOver(x, y, x + this.len, y + 14) && this.lmDown) {
            s.mode = s.getNextMode();
        }
    }
    
    public void drawToggleSetting(final SettingToggle s, final int x, final int y) {
        int color;
        String color2;
        if (s.state) {
            if (this.mouseOver(x, y, x + this.len, y + 14)) {
                color = -1876885728;
                color2 = "�2";
            }
            else {
                color = 1881210656;
                color2 = "�a";
            }
        }
        else if (this.mouseOver(x, y, x + this.len, y + 14)) {
            color = -1862328288;
            color2 = "�4";
        }
        else {
            color = 1895768096;
            color2 = "�c";
        }
        GuiScreen.drawRect(x, y, x + this.len, y + 14, 1879048192);
        GuiScreen.drawRect(x, y, x + 1, y + 14, color);
        this.font.drawStringWithShadow(color2 + s.text, (float)(x + 3), (float)(y + 3), -1);
        if (this.mouseOver(x, y, x + this.len, y + 14) && this.lmDown) {
            s.state = !s.state;
        }
    }
    
    public void drawSliderSetting(final SettingSlider s, final int x, final int y) {
        final int pixels = (int)Math.round(MathHelper.clamp(this.len * ((s.getValue() - s.min) / (s.max - s.min)), 0.0, this.len));
        GuiScreen.drawRect(x, y, x + this.len, y + 14, 1879048192);
        GuiScreen.drawRect(x, y, x + pixels, y + 14, -265256800);
        this.font.drawStringWithShadow(s.text + ((s.round == 0 && s.value > 100.0) ? Integer.toString((int)s.value) : Double.valueOf(s.value)), (float)(x + 2), (float)(y + 3), this.mouseOver(x, y, x + this.len, y + 14) ? 13616079 : 13623503);
        if (this.mouseOver(x, y, x + this.len, y + 14) && this.lmHeld) {
            final int percent = (this.mouseX - x) * 100 / this.len;
            s.value = s.round(percent * ((s.max - s.min) / 100.0) + s.min, s.round);
        }
    }
}
