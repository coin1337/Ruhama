package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import bleach.a32k.settings.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import bleach.a32k.utils.*;
import java.util.*;

public class Surround extends Module
{
    public Surround() {
        super("Surround", 0, Category.COMBAT, "Build obsidian around you to protect you from crystals", Arrays.asList(new SettingMode("Mode: ", "1x1", "2x2", "Smart"), new SettingToggle(true, "Switch Back"), new SettingToggle(false, "2 High"), new SettingToggle(true, "2b Bypass")));
    }
    
    @Override
    public void onUpdate() {
        int obsidian = -1;
        for (int i = 0; i < 9; ++i) {
            if (this.mc.player.inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) {
                obsidian = i;
                break;
            }
        }
        int cap = 0;
        final List<BlockPos> poses = new ArrayList<BlockPos>();
        final boolean rotate = this.getSettings().get(3).toToggle().state;
        if (this.getSettings().get(0).toMode().mode == 0) {
            poses.addAll(Arrays.asList(new BlockPos(this.mc.player.getPositionVector()).add(0, 0, 1), new BlockPos(this.mc.player.getPositionVector()).add(1, 0, 0), new BlockPos(this.mc.player.getPositionVector()).add(0, 0, -1), new BlockPos(this.mc.player.getPositionVector()).add(-1, 0, 0)));
        }
        else if (this.getSettings().get(0).toMode().mode == 1) {
            poses.addAll(Arrays.asList(new BlockPos(this.mc.player.getPositionVector()).add(0, 0, 2), new BlockPos(this.mc.player.getPositionVector()).add(2, 0, 0), new BlockPos(this.mc.player.getPositionVector()).add(0, 0, -2), new BlockPos(this.mc.player.getPositionVector()).add(-2, 0, 0)));
        }
        else if (this.getSettings().get(0).toMode().mode == 2) {
            poses.addAll(Arrays.asList(new BlockPos(this.mc.player.getPositionVector().add(0.0, 0.0, -this.mc.player.width)).add(0, 0, -1), new BlockPos(this.mc.player.getPositionVector().add(-this.mc.player.width, 0.0, 0.0)).add(-1, 0, 0), new BlockPos(this.mc.player.getPositionVector().add(0.0, 0.0, this.mc.player.width)).add(0, 0, 1), new BlockPos(this.mc.player.getPositionVector().add(this.mc.player.width, 0.0, 0.0)).add(1, 0, 0)));
        }
        for (final BlockPos b : new ArrayList<BlockPos>(poses)) {
            poses.add(0, b.down());
            if (this.getSettings().get(2).toToggle().state) {
                poses.add(0, b.up());
            }
        }
        if (obsidian != -1) {
            final int hand = this.mc.player.inventory.currentItem;
            for (final BlockPos b2 : poses) {
                if (WorldUtils.placeBlock(b2, obsidian, rotate, false)) {
                    ++cap;
                }
                if (cap > 2) {
                    break;
                }
            }
            if (this.getSettings().get(1).toToggle().state) {
                this.mc.player.inventory.currentItem = hand;
            }
        }
    }
}
