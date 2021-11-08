package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import net.minecraft.init.*;
import bleach.a32k.utils.*;
import net.minecraft.util.math.*;
import net.minecraft.item.*;
import net.minecraft.block.*;
import java.util.*;
import bleach.a32k.settings.*;

public class TreeAura extends Module
{
    private static final List<SettingBase> settings;
    private int timesBonemealed;
    private List<TreeAction> queue;
    private BlockPos pos;
    private Vec3d playerPos;
    
    public TreeAura() {
        super("TreeAura", 0, Category.MISC, "teamnotrees is a meme", TreeAura.settings);
        this.queue = new ArrayList<TreeAction>();
    }
    
    @Override
    public void onEnable() {
        this.pos = null;
        this.playerPos = this.mc.player.getPositionVector();
        this.timesBonemealed = 0;
        this.queue.clear();
        int sapling = -1;
        int dirt = -1;
        for (int i = 0; i <= 8; ++i) {
            final ItemStack stack = this.mc.player.inventory.getStackInSlot(i);
            if (this.isSapling(stack, this.getSettings().get(4).toToggle().state)) {
                sapling = i;
            }
            if (stack.getItem() == Item.getItemFromBlock(Blocks.DIRT) || stack.getItem() == Item.getItemFromBlock(Blocks.GRASS)) {
                dirt = i;
            }
        }
        if (sapling == -1) {
            RuhamaLogger.log("No sapling");
            this.setToggled(false);
            return;
        }
        final Vec3d looking = this.mc.player.getPositionVector().add(new Vec3d(0.0, 0.0, 2.0).rotateYaw(-(float)Math.toRadians(this.mc.player.rotationYaw)));
        BlockPos backupPos = null;
        if (this.getSettings().get(4).toToggle().state) {
            for (int x = -2; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    for (int z = -2; z <= 1; ++z) {
                        this.pos = this.mc.player.getPosition().add(x, y, z);
                        final Block block = this.mc.world.getBlockState(this.pos.down()).getBlock();
                        final Block block2 = this.mc.world.getBlockState(this.pos.down().east()).getBlock();
                        final Block block3 = this.mc.world.getBlockState(this.pos.down().south()).getBlock();
                        final Block block4 = this.mc.world.getBlockState(this.pos.down().south().east()).getBlock();
                        if ((block == Blocks.GRASS || block == Blocks.DIRT) && (block2 == Blocks.GRASS || block2 == Blocks.DIRT) && (block3 == Blocks.GRASS || block3 == Blocks.DIRT) && (block4 == Blocks.GRASS || block4 == Blocks.DIRT) && this.isBlockAir(this.pos) && this.isBlockAir(this.pos.east()) && this.isBlockAir(this.pos.south()) && this.isBlockAir(this.pos.south().east()) && this.canTreeGrow(this.pos)) {
                            if (this.getSettings().get(0).toMode().mode == 1) {
                                this.queue.add(new WaitAction((int)this.getSettings().get(5).toSlider().getValue()));
                            }
                            this.queue.add(new PlaceAction(this.pos, sapling));
                            this.queue.add(new PlaceAction(this.pos.east(), sapling));
                            this.queue.add(new PlaceAction(this.pos.south(), sapling));
                            this.queue.add(new PlaceAction(this.pos.south().east(), sapling));
                            return;
                        }
                        if (dirt != -1 && WorldUtils.canPlaceBlock(this.pos.down()) && this.isBlockAir(this.pos) && this.isBlockAir(this.pos.east()) && this.isBlockAir(this.pos.south()) && this.isBlockAir(this.pos.south().east()) && WorldUtils.isBlockEmpty(this.pos.down().east()) && WorldUtils.isBlockEmpty(this.pos.down().south()) && WorldUtils.isBlockEmpty(this.pos.down().south().east()) && this.canTreeGrow(this.pos)) {
                            backupPos = this.pos;
                        }
                    }
                }
            }
            if (backupPos != null) {
                this.pos = backupPos;
                if (this.getSettings().get(0).toMode().mode == 1) {
                    this.queue.add(new WaitAction((int)this.getSettings().get(5).toSlider().getValue()));
                }
                this.queue.add(new PlaceAction(this.pos.down(), dirt));
                this.queue.add(new PlaceAction(this.pos.down().east(), dirt));
                this.queue.add(new PlaceAction(this.pos.down().south(), dirt));
                this.queue.add(new PlaceAction(this.pos.down().south().east(), dirt));
                this.queue.add(new WaitAction(2));
                this.queue.add(new PlaceAction(this.pos, sapling));
                this.queue.add(new PlaceAction(this.pos.east(), sapling));
                this.queue.add(new PlaceAction(this.pos.south(), sapling));
                this.queue.add(new PlaceAction(this.pos.south().east(), sapling));
                return;
            }
        }
        else {
            for (int x = -2; x <= 2; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    for (int z = -2; z <= 2; ++z) {
                        this.pos = this.mc.player.getPosition().add(x, y, z);
                        if (Math.abs(Math.abs(looking.x) - (Math.abs(this.pos.getX()) + 0.5)) >= 1.0) {
                            if (Math.abs(Math.abs(looking.z) - (Math.abs(this.pos.getZ()) + 0.5)) >= 1.0) {
                                final Block block = this.mc.world.getBlockState(this.pos.down()).getBlock();
                                if ((block == Blocks.GRASS || block == Blocks.DIRT) && this.isBlockAir(this.pos) && this.canTreeGrow(this.pos)) {
                                    this.queue.add(new WaitAction((int)this.getSettings().get(5).toSlider().getValue()));
                                    this.queue.add(new PlaceAction(this.pos, sapling));
                                    return;
                                }
                                if (dirt != -1 && WorldUtils.canPlaceBlock(this.pos.down()) && this.isBlockAir(this.pos) && this.canTreeGrow(this.pos)) {
                                    backupPos = this.pos;
                                }
                            }
                        }
                    }
                }
            }
            if (backupPos != null) {
                this.pos = backupPos;
                if (this.getSettings().get(0).toMode().mode == 1) {
                    this.queue.add(new WaitAction((int)this.getSettings().get(5).toSlider().getValue()));
                }
                this.queue.add(new PlaceAction(this.pos.down(), dirt));
                this.queue.add(new PlaceAction(this.pos, sapling));
                return;
            }
        }
        if (this.getSettings().get(0).toMode().mode == 0) {
            RuhamaLogger.log("Nowhere to place sapling");
            this.setToggled(false);
        }
        else {
            this.pos = null;
        }
    }
    
    @Override
    public void onRender() {
        if (this.pos == null) {
            return;
        }
        if (this.getSettings().get(4).toToggle().state) {
            RenderUtils.drawFilledBlockBox(new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 2, this.pos.getY(), this.pos.getZ() + 2), 1.0f, 0.0f, 0.0f, 0.1f);
        }
        else {
            RenderUtils.drawFilledBlockBox(new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY(), this.pos.getZ() + 1), 1.0f, 0.0f, 0.0f, 0.1f);
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.pos == null || this.mc.player.getDistance(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) > 4.5 || this.timesBonemealed > 10 || this.mc.world.getBlockState(this.pos).getBlock() == Blocks.LOG || this.mc.world.getBlockState(this.pos).getBlock() == Blocks.LOG2) {
            if (this.getSettings().get(0).toMode().mode == 0) {
                this.setToggled(false);
            }
            else {
                this.onEnable();
            }
            return;
        }
        int sapling = -1;
        int dirt = -1;
        int bonemeal = -1;
        for (int i = 0; i <= 8; ++i) {
            final ItemStack stack = this.mc.player.inventory.getStackInSlot(i);
            if (sapling == -1 && this.isSapling(stack, this.getSettings().get(4).toToggle().state)) {
                sapling = i;
            }
            if (dirt == -1 && (stack.getItem() == Item.getItemFromBlock(Blocks.DIRT) || stack.getItem() == Item.getItemFromBlock(Blocks.GRASS))) {
                dirt = i;
            }
            if (bonemeal == -1 && stack.getItem() instanceof ItemDye && EnumDyeColor.byDyeDamage(stack.getMetadata()) == EnumDyeColor.WHITE) {
                bonemeal = i;
            }
        }
        if (sapling == -1) {
            RuhamaLogger.log("No sapling");
            this.setToggled(false);
            return;
        }
        if (this.getSettings().get(4).toToggle().state && this.mc.player.getEntityBoundingBox().intersects(new AxisAlignedBB(this.pos.down(), this.pos.add(2, 1, 2))) && !this.queue.isEmpty() && !(this.queue.get(0) instanceof WaitAction)) {
            this.queue.add(0, new WaitAction(1));
        }
        if (this.queue.isEmpty() && this.getSettings().get(1).toToggle().state && bonemeal != -1 && this.timesBonemealed < 10) {
            this.queue.add(new BonemealAction(this.pos, bonemeal));
            ++this.timesBonemealed;
        }
        else if (this.queue.isEmpty()) {
            if (this.getSettings().get(0).toMode().mode == 0) {
                this.setToggled(false);
            }
            else {
                this.onEnable();
            }
            return;
        }
        if (this.getSettings().get(3).toToggle().state && !this.queue.isEmpty() && (!(this.queue.get(0) instanceof WaitAction) || ((WaitAction) this.queue.get(0)).waitTime <= 1)) {
            this.mc.player.motionX = MathHelper.clamp(this.playerPos.x - this.mc.player.posX, -0.1, 0.1);
            this.mc.player.motionZ = MathHelper.clamp(this.playerPos.z - this.mc.player.posZ, -0.1, 0.1);
        }
        this.processQueue();
    }
    
    public void processQueue() {
        if (this.queue.isEmpty()) {
            return;
        }
        if (this.queue.get(0) instanceof WaitAction) {
            final WaitAction action = (WaitAction) this.queue.get(0);
            if (action.isDone()) {
                this.queue.remove(0);
            }
            else {
                action.decrese();
            }
        }
        else if (this.queue.get(0) instanceof PlaceAction) {
            final PlaceAction action2 = (PlaceAction) this.queue.get(0);
            WorldUtils.placeBlock(action2.pos, action2.slot, this.getSettings().get(2).toToggle().state, false);
            this.queue.remove(0);
            if (this.getSettings().get(6).toSlider().getValue() > 0.0) {
                this.queue.add(0, new WaitAction((int)this.getSettings().get(6).toSlider().getValue()));
            }
        }
        else if (this.queue.get(0) instanceof BonemealAction) {
            final BonemealAction action3 = (BonemealAction) this.queue.get(0);
            this.mc.player.inventory.currentItem = action3.slot;
            WorldUtils.openBlock(this.queue.get(0).pos);
            this.queue.remove(0);
            if (this.getSettings().get(6).toSlider().getValue() > 0.0) {
                this.queue.add(0, new WaitAction((int)this.getSettings().get(6).toSlider().getValue()));
            }
        }
    }
    
    public boolean isSapling(final ItemStack item, final boolean thicc) {
        if (item.getItem() instanceof ItemBlock && ((ItemBlock)item.getItem()).getBlock() instanceof BlockSapling) {
            if (!thicc && item.getItemDamage() != 5) {
                return true;
            }
            return thicc && (item.getItemDamage() == 1 || item.getItemDamage() == 3 || item.getItemDamage() == 5);
        }
        return false;
    }
    
    public boolean canTreeGrow(final BlockPos pos2) {
        final List<Block> blocks = Arrays.asList(Blocks.AIR, Blocks.LOG, Blocks.LOG2, Blocks.LEAVES, Blocks.LEAVES2, Blocks.GRASS, Blocks.DIRT, Blocks.SAPLING, Blocks.VINE);
        for (int x = -2; x <= 2; ++x) {
            for (int y = 1; y <= 14; ++y) {
                for (int z = -2; z <= 2; ++z) {
                    final BlockPos nextPos = pos2.add(x, y, z);
                    final Block block = this.mc.world.getBlockState(nextPos).getBlock();
                    if (!blocks.contains(block)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public boolean isBlockAir(final BlockPos pos2) {
        if (!WorldUtils.isBlockEmpty(pos2)) {
            return false;
        }
        final List<Block> waterBlocks = Arrays.asList(Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER);
        return !waterBlocks.contains(this.mc.world.getBlockState(pos2).getBlock());
    }
    
    static {
        settings = Arrays.asList(new SettingMode("Mode: ", "Once", "Repeat"), new SettingToggle(true, "Bonemeal"), new SettingToggle(true, "2b Bypass"), new SettingToggle(false, "Freeze"), new SettingToggle(false, "2x2"), new SettingSlider(0.0, 40.0, 10.0, 0, "Delay: "), new SettingSlider(0.0, 10.0, 0.0, 0, "Use Delay: "));
    }
    
    class TreeAction
    {
        public BlockPos pos;
    }
    
    class WaitAction extends TreeAction
    {
        public int waitTime;
        
        public WaitAction(final int setTimer) {
            this.waitTime = 0;
            this.waitTime = setTimer;
        }
        
        public boolean isDone() {
            return this.waitTime <= 0;
        }
        
        public void decrese() {
            --this.waitTime;
        }
    }
    
    class PlaceAction extends TreeAction
    {
        public int slot;
        
        public PlaceAction(final BlockPos setPos, final int setSlot) {
            this.pos = setPos;
            this.slot = setSlot;
        }
    }
    
    class BonemealAction extends TreeAction
    {
        public int slot;
        
        public BonemealAction(final BlockPos setPos, final int setSlot) {
            this.pos = setPos;
            this.slot = setSlot;
        }
    }
}
