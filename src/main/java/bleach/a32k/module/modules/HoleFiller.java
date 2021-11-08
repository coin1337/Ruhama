package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.block.material.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import bleach.a32k.utils.*;
import bleach.a32k.settings.*;
import java.util.*;

public class HoleFiller extends Module
{
    private static final List<SettingBase> settings;
    
    public HoleFiller() {
        super("HoleFiller", 0, Category.COMBAT, "Fills holes that other people can jump into", HoleFiller.settings);
    }
    
    @Override
    public void onUpdate() {
        int v7 = -1;
        for (int i = 0; i < 8; ++i) {
            if (this.mc.player.inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) {
                v7 = i;
            }
        }
        if (v7 == -1) {
            return;
        }
        for (final EntityPlayer v8 : this.mc.world.playerEntities) {
            if (!v8.getUniqueID().equals(this.mc.player.getUniqueID())) {
                final int v9 = (int)this.getSettings().get(0).toSlider().getValue();
                final BlockPos v10 = v8.getPosition();
                final Iterable<BlockPos> v11 = BlockPos.getAllInBox(v10.add(-v9, -v9, -v9), v10.add(v9, v9, v9));
                for (final BlockPos v12 : v11) {
                    if (this.mc.player.getDistanceSqToCenter(v12) > this.getSettings().get(1).toSlider().getValue()) {
                        continue;
                    }
                    if (!this.mc.world.getBlockState(v12).getMaterial().isReplaceable()) {
                        continue;
                    }
                    if (!this.mc.world.getBlockState(v12.add(0, 1, 0)).getMaterial().isReplaceable()) {
                        continue;
                    }
                    final boolean v13 = this.mc.world.getBlockState(v12.add(0, -1, 0)).getMaterial().isSolid() && this.mc.world.getBlockState(v12.add(1, 0, 0)).getMaterial().isSolid() && this.mc.world.getBlockState(v12.add(0, 0, 1)).getMaterial().isSolid() && this.mc.world.getBlockState(v12.add(-1, 0, 0)).getMaterial().isSolid() && this.mc.world.getBlockState(v12.add(0, 0, -1)).getMaterial().isSolid() && this.mc.world.getBlockState(v12.add(0, 0, 0)).getMaterial() == Material.AIR && this.mc.world.getBlockState(v12.add(0, 1, 0)).getMaterial() == Material.AIR && this.mc.world.getBlockState(v12.add(0, 2, 0)).getMaterial() == Material.AIR;
                    if (!v13) {
                        continue;
                    }
                    if (!this.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(v12)).isEmpty()) {
                        continue;
                    }
                    final int v14 = this.mc.player.inventory.currentItem;
                    WorldUtils.placeBlock(v12, this.mc.player.inventory.currentItem = v7, this.getSettings().get(2).toToggle().state, this.getSettings().get(2).toToggle().state);
                    this.mc.player.inventory.currentItem = v14;
                }
            }
        }
    }
    
    static {
        settings = Arrays.asList(new SettingSlider(1.0, 5.0, 3.0, 2, "Radius: "), new SettingSlider(1.0, 10.0, 5.0, 2, "Range: "), new SettingToggle(true, "2b Bypass"));
    }
}
