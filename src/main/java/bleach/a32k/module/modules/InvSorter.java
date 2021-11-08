package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import bleach.a32k.settings.*;
import bleach.a32k.utils.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import java.util.*;

public class InvSorter extends Module
{
    private List<String> items;
    
    public InvSorter() {
        super("InvSorter", 0, Category.MISC, "Sorts your hotbar, use /invsorter command to save", null);
    }
    
    @Override
    public void onEnable() {
        FileMang.createFile("invsorter.txt");
        this.items = FileMang.readFileLines("invsorter.txt");
        if (this.items.size() < 9) {
            RuhamaLogger.log("No Inventory Saved, Use /invsorter to save your hotbar");
            this.setToggled(false);
        }
    }
    
    @Override
    public void onUpdate() {
        int index = -1;
        int done = 0;
        for (final String s : this.items) {
            ++index;
            if (s != "") {
                if (s.equals(this.mc.player.inventory.getStackInSlot(index).getItem().getRegistryName().toString())) {
                    continue;
                }
                for (int i = 9; i <= 45; ++i) {
                    if (this.mc.player.inventory.getStackInSlot(i).getItem().getRegistryName().toString().equals(s)) {
                        if (s.equals(Items.AIR.getRegistryName().toString())) {
                            this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, 36 + index, 0, ClickType.QUICK_MOVE, this.mc.player);
                        }
                        else if (this.mc.player.inventory.getStackInSlot(index).getItem() == Items.AIR) {
                            this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, this.mc.player);
                            this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, 36 + index, 0, ClickType.PICKUP, this.mc.player);
                        }
                        else {
                            this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, this.mc.player);
                            this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, 36 + index, 0, ClickType.PICKUP, this.mc.player);
                            this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, this.mc.player);
                        }
                        ++done;
                        return;
                    }
                }
            }
        }
        if (done == 0) {
            this.setToggled(false);
        }
    }
}
