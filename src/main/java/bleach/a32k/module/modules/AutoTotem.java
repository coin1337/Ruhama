package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import net.minecraft.client.gui.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import bleach.a32k.settings.*;
import java.util.*;

public class AutoTotem extends Module
{
    private static final List<SettingBase> settings;
    private long time;
    
    public AutoTotem() {
        super("AutoTotem", 0, Category.COMBAT, "Automatically places totems in yout first slot", AutoTotem.settings);
        this.time = 0L;
    }
    
    @Override
    public void onUpdate() {
        if (this.getSettings().get(2).toToggle().state && System.currentTimeMillis() - this.time < this.getSettings().get(3).toSlider().getValue() * 1000.0) {
            return;
        }
        this.time = System.currentTimeMillis();
        if (this.mc.currentScreen != null && this.mc.currentScreen instanceof GuiHopper) {
            return;
        }
        if (this.getSettings().get(0).toToggle().state && this.mc.player.getHeldItemOffhand().getItem() == Items.AIR) {
            for (int i = 9; i <= 44; ++i) {
                if (this.mc.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.TOTEM_OF_UNDYING) {
                    this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, this.mc.player);
                    this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, this.mc.player);
                }
            }
        }
        if (this.getSettings().get(1).toToggle().state) {
            if (this.mc.player.inventory.getStackInSlot(0).getItem() == Items.TOTEM_OF_UNDYING) {
                return;
            }
            for (int i = 9; i < 35; ++i) {
                if (this.mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                    this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, i, 0, ClickType.SWAP, this.mc.player);
                    break;
                }
            }
        }
    }
    
    static {
        settings = Arrays.asList(new SettingToggle(false, "Offhand"), new SettingToggle(true, "Hotbar"), new SettingToggle(true, "Delay"), new SettingSlider(0.0, 2.0, 0.25, 3, "Delay: "));
    }
}
