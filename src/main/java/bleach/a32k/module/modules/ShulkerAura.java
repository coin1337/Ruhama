package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import bleach.a32k.settings.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.gui.*;
import net.minecraft.block.*;
import bleach.a32k.*;
import net.minecraft.util.math.*;
import bleach.a32k.utils.*;
import java.util.*;

public class ShulkerAura extends Module
{
    public boolean inShulker;
    public HashMap<BlockPos, Integer> openedShulkers;
    
    public ShulkerAura() {
        super("ShulkerAura", 0, Category.COMBAT, "Automatically opens shulkers", null);
        this.inShulker = false;
        this.openedShulkers = new HashMap<BlockPos, Integer>();
    }
    
    @Override
    public void onUpdate() {
        final HashMap<BlockPos, Integer> tempShulkers = new HashMap<BlockPos, Integer>(this.openedShulkers);
        for (final Map.Entry<BlockPos, Integer> e : this.openedShulkers.entrySet()) {
            if (e.getValue() <= 0) {
                tempShulkers.remove(e.getKey());
            }
            tempShulkers.replace(e.getKey(), e.getValue() - 1);
        }
        this.openedShulkers.clear();
        this.openedShulkers.putAll(tempShulkers);
        if (this.mc.currentScreen instanceof GuiContainer && !(this.mc.currentScreen instanceof GuiShulkerBox)) {
            return;
        }
        if (this.mc.currentScreen instanceof GuiShulkerBox) {
            if (this.inShulker) {
                this.mc.displayGuiScreen(null);
            }
            this.inShulker = false;
            return;
        }
        for (int x = -4; x <= 4; ++x) {
            for (int y = -4; y <= 4; ++y) {
                for (int z = -4; z <= 4; ++z) {
                    final BlockPos pos = this.mc.player.getPosition().add(x, y, z);
                    if (this.mc.world.getBlockState(pos).getBlock() instanceof BlockShulkerBox) {
                        if (!Ruhama.friendBlocks.containsKey(pos)) {
                            if (!this.openedShulkers.containsKey(pos)) {
                                if (this.mc.player.getPositionVector().distanceTo(new Vec3d(pos).add(0.5, 0.5, 0.5)) <= 5.25) {
                                    WorldUtils.openBlock(pos);
                                    this.openedShulkers.put(pos, 300);
                                    this.inShulker = true;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
