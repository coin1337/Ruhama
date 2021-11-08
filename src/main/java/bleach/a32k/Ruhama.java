package bleach.a32k;

import bleach.a32k.command.*;
import bleach.a32k.gui.AdvancedText;
import bleach.a32k.gui.NewRuhamaGui;
import bleach.a32k.gui.TextWindow;
import bleach.a32k.module.Module;
import bleach.a32k.module.ModuleManager;
import bleach.a32k.module.modules.ClickGui;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingMode;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.utils.FileMang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.command.ICommand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.tuple.MutableTriple;

import java.util.HashMap;
import java.util.Map;

@Mod(modid = "ruhama", name = "Ruhama", version = "0.8", acceptedMinecraftVersions = "[1.12.2]")
public class Ruhama
{
    public static final String VERSION = "0.8";
    public static final boolean PLUS = false;
    public static Minecraft mc;
    private long timer;
    private boolean timerStart;
    public static HashMap<BlockPos, Integer> friendBlocks;
    
    public Ruhama() {
        this.timer = 0L;
        this.timerStart = false;
    }
    
    @Mod.EventHandler
    public void cuckfuck(final FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClickGui.clickGui.initWindows();
        FileMang.init();
        FileMang.readModules();
        FileMang.readSettings();
        FileMang.readClickGui();
        FileMang.readBinds();
        FileMang.createFile("friends.txt");
        for (final Module m : ModuleManager.getModules()) {
            for (final SettingBase s : m.getSettings()) {
                if (s instanceof SettingMode) {
                    s.toMode().mode = MathHelper.clamp(s.toMode().mode, 0, s.toMode().modes.length - 1);
                }
                else {
                    if (!(s instanceof SettingSlider)) {
                        continue;
                    }
                    s.toSlider().value = MathHelper.clamp(s.toSlider().value, s.toSlider().min, s.toSlider().max);
                }
            }
        }
        this.timerStart = true;
    }
    
    @Mod.EventHandler
    public void cuckfucksuck(final FMLPostInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new PeekCmd.PeekCommand());
        ClientCommandHandler.instance.registerCommand(new LoginCmd());
        ClientCommandHandler.instance.registerCommand(new InvSorterCmd());
        ClientCommandHandler.instance.registerCommand(new StashFinderCmd());
        ClientCommandHandler.instance.registerCommand(new EntityDesyncCmd());
        MinecraftForge.EVENT_BUS.register(new PeekCmd());
    }
    
    @SubscribeEvent
    public void suckfuck(final RenderWorldLastEvent event) {
        if (Ruhama.mc.player == null || Ruhama.mc.world == null) {
            return;
        }
        if (!Ruhama.mc.world.isBlockLoaded(Ruhama.mc.player.getPosition())) {
            return;
        }
        ModuleManager.onRender();
    }
    
    @SubscribeEvent
    public void fuckcuck(final RenderGameOverlayEvent.Text event) {
        if (!event.getType().equals(RenderGameOverlayEvent.ElementType.TEXT)) {
            return;
        }
        if (!(Ruhama.mc.currentScreen instanceof NewRuhamaGui)) {
            for (final MutableTriple<Module, Integer, TextWindow> e : NewRuhamaGui.textWins) {
                if (ModuleManager.getModuleByName(e.left.getName()).isToggled()) {
                    int h = 2;
                    for (final AdvancedText s : e.right.getText()) {
                        final ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
                        final int x = (e.right.posX > scale.getScaledWidth() / 1.5) ? (e.right.posX + e.right.len - Ruhama.mc.fontRenderer.getStringWidth(s.text) - 2) : ((e.right.posX < scale.getScaledWidth() / 3) ? (e.right.posX + 2) : (e.right.posX + e.right.len / 2 - Ruhama.mc.fontRenderer.getStringWidth(s.text) / 2));
                        if (s.shadow) {
                            Ruhama.mc.fontRenderer.drawStringWithShadow(s.text, (float)x, (float)(e.right.posY + h), s.color);
                        }
                        else {
                            Ruhama.mc.fontRenderer.drawString(s.text, x, e.right.posY + h, s.color);
                        }
                        h += 10;
                    }
                }
            }
        }
        ModuleManager.onOverlay();
    }
    
    @SubscribeEvent
    public void suckcuck(final ClientChatEvent event) {
        if (ModuleManager.getModuleByName("RuhamaOntop").isToggled() && !event.getMessage().contains("\u0280\u1d1c\u029c\u1d00\u1d0d\u1d00") && !event.getMessage().startsWith("/")) {
            event.setCanceled(true);
            Ruhama.mc.ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
            Ruhama.mc.player.sendChatMessage(event.getMessage() + " \uff5c \u0280\u1d1c\u029c\u1d00\u1d0d\u1d00");
        }
    }
    
    @SubscribeEvent
    public void fucksuck(final TickEvent.ClientTickEvent event) {
        if (System.currentTimeMillis() - 5000L > this.timer && this.timerStart) {
            this.timer = System.currentTimeMillis();
            FileMang.saveClickGui();
            FileMang.saveSettings();
            FileMang.saveModules();
            FileMang.saveBinds();
        }
        if (event.phase != TickEvent.Phase.START || Ruhama.mc.player == null || Ruhama.mc.world == null) {
            return;
        }
        if (!Ruhama.mc.world.isBlockLoaded(new BlockPos(Ruhama.mc.player.posX, 0.0, Ruhama.mc.player.posZ))) {
            return;
        }
        ModuleManager.onUpdate();
        ModuleManager.updateKeys();
        try {
            for (final Map.Entry<BlockPos, Integer> e : Ruhama.friendBlocks.entrySet()) {
                if (e.getValue() <= 0) {
                    Ruhama.friendBlocks.remove(e.getKey());
                }
                Ruhama.friendBlocks.replace(e.getKey(), e.getValue() - 1);
            }
        }
        catch (Exception ex) {}
    }
    
    static {
        Ruhama.mc = Minecraft.getMinecraft();
        Ruhama.friendBlocks = new HashMap<BlockPos, Integer>();
    }
}
