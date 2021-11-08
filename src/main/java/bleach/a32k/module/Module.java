package bleach.a32k.module;

import net.minecraft.client.*;
import net.minecraft.client.settings.*;
import bleach.a32k.settings.*;
import bleach.a32k.gui.*;
import java.util.*;
import net.minecraft.network.*;
import net.minecraftforge.fml.client.registry.*;

public class Module
{
    protected Minecraft mc;
    private String name;
    private KeyBinding key;
    private boolean toggled;
    private Category category;
    private String desc;
    private List<SettingBase> settings;
    private List<TextWindow> windows;
    public boolean keyActive;
    
    public Module(final String nm, final int k, final Category c, final String d, final List<SettingBase> s) {
        this.mc = Minecraft.getMinecraft();
        this.settings = new ArrayList<SettingBase>();
        this.windows = new ArrayList<TextWindow>();
        this.keyActive = false;
        this.registerBind(this.name = nm, k);
        this.category = c;
        this.desc = d;
        if (s != null) {
            this.settings = s;
        }
        this.toggled = false;
    }
    
    public void toggle() {
        this.toggled = !this.toggled;
        if (this.toggled) {
            try {
                this.onEnable();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                this.onDisable();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void onEnable() {
    }
    
    public void onDisable() {
    }
    
    public void onUpdate() {
    }
    
    public void onRender() {
    }
    
    public void onOverlay() {
    }
    
    public boolean onPacketRead(final Packet<?> packet) {
        return false;
    }
    
    public boolean onPacketSend(final Packet<?> packet) {
        return false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Category getCategory() {
        return this.category;
    }
    
    public String getDesc() {
        return this.desc;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public KeyBinding getKey() {
        return this.key;
    }
    
    public List<SettingBase> getSettings() {
        return this.settings;
    }
    
    public List<TextWindow> getWindows() {
        return this.windows;
    }
    
    public void setKey(final KeyBinding key) {
        this.key = key;
    }
    
    public boolean isToggled() {
        return this.toggled;
    }
    
    public void setToggled(final boolean toggled) {
        this.toggled = toggled;
    }
    
    public void registerBind(final String name, final int keycode) {
        ClientRegistry.registerKeyBinding(this.key = new KeyBinding(name, keycode, "Ruhama"));
    }
}
