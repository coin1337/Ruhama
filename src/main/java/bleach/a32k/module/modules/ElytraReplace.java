package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import java.util.*;
import bleach.a32k.settings.*;
import net.minecraft.item.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;

public class ElytraReplace extends Module
{
    public ElytraReplace() {
        super("ElytraReplace", 0, Category.MISC, "Automatically replaces your elytra when its low", null);
    }
    
    @Override
    public void onUpdate() {
        if (this.mc.player.inventoryContainer.getSlot(6).getStack().getItem() instanceof ItemElytra && this.mc.player.inventoryContainer.getSlot(6).getStack().getMaxDamage() - this.mc.player.inventoryContainer.getSlot(6).getStack().getItemDamage() < 9) {
            for (int i = 9, n = 9; i <= 44; n = (i = (byte)(n + 1))) {
                final ItemStack stack;
                if ((stack = this.mc.player.inventoryContainer.getSlot(n).getStack()) != ItemStack.EMPTY && stack.getItem() instanceof ItemElytra && stack.getCount() == 1 && stack.getMaxDamage() - stack.getItemDamage() > 5) {
                    this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, this.mc.player);
                    this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, n, 0, ClickType.QUICK_MOVE, this.mc.player);
                    this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, n, 0, ClickType.PICKUP, this.mc.player);
                }
            }
        }
    }
}
