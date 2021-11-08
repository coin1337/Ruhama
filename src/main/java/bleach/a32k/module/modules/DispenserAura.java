package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.tileentity.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.*;
import bleach.a32k.utils.*;
import net.minecraft.init.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import bleach.a32k.settings.*;
import java.util.*;

public class DispenserAura extends Module
{
    private static final List<SettingBase> settings;
    public int breakingSlot;
    public BlockPos breakingBlock;
    
    public DispenserAura() {
        super("DispenserAura", 0, Category.COMBAT, "Tries to block dispenser 32ks", DispenserAura.settings);
        this.breakingSlot = 0;
    }
    
    @Override
    public void onUpdate() {
        if (this.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        TileEntityDispenser dispenser = null;
        for (final TileEntity t : this.mc.world.loadedTileEntityList) {
            if (t instanceof TileEntityDispenser && this.mc.player.getDistance(t.getPos().getX() + 0.5, t.getPos().getY() + 0.5, t.getPos().getZ() + 0.5) <= ((this.getSettings().get(0).toMode().mode == 0) ? 4.5 : 5.5)) {
                dispenser = (TileEntityDispenser)t;
                break;
            }
        }
        if (dispenser == null) {
            return;
        }
        if (this.getSettings().get(0).toMode().mode == 0) {
            final BlockPos jamPos = dispenser.getPos().offset((EnumFacing)this.mc.world.getBlockState(dispenser.getPos()).getValue((IProperty)PropertyDirection.create("facing")));
            if (this.mc.player.getDistance(jamPos.getX() + 0.5, jamPos.getY() + 0.5, jamPos.getZ() + 0.5) > 4.25) {
                return;
            }
            int slot = 0;
            for (int i = 0; i <= 8; ++i) {
                final Item item = this.mc.player.inventory.getStackInSlot(i).getItem();
                if (item instanceof ItemBlock && !(((ItemBlock)item).getBlock() instanceof BlockShulkerBox) && ((ItemBlock)item).getBlock().getDefaultState().isFullCube()) {
                    slot = i;
                    break;
                }
            }
            WorldUtils.placeBlock(jamPos, slot, this.getSettings().get(1).toToggle().state, false);
        }
        else if (this.getSettings().get(0).toMode().mode == 1) {
            int pickaxeSlot = this.mc.player.inventory.currentItem;
            for (int j = 0; j < 8; ++j) {
                if (this.mc.player.inventory.getStackInSlot(j).getItem() instanceof ItemPickaxe) {
                    pickaxeSlot = j;
                }
            }
            if (this.breakingBlock != null) {
                this.mc.player.inventory.currentItem = ((this.mc.world.getBlockState(this.breakingBlock).getBlock() == Blocks.AIR) ? this.breakingSlot : pickaxeSlot);
                if (this.mc.world.getBlockState(this.breakingBlock).getBlock() == Blocks.AIR || this.mc.player.getPositionVector().distanceTo(new Vec3d(this.breakingBlock).add(0.5, 0.5, 0.5)) > 4.5) {
                    this.breakingBlock = null;
                    return;
                }
                this.mc.playerController.onPlayerDamageBlock(this.breakingBlock, EnumFacing.UP);
                this.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            else {
                this.breakingSlot = this.mc.player.inventory.currentItem;
                this.mc.player.inventory.currentItem = pickaxeSlot;
                this.mc.playerController.onPlayerDamageBlock(dispenser.getPos(), EnumFacing.UP);
                this.mc.player.swingArm(EnumHand.MAIN_HAND);
                this.breakingBlock = dispenser.getPos();
            }
        }
    }
    
    static {
        settings = Arrays.asList(new SettingMode("Mode: ", "Block", "Mine"), new SettingToggle(true, "2b Bypass"));
    }
}
