package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import java.util.*;
import bleach.a32k.settings.*;
import net.minecraft.client.gui.inventory.*;
import bleach.a32k.*;
import net.minecraft.item.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;
import net.minecraft.init.*;

public class HopperNuker extends Module
{
    public int breakingSlot;
    public BlockPos breakingBlock;
    
    public HopperNuker() {
        super("HopperNuker", 0, Category.COMBAT, "Nukes Hoppers Arond you", null);
        this.breakingSlot = 0;
    }
    
    @Override
    public void onUpdate() {
        if (this.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (this.breakingBlock == null || Ruhama.friendBlocks.containsKey(this.breakingBlock)) {
            this.breakingSlot = this.mc.player.inventory.currentItem;
            int pickaxeSlot = this.mc.player.inventory.currentItem;
            for (int i = 0; i < 8; ++i) {
                if (this.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemPickaxe) {
                    pickaxeSlot = i;
                }
            }
            for (int x = -4; x <= 4; ++x) {
                for (int y = -4; y <= 4; ++y) {
                    for (int z = -4; z <= 4; ++z) {
                        final BlockPos pos = this.mc.player.getPosition().add(x, y, z);
                        if (this.mc.world.getBlockState(pos).getBlock() instanceof BlockHopper) {
                            if (this.mc.world.getBlockState(pos.up()).getBlock() instanceof BlockShulkerBox) {
                                if (!Ruhama.friendBlocks.containsKey(pos)) {
                                    if (this.mc.player.getPositionVector().distanceTo(new Vec3d(pos).add(0.5, 0.5, 0.5)) <= 5.25) {
                                        this.mc.player.inventory.currentItem = pickaxeSlot;
                                        this.mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP);
                                        this.mc.player.swingArm(EnumHand.MAIN_HAND);
                                        this.breakingBlock = pos;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return;
        }
        if (this.mc.world.getBlockState(this.breakingBlock).getBlock() == Blocks.AIR || this.mc.player.getPositionVector().distanceTo(new Vec3d(this.breakingBlock).add(0.5, 0.5, 0.5)) > 4.5) {
            this.breakingBlock = null;
            this.mc.player.inventory.currentItem = this.breakingSlot;
            return;
        }
        this.mc.playerController.onPlayerDamageBlock(this.breakingBlock, EnumFacing.UP);
        this.mc.player.swingArm(EnumHand.MAIN_HAND);
    }
}
