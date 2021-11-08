package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import bleach.a32k.utils.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.entity.boss.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.*;
import bleach.a32k.settings.*;
import java.util.*;

public class AutoWither extends Module
{
    private int tick;
    private BlockPos pos;
    private int rotation;
    private boolean nameTagTime;
    private static final List<SettingBase> settings;
    
    public AutoWither() {
        super("AutoWither", 0, Category.MISC, "Automatically creates a wither", AutoWither.settings);
        this.tick = 0;
    }
    
    @Override
    public void onEnable() {
        this.tick = 0;
        this.nameTagTime = false;
        final BlockPos player = this.mc.player.getPosition();
        for (int x = -2; x <= 2; ++x) {
            for (int y = -2; y <= 1; ++y) {
                for (int z = -2; z <= 2; ++z) {
                    for (int r = 0; r <= 1; ++r) {
                        final BlockPos newPos = player.add(x, y, z);
                        if (!this.witherBoxIntersects(newPos, r) && this.isAreaEmpty(newPos, r) && WorldUtils.canPlaceBlock(newPos)) {
                            this.pos = newPos;
                            this.rotation = r;
                            return;
                        }
                    }
                }
            }
        }
        this.setToggled(false);
    }
    
    @Override
    public void onUpdate() {
        if (this.tick > 7) {
            this.setToggled(false);
        }
        ++this.tick;
        int sand = -1;
        int skull = -1;
        int tag = -1;
        for (int i = 0; i < 9; ++i) {
            if (this.mc.player.inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.SOUL_SAND)) {
                sand = i;
            }
            else if (this.mc.player.inventory.getStackInSlot(i).getItem() == Items.SKULL) {
                skull = i;
            }
            else if (this.mc.player.inventory.getStackInSlot(i).getItem() == Items.NAME_TAG) {
                tag = i;
            }
        }
        if (this.nameTagTime && tag != -1) {
            for (final Entity e : this.mc.world.loadedEntityList) {
                if (e instanceof EntityWither && !e.getName().equals(this.mc.player.inventory.getStackInSlot(tag).getDisplayName()) && this.mc.player.getDistance(e) <= 5.5) {
                    this.mc.player.inventory.currentItem = tag;
                    this.mc.playerController.interactWithEntity(this.mc.player, e, EnumHand.MAIN_HAND);
                    this.setToggled(false);
                }
            }
            return;
        }
        if (skull == -1 || sand == -1) {
            this.setToggled(false);
            return;
        }
        final LinkedHashMap<BlockPos, Integer> blocks = new LinkedHashMap<BlockPos, Integer>();
        blocks.put(this.pos, sand);
        blocks.put(this.pos.add(0, 1, 0), sand);
        blocks.put(this.pos.add(0, 2, 0), skull);
        if (this.rotation == 0) {
            blocks.put(this.pos.add(-1, 1, 0), sand);
            blocks.put(this.pos.add(1, 1, 0), sand);
            blocks.put(this.pos.add(-1, 2, 0), skull);
            blocks.put(this.pos.add(1, 2, 0), skull);
        }
        else {
            blocks.put(this.pos.add(0, 1, -1), sand);
            blocks.put(this.pos.add(0, 1, 1), sand);
            blocks.put(this.pos.add(0, 2, -1), skull);
            blocks.put(this.pos.add(0, 2, 1), skull);
        }
        int cap = 0;
        for (final Map.Entry<BlockPos, Integer> e2 : blocks.entrySet()) {
            if (cap >= 2) {
                return;
            }
            if (!WorldUtils.placeBlock(e2.getKey(), e2.getValue(), this.getSettings().get(1).toToggle().state, e2.equals(blocks.entrySet().toArray()[blocks.size() - 1]) && this.getSettings().get(1).toToggle().state)) {
                continue;
            }
            ++cap;
        }
        if (this.getSettings().get(0).toToggle().state) {
            this.nameTagTime = true;
        }
        else {
            this.setToggled(false);
        }
    }
    
    public boolean isAreaEmpty(final BlockPos p, final int rot) {
        for (int x = -1; x <= 1; ++x) {
            for (int y = 0; y <= 2; ++y) {
                if (rot == 0 && this.mc.world.getBlockState(p.add(x, y, 0)).getBlock() != Blocks.AIR) {
                    return false;
                }
                if (this.mc.world.getBlockState(p.add(0, y, x)).getBlock() != Blocks.AIR) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean witherBoxIntersects(final BlockPos p, final int rot) {
        final Vec3d vec = new Vec3d(p);
        final AxisAlignedBB box = (rot == 0) ? new AxisAlignedBB(vec.add(-1.0, 0.0, 0.0), vec.add(2.0, 3.0, 1.0)) : new AxisAlignedBB(vec.add(0.0, 0.0, -1.0), vec.add(1.0, 3.0, 2.0));
        for (final Entity e : this.mc.world.loadedEntityList) {
            if (e instanceof EntityLivingBase && box.intersects(e.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }
    
    static {
        settings = Arrays.asList(new SettingToggle(false, "Rename"), new SettingToggle(true, "2b Bypass"));
    }
}
