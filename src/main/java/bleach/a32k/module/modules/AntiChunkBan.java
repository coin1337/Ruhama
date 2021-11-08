package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import bleach.a32k.settings.*;
import java.util.*;

public class AntiChunkBan extends Module
{
    private static final List<SettingBase> settings;
    private int dis;
    
    public AntiChunkBan() {
        super("AntiChunkBan", 0, Category.EXPLOITS, "Bypasses chunk bans", AntiChunkBan.settings);
        this.dis = 0;
    }
    
    @Override
    public void onEnable() {
        this.dis = this.mc.gameSettings.renderDistanceChunks;
    }
    
    @Override
    public void onDisable() {
        this.mc.gameSettings.renderDistanceChunks = this.dis;
    }
    
    @Override
    public void onUpdate() {
        if (this.getSettings().get(0).toMode().mode == 1) {
            this.mc.gameSettings.renderDistanceChunks = 1;
        }
    }
    
    static {
        settings = Arrays.asList(new SettingMode("Mode: ", "AntiKick", "1 chunk"));
    }
}
