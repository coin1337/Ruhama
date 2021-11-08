package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import net.minecraft.block.*;
import bleach.a32k.utils.*;
import net.minecraft.util.math.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.client.gui.*;
import net.minecraft.init.*;
import net.minecraft.enchantment.*;
import net.minecraft.item.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import bleach.a32k.settings.*;
import java.util.*;

public class Auto32k extends Module
{
    private static final List<SettingBase> settings;
    private BlockPos placedHopperPos;
    private boolean ready;
    private boolean active;
    private boolean tickPassed;
    private int timer;
    
    public Auto32k() {
        super("Auto32k", 0, Category.COMBAT, "Automatically places 32ks", Auto32k.settings);
        this.timer = 0;
    }
    
    @Override
    public void onEnable() {
        this.tickPassed = false;
        int obsidian = -1;
        int shulker = -1;
        int hopper = -1;
        for (int i = 0; i < 9; ++i) {
            if (this.mc.player.inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.HOPPER)) {
                hopper = i;
            }
            if (this.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemShulkerBox) {
                shulker = i;
            }
            if (this.mc.player.inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) {
                obsidian = i;
            }
        }
        if (shulker == -1 || hopper == -1) {
            return;
        }
        Label_0668: {
            if (this.getSettings().get(0).toMode().mode == 1) {
                final RayTraceResult ray = this.mc.player.rayTrace(4.25, this.mc.getRenderPartialTicks());
                if (WorldUtils.isBlockEmpty(ray.getBlockPos())) {
                    return;
                }
                WorldUtils.placeBlock(ray.getBlockPos().up(), hopper, this.getSettings().get(7).toToggle().state, false);
                WorldUtils.placeBlock(ray.getBlockPos().up(2), shulker, this.getSettings().get(7).toToggle().state, false);
                WorldUtils.openBlock(ray.getBlockPos().up());
                this.placedHopperPos = ray.getBlockPos().up();
                this.ready = true;
            }
            else {
                for (int x = -2; x <= 2; ++x) {
                    for (int y = -1; y <= 2; ++y) {
                        for (int z = -2; z <= 2; ++z) {
                            if (x != 0 || y != 0 || z != 0) {
                                if (x != 0 || y != 1 || z != 0) {
                                    if (WorldUtils.isBlockEmpty(this.mc.player.getPosition().add(x, y, z)) && this.mc.player.getPositionEyes(this.mc.getRenderPartialTicks()).distanceTo(this.mc.player.getPositionVector().add(x + 0.5, y + 0.5, z + 0.5)) < 4.5 && WorldUtils.isBlockEmpty(this.mc.player.getPosition().add(x, y + 1, z)) && this.mc.player.getPositionEyes(this.mc.getRenderPartialTicks()).distanceTo(this.mc.player.getPositionVector().add(x + 0.5, y + 1.5, z + 0.5)) < 4.5) {
                                        final boolean r = this.getSettings().get(7).toToggle().state;
                                        WorldUtils.placeBlock(this.mc.player.getPosition().add(x, y, z), hopper, r, false);
                                        WorldUtils.placeBlock(this.mc.player.getPosition().add(x, y + 1, z), shulker, r, false);
                                        WorldUtils.openBlock(this.mc.player.getPosition().add(x, y, z));
                                        this.placedHopperPos = this.mc.player.getPosition().add(x, y, z);
                                        this.ready = true;
                                        break Label_0668;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.getSettings().get(1).toMode().mode == 2 && obsidian == -1) {
            return;
        }
        Label_0923: {
            if (this.getSettings().get(1).toMode().mode != 0) {
                int cap2 = 0;
                this.mc.player.inventory.currentItem = ((this.getSettings().get(1).toMode().mode == 1) ? hopper : obsidian);
                for (int y = -1; y <= 1; ++y) {
                    for (int x2 = -1; x2 <= 1; ++x2) {
                        for (int z2 = -1; z2 <= 1; ++z2) {
                            if (x2 != 0 || z2 != 0) {
                                if (x2 == 0 || z2 == 0) {
                                    if (WorldUtils.placeBlock(this.placedHopperPos.add(x2, y, z2), this.mc.player.inventory.currentItem, this.getSettings().get(7).toToggle().state, false) && ++cap2 > ((this.getSettings().get(1).toMode().mode == 1) ? 1 : 2)) {
                                        break Label_0923;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void onUpdate() {
        if (!this.active && !this.ready) {
            this.setToggled(false);
        }
        if (this.active && this.getSettings().get(2).toToggle().state) {
            this.killAura();
        }
        if (this.active && this.getSettings().get(6).toToggle().state) {
            this.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(this.mc.player.rotationYaw, 90.0f, this.mc.player.onGround));
        }
        int obsidian = -1;
        for (int i = 0; i < 9; ++i) {
            if (this.mc.player.inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) {
                obsidian = i;
            }
        }
        if (this.tickPassed && this.getSettings().get(5).toToggle().state && obsidian != -1) {
            WorldUtils.placeBlock(this.placedHopperPos.add(0, 2, 0), obsidian, this.getSettings().get(7).toToggle().state, false);
        }
        this.tickPassed = true;
        if (this.mc.currentScreen instanceof GuiHopper) {
            final GuiHopper gui = (GuiHopper)this.mc.currentScreen;
            if (this.ready) {
                this.active = true;
                this.ready = false;
            }
            for (int j = 32; j <= 40; ++j) {
                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, gui.inventorySlots.getSlot(j).getStack()) > 5) {
                    this.mc.player.inventory.currentItem = j - 32;
                    break;
                }
            }
            if (this.active) {
                if (this.getSettings().get(4).toMode().mode == 0) {
                    this.timer = ((this.timer >= Math.round(20.0 / this.getSettings().get(3).toSlider().getValue())) ? 0 : (this.timer + 1));
                }
                else if (this.getSettings().get(4).toMode().mode == 1) {
                    this.timer = 0;
                }
                else if (this.getSettings().get(4).toMode().mode == 2) {
                    this.timer = ((this.timer >= this.getSettings().get(3).toSlider().getValue()) ? 0 : (this.timer + 1));
                }
            }
            if (!(gui.inventorySlots.inventorySlots.get(0).getStack().getItem() instanceof ItemAir) && this.active) {
                int slot = this.mc.player.inventory.currentItem;
                boolean pull = false;
                for (int k = 40; k >= 32; --k) {
                    if (gui.inventorySlots.getSlot(k).getStack().isEmpty()) {
                        slot = k;
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
        else {
            this.active = false;
            this.timer = 0;
        }
    }
    
    public void killAura() {
        for (int i = 0; i < ((this.getSettings().get(4).toMode().mode == 1) ? this.getSettings().get(4).toSlider().getValue() : 1.0); ++i) {
            Entity target = null;
            try {
                final List<Entity> players = new ArrayList<Entity>(this.mc.world.playerEntities);
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
        settings = Arrays.asList(new SettingMode("Mode: ", "Auto", "Looking"), new SettingMode("Protect: ", "Off", "Hopper", "Obby"), new SettingToggle(true, "Aura"), new SettingSlider(0.0, 20.0, 10.0, 0, "CPS: "), new SettingMode("CPS: ", "Clicks/Sec", "Clicks/Tick", "Tick Delay"), new SettingToggle(false, "SafeShuker"), new SettingToggle(false, "AntiAim"), new SettingToggle(true, "2b Bypass"));
    }
}
