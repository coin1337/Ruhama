package bleach.a32k.module.modules;

import net.minecraft.util.math.*;
import net.minecraft.entity.player.*;
import bleach.a32k.module.*;
import net.minecraft.entity.*;
import net.minecraft.client.entity.*;
import net.minecraft.item.*;
import bleach.a32k.utils.*;
import bleach.a32k.settings.*;
import java.util.*;

public class ObsidianTrap extends Module
{
    BlockPos blockpos1;
    BlockPos blockpos2;
    BlockPos blockpos3;
    BlockPos blockpos4;
    BlockPos blockpos5;
    BlockPos blockpos6;
    BlockPos blockpos7;
    BlockPos blockpos8;
    BlockPos blockpos9;
    BlockPos blockpos10;
    private EntityPlayer target;
    private List<EntityPlayer> targets;
    private static final List<SettingBase> settings;
    
    public ObsidianTrap() {
        super("ObsidianTrap", 0, Category.COMBAT, "Boxes players in a obsidian box", ObsidianTrap.settings);
    }
    
    public boolean isInBlockRange(final Entity target) {
        return target.getDistance(this.mc.player) <= 4.0f;
    }
    
    public boolean isValid(final EntityPlayer entity) {
        if (entity instanceof EntityPlayer) {
            final EntityPlayer animal = entity;
            return this.isInBlockRange(animal) && animal.getHealth() > 0.0f && !animal.isDead;
        }
        return false;
    }
    
    public void loadTargets() {
        for (final EntityPlayer player : this.mc.world.playerEntities) {
            if (!(player instanceof EntityPlayerSP)) {
                final EntityPlayer p = player;
                if (this.isValid(p)) {
                    this.targets.add(p);
                }
                else {
                    if (!this.targets.contains(p)) {
                        continue;
                    }
                    this.targets.remove(p);
                }
            }
        }
    }
    
    private boolean isStackObby(final ItemStack stack) {
        return stack != null && stack.getItem() == Item.getItemById(49);
    }
    
    private boolean doesHotbarHaveObby() {
        for (int i = 36; i < 45; ++i) {
            final ItemStack stack = this.mc.player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && this.isStackObby(stack)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void onUpdate() {
        if (this.mc.player.isHandActive()) {
            return;
        }
        if (!this.isValid(this.target) || this.target == null) {
            this.updateTarget();
        }
        for (final EntityPlayer player : this.mc.world.playerEntities) {
            if (!(player instanceof EntityPlayerSP)) {
                final EntityPlayer e = player;
                if (this.isValid(e) && e.getDistance(this.mc.player) < this.target.getDistance(this.mc.player)) {
                    this.target = e;
                    return;
                }
                continue;
            }
        }
        if (this.isValid(this.target) && this.mc.player.getDistance(this.target) < 4.0f) {
            this.trap(this.target);
        }
    }
    
    private void trap(final EntityPlayer player) {
        if (this.doesHotbarHaveObby()) {
            this.blockpos1 = new BlockPos(player.posX, player.posY + 2.0, player.posZ);
            this.blockpos2 = new BlockPos(player.posX + 1.0, player.posY + 2.0, player.posZ);
            this.blockpos3 = new BlockPos(player.posX + 1.0, player.posY, player.posZ);
            this.blockpos4 = new BlockPos(player.posX, player.posY, player.posZ + 1.0);
            this.blockpos5 = new BlockPos(player.posX - 1.0, player.posY, player.posZ);
            this.blockpos6 = new BlockPos(player.posX, player.posY, player.posZ - 1.0);
            this.blockpos7 = new BlockPos(player.posX + 1.0, player.posY + 1.0, player.posZ);
            this.blockpos8 = new BlockPos(player.posX, player.posY + 1.0, player.posZ + 1.0);
            this.blockpos9 = new BlockPos(player.posX - 1.0, player.posY + 1.0, player.posZ);
            this.blockpos10 = new BlockPos(player.posX, player.posY + 1.0, player.posZ - 1.0);
            for (int i = 36; i < 45; ++i) {
                final ItemStack stack = this.mc.player.inventoryContainer.getSlot(i).getStack();
                if (stack != null && this.isStackObby(stack)) {
                    final int oldSlot = this.mc.player.inventory.currentItem;
                    if (this.mc.world.getBlockState(this.blockpos1).getMaterial().isReplaceable() || this.mc.world.getBlockState(this.blockpos3).getMaterial().isReplaceable() || this.mc.world.getBlockState(this.blockpos4).getMaterial().isReplaceable() || this.mc.world.getBlockState(this.blockpos5).getMaterial().isReplaceable() || this.mc.world.getBlockState(this.blockpos6).getMaterial().isReplaceable()) {
                        this.mc.player.inventory.currentItem = i - 36;
                        if (this.mc.world.getBlockState(this.blockpos3).getMaterial().isReplaceable()) {
                            WorldUtils.placeBlock(this.blockpos3, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }
                        if (this.mc.world.getBlockState(this.blockpos4).getMaterial().isReplaceable()) {
                            WorldUtils.placeBlock(this.blockpos4, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }
                        if (this.mc.world.getBlockState(this.blockpos5).getMaterial().isReplaceable()) {
                            WorldUtils.placeBlock(this.blockpos5, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }
                        if (this.mc.world.getBlockState(this.blockpos6).getMaterial().isReplaceable()) {
                            WorldUtils.placeBlock(this.blockpos6, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }
                        if (this.mc.world.getBlockState(this.blockpos7).getMaterial().isReplaceable()) {
                            WorldUtils.placeBlock(this.blockpos7, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }
                        if (this.mc.world.getBlockState(this.blockpos8).getMaterial().isReplaceable()) {
                            WorldUtils.placeBlock(this.blockpos8, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }
                        if (this.mc.world.getBlockState(this.blockpos9).getMaterial().isReplaceable()) {
                            WorldUtils.placeBlock(this.blockpos9, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }
                        if (this.mc.world.getBlockState(this.blockpos10).getMaterial().isReplaceable()) {
                            WorldUtils.placeBlock(this.blockpos10, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }
                        if (this.mc.world.getBlockState(this.blockpos2).getMaterial().isReplaceable()) {
                            WorldUtils.placeBlock(this.blockpos2, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }
                        if (this.mc.world.getBlockState(this.blockpos1).getMaterial().isReplaceable()) {
                            WorldUtils.placeBlock(this.blockpos1, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, this.getSettings().get(0).toToggle().state);
                        }
                        this.mc.player.inventory.currentItem = oldSlot;
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    public void onDisable() {
        this.target = null;
    }
    
    public void updateTarget() {
        for (final EntityPlayer player : this.mc.world.playerEntities) {
            if (!(player instanceof EntityPlayerSP)) {
                final EntityPlayer entity = player;
                if (entity instanceof EntityPlayerSP) {
                    continue;
                }
                if (!this.isValid(entity)) {
                    continue;
                }
                this.target = entity;
            }
        }
    }
    
    static {
        settings = Arrays.asList(new SettingToggle(false, "2b Bypass"));
    }
}
