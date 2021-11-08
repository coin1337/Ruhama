package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import bleach.a32k.utils.*;
import net.minecraft.util.math.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.init.*;
import net.minecraft.enchantment.*;
import net.minecraft.item.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.gui.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import bleach.a32k.settings.*;
import java.util.*;

public class NewAuto32k extends Module
{
    private static final List<SettingBase> settings;
    private BlockPos pos;
    private int hopper;
    private int dispenser;
    private int redstone;
    private int shulker;
    private int block;
    private int[] rot;
    private boolean active;
    private boolean openedDispenser;
    private int dispenserTicks;
    private int ticksPassed;
    private int timer;
    
    public NewAuto32k() {
        super("NewAuto32k", 0, Category.COMBAT, "Dispenser Auto32k", NewAuto32k.settings);
        this.timer = 0;
    }
    
    @Override
    public void onEnable() {
        this.ticksPassed = 0;
        this.hopper = -1;
        this.dispenser = -1;
        this.redstone = -1;
        this.shulker = -1;
        this.block = -1;
        this.active = false;
        this.openedDispenser = false;
        this.dispenserTicks = 0;
        this.timer = 0;
        for (int i = 0; i <= 8; ++i) {
            final Item item = this.mc.player.inventory.getStackInSlot(i).getItem();
            if (item == Item.getItemFromBlock(Blocks.HOPPER)) {
                this.hopper = i;
            }
            else if (item == Item.getItemFromBlock(Blocks.DISPENSER)) {
                this.dispenser = i;
            }
            else if (item == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) {
                this.redstone = i;
            }
            else if (item instanceof ItemShulkerBox) {
                this.shulker = i;
            }
            else if (item instanceof ItemBlock) {
                this.block = i;
            }
        }
        if (this.hopper == -1) {
            RuhamaLogger.log("Missing Hopper");
        }
        else if (this.dispenser == -1) {
            RuhamaLogger.log("Missing Dispenser");
        }
        else if (this.redstone == -1) {
            RuhamaLogger.log("Missing Redstone Block");
        }
        else if (this.shulker == -1) {
            RuhamaLogger.log("Missing Shulker");
        }
        else if (this.block == -1) {
            RuhamaLogger.log("Missing Generic Block");
        }
        if (this.hopper == -1 || this.dispenser == -1 || this.redstone == -1 || this.shulker == -1 || this.block == -1) {
            this.setToggled(false);
            return;
        }
        if (this.getSettings().get(5).toMode().mode != 1) {
            for (int x = -2; x <= 2; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    for (int z = -2; z <= 2; ++z) {
                        this.rot = ((Math.abs(x) > Math.abs(z)) ? ((x > 0) ? new int[] { -1, 0 } : new int[] { 1, 0 }) : ((z > 0) ? new int[] { 0, -1 } : new int[] { 0, 1 }));
                        this.pos = this.mc.player.getPosition().add(x, y, z);
                        if (this.mc.player.getPositionEyes(this.mc.getRenderPartialTicks()).distanceTo(this.mc.player.getPositionVector().add(x - this.rot[0] / 2, y + 0.5, z + this.rot[1] / 2)) <= 4.5 && this.mc.player.getPositionEyes(this.mc.getRenderPartialTicks()).distanceTo(this.mc.player.getPositionVector().add(x + 0.5, y + 2.5, z + 0.5)) <= 4.5 && WorldUtils.canPlaceBlock(this.pos) && WorldUtils.isBlockEmpty(this.pos) && WorldUtils.isBlockEmpty(this.pos.add(this.rot[0], 0, this.rot[1])) && WorldUtils.isBlockEmpty(this.pos.add(0, 1, 0)) && WorldUtils.isBlockEmpty(this.pos.add(0, 2, 0)) && WorldUtils.isBlockEmpty(this.pos.add(this.rot[0], 1, this.rot[1]))) {
                            final boolean rotate = this.getSettings().get(0).toToggle().state;
                            WorldUtils.placeBlock(this.pos, this.block, rotate, false);
                            WorldUtils.rotatePacket(this.pos.add(-this.rot[0], 1, -this.rot[1]).getX() + 0.5, this.pos.getY() + 1, this.pos.add(-this.rot[0], 1, -this.rot[1]).getZ() + 0.5);
                            WorldUtils.placeBlock(this.pos.add(0, 1, 0), this.dispenser, false, false);
                            return;
                        }
                    }
                }
            }
            RuhamaLogger.log("Unable to place 32k");
            this.setToggled(false);
            return;
        }
        final RayTraceResult ray = this.mc.player.rayTrace(5.0, this.mc.getRenderPartialTicks());
        this.pos = ray.getBlockPos().up();
        final double x2 = this.pos.getX() - this.mc.player.posX;
        final double z2 = this.pos.getZ() - this.mc.player.posZ;
        this.rot = ((Math.abs(x2) > Math.abs(z2)) ? ((x2 > 0.0) ? new int[] { -1, 0 } : new int[] { 1, 0 }) : ((z2 > 0.0) ? new int[] { 0, -1 } : new int[] { 0, 1 }));
        if (!WorldUtils.canPlaceBlock(this.pos) || !WorldUtils.isBlockEmpty(this.pos) || !WorldUtils.isBlockEmpty(this.pos.add(this.rot[0], 0, this.rot[1])) || !WorldUtils.isBlockEmpty(this.pos.add(0, 1, 0)) || !WorldUtils.isBlockEmpty(this.pos.add(0, 2, 0)) || !WorldUtils.isBlockEmpty(this.pos.add(this.rot[0], 1, this.rot[1]))) {
            RuhamaLogger.log("Unable to place 32k");
            this.setToggled(false);
            return;
        }
        final boolean rotate2 = this.getSettings().get(0).toToggle().state;
        WorldUtils.placeBlock(this.pos, this.block, rotate2, false);
        WorldUtils.rotatePacket(this.pos.add(-this.rot[0], 1, -this.rot[1]).getX() + 0.5, this.pos.getY() + 1, this.pos.add(-this.rot[0], 1, -this.rot[1]).getZ() + 0.5);
        WorldUtils.placeBlock(this.pos.add(0, 1, 0), this.dispenser, false, false);
    }
    
    @Override
    public void onUpdate() {
        if ((this.getSettings().get(4).toToggle().state && !this.active && this.ticksPassed > 25) || (this.active && !(this.mc.currentScreen instanceof GuiHopper))) {
            this.setToggled(false);
            return;
        }
        if (this.active && this.getSettings().get(1).toToggle().state && this.timer == 0) {
            this.killAura();
        }
        if (this.mc.currentScreen instanceof GuiDispenser) {
            this.openedDispenser = true;
        }
        if (this.mc.currentScreen instanceof GuiHopper) {
            final GuiHopper gui = (GuiHopper)this.mc.currentScreen;
            for (int i = 32; i <= 40; ++i) {
                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, gui.inventorySlots.getSlot(i).getStack()) > 5) {
                    this.mc.player.inventory.currentItem = i - 32;
                    break;
                }
            }
            this.active = true;
            if (this.active) {
                if (this.getSettings().get(3).toMode().mode == 0) {
                    this.timer = ((this.timer >= Math.round(20.0 / this.getSettings().get(2).toSlider().getValue())) ? 0 : (this.timer + 1));
                }
                else if (this.getSettings().get(3).toMode().mode == 1) {
                    this.timer = 0;
                }
                else if (this.getSettings().get(3).toMode().mode == 2) {
                    this.timer = ((this.timer >= this.getSettings().get(2).toSlider().getValue()) ? 0 : (this.timer + 1));
                }
            }
            if (!(gui.inventorySlots.inventorySlots.get(0).getStack().getItem() instanceof ItemAir) && this.active) {
                int slot = this.mc.player.inventory.currentItem;
                boolean pull = false;
                for (int j = 40; j >= 32; --j) {
                    if (gui.inventorySlots.getSlot(j).getStack().isEmpty()) {
                        slot = j;
                        pull = true;
                        break;
                    }
                }
                if (pull) {
                    this.mc.playerController.windowClick(gui.inventorySlots.windowId, 0, 0, ClickType.PICKUP, this.mc.player);
                    this.mc.playerController.windowClick(gui.inventorySlots.windowId, slot, 0, ClickType.PICKUP, this.mc.player);
                }
            }
        }
        if (this.ticksPassed == 0) {
            WorldUtils.openBlock(this.pos.add(0, 1, 0));
        }
        if (this.openedDispenser && this.dispenserTicks == 0) {
            this.mc.playerController.windowClick(this.mc.player.openContainer.windowId, 36 + this.shulker, 0, ClickType.QUICK_MOVE, this.mc.player);
        }
        if (this.dispenserTicks == 1) {
            this.mc.displayGuiScreen(null);
            WorldUtils.placeBlock(this.pos.add(0, 2, 0), this.redstone, this.getSettings().get(0).toToggle().state, false);
        }
        if (this.mc.world.getBlockState(this.pos.add(this.rot[0], 1, this.rot[1])).getBlock() instanceof BlockShulkerBox && this.mc.world.getBlockState(this.pos.add(this.rot[0], 0, this.rot[1])).getBlock() != Blocks.HOPPER) {
            WorldUtils.placeBlock(this.pos.add(this.rot[0], 0, this.rot[1]), this.hopper, this.getSettings().get(0).toToggle().state, false);
            WorldUtils.openBlock(this.pos.add(this.rot[0], 0, this.rot[1]));
        }
        if (this.openedDispenser) {
            ++this.dispenserTicks;
        }
        ++this.ticksPassed;
    }
    
    public void killAura() {
        for (int i = 0; i < ((this.getSettings().get(3).toMode().mode == 1) ? this.getSettings().get(2).toSlider().getValue() : 1.0); ++i) {
            Entity target = null;
            try {
                final List<Entity> players = new ArrayList<Entity>(this.mc.world.loadedEntityList);
                for (final Entity e : new ArrayList<Entity>(players)) {
                    if (!(e instanceof EntityLivingBase)) {
                        players.remove(e);
                    }
                }
                players.remove(this.mc.player);
                players.sort((a, b) -> Float.compare(a.getDistance((Entity)this.mc.player), b.getDistance((Entity)this.mc.player)));
                if (players.get(0).getDistance(this.mc.player) < 8.0f) {
                    target = players.get(0);
                }
            }
            catch (Exception ex) {}
            if (target == null) {
                return;
            }
            WorldUtils.rotateClient(target.posX, target.posY + 1.0, target.posZ);
            if (target.getDistance(this.mc.player) > 6.0f) {
                return;
            }
            this.mc.playerController.attackEntity(this.mc.player, target);
            this.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
    
    static {
        settings = Arrays.asList(new SettingToggle(true, "2b Bypass"), new SettingToggle(true, "Killaura"), new SettingSlider(0.0, 20.0, 20.0, 0, "CPS: "), new SettingMode("CPS: ", "Clicks/Sec", "Clicks/Tick", "Tick Delay"), new SettingToggle(false, "Timeout"), new SettingMode("Place: ", "Auto", "Looking"));
    }
}
