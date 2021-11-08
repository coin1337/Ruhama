package bleach.a32k.module;

import net.minecraft.network.*;
import net.minecraft.client.*;
import org.lwjgl.input.*;
import bleach.a32k.module.modules.*;
import java.util.*;

public class ModuleManager
{
    private static List<Module> mods;
    
    public static List<Module> getModules() {
        return new ArrayList<Module>(ModuleManager.mods);
    }
    
    public static Module getModuleByName(final String name) {
        for (final Module m : ModuleManager.mods) {
            if (name.equals(m.getName())) {
                return m;
            }
        }
        return null;
    }
    
    public static List<Module> getModulesInCat(final Category cat) {
        final List<Module> mds = new ArrayList<Module>();
        for (final Module m : ModuleManager.mods) {
            if (m.getCategory().equals(cat)) {
                mds.add(m);
            }
        }
        return mds;
    }
    
    public static void onUpdate() {
        for (final Module m : ModuleManager.mods) {
            try {
                if (!m.isToggled()) {
                    continue;
                }
                m.onUpdate();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void onRender() {
        for (final Module m : ModuleManager.mods) {
            try {
                if (!m.isToggled()) {
                    continue;
                }
                m.onRender();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void onOverlay() {
        for (final Module m : ModuleManager.mods) {
            try {
                if (!m.isToggled()) {
                    continue;
                }
                m.onOverlay();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static boolean onPacketRead(final Packet<?> packet) {
        for (final Module m : ModuleManager.mods) {
            try {
                if (m.isToggled() && m.onPacketRead(packet)) {
                    return true;
                }
                continue;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public static boolean onPacketSend(final Packet<?> packet) {
        for (final Module m : ModuleManager.mods) {
            try {
                if (m.isToggled() && m.onPacketSend(packet)) {
                    return true;
                }
                continue;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public static void updateKeys() {
        if (Minecraft.getMinecraft().currentScreen != null) {
            return;
        }
        for (final Module m : ModuleManager.mods) {
            try {
                if (Keyboard.isKeyDown(m.getKey().getKeyCode()) && !m.keyActive) {
                    m.keyActive = true;
                    m.toggle();
                }
                else {
                    if (Keyboard.isKeyDown(m.getKey().getKeyCode())) {
                        continue;
                    }
                    m.keyActive = false;
                }
            }
            catch (Exception ex) {}
        }
    }
    
    static {
        ModuleManager.mods = Arrays.asList(new AntiChunkBan(), new Aura(), new Auto32k(), new AutoLog(), new AutoTotem(), new AutoWither(), new BedAura(), new ClickGui(), new Crasher(), new CrystalAura(), new CrystalAura2(), new DispenserAura(), new ElytraFly(), new ElytraReplace(), new Gui(), new HoleFiller(), new HoleFinderESP(), new HopperNuker(), new HopperRadius(), new InvSorter(), new NBTViewer(), new NewAuto32k(), new ObsidianTrap(), new PearlViewer(), new Peek(), new PlayerRadar(), new RuhamaOntop(), new ShulkerAura(), new StashFinder(), new StrengthESP(), new Surround(), new ThunderHack(), new TreeAura(), new TunnelESP(), new Welcomer());
    }
}
