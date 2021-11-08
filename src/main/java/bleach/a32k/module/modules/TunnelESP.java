package bleach.a32k.module.modules;

import java.util.concurrent.*;
import net.minecraft.world.chunk.*;
import bleach.a32k.module.*;
import net.minecraft.network.*;
import net.minecraft.network.play.server.*;
import net.minecraft.world.*;
import bleach.a32k.utils.*;
import net.minecraft.init.*;
import java.util.*;
import net.minecraft.util.math.*;
import bleach.a32k.settings.*;

public class TunnelESP extends Module
{
    private static final List<SettingBase> settings;
    private List<Space> spaces;
    private ConcurrentLinkedQueue<Chunk> scanQueue;
    private List<Chunk> addChunkQueue;
    private Thread scanThread;
    
    public TunnelESP() {
        super("TunnelESP", 0, Category.RENDER, "Shows 1x2/small tunnels (toggle when changing mode)", TunnelESP.settings);
        this.spaces = new ArrayList<Space>();
        this.scanQueue = new ConcurrentLinkedQueue<Chunk>();
        this.addChunkQueue = new ArrayList<Chunk>();
        (this.scanThread = new Thread(() -> {
            while (true) {
                if (!this.scanQueue.isEmpty()) {
                    try {
                        this.scan(this.scanQueue.poll());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Done Scanning Chunk! (Queue: " + this.scanQueue.size() + ")");
                }
            }
        })).start();
    }
    
    @Override
    public void onDisable() {
        this.spaces.clear();
        this.scanQueue.clear();
    }
    
    @Override
    public void onEnable() {
        for (int x = (int)(this.mc.player.posX - 120.0); x < (int)this.mc.player.posX + 120; x += 16) {
            for (int z = (int)(this.mc.player.posZ - 120.0); z < (int)this.mc.player.posZ + 120; z += 16) {
                final BlockPos b = new BlockPos(x, 0, z);
                if (this.mc.world.isBlockLoaded(b, false)) {
                    this.scanQueue.add(this.mc.world.getChunk(b));
                }
            }
        }
    }
    
    @Override
    public boolean onPacketRead(final Packet<?> packet) {
        if (packet instanceof SPacketChunkData) {
            final SPacketChunkData chunkPack = (SPacketChunkData)packet;
            final Chunk chunk = new Chunk(this.mc.world, chunkPack.getChunkX(), chunkPack.getChunkZ());
            chunk.read(chunkPack.getReadBuffer(), chunkPack.getExtractedSize(), chunkPack.isFullChunk());
            this.addChunkQueue.add(chunk);
        }
        return false;
    }
    
    @Override
    public void onUpdate() {
        for (final Chunk c : this.addChunkQueue) {
            this.scanQueue.add(c);
        }
        this.addChunkQueue.clear();
        for (final Space s : new ArrayList<Space>(this.spaces)) {
            if (this.mc.player.getPosition().add(0, -(int)this.mc.player.posY, 0).getDistance(s.pos.getX(), 0, s.pos.getZ()) > 160.0) {
                this.spaces.remove(s);
            }
        }
    }
    
    @Override
    public void onRender() {
        final float r = (float)(this.getSettings().get(2).toSlider().getValue() / 255.0);
        final float g = (float)(this.getSettings().get(3).toSlider().getValue() / 255.0);
        final float b = (float)(this.getSettings().get(4).toSlider().getValue() / 255.0);
        final float a = (float)(this.getSettings().get(5).toSlider().getValue() / 255.0);
        for (final Space s : new ArrayList<Space>(this.spaces)) {
            if (s.xpos) {
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(s.pos.getX() + 1, s.pos.getY(), s.pos.getZ(), s.pos.getX() + 1, s.pos.getY() + 1, s.pos.getZ() + 1), r, g, b, a);
            }
            if (s.xneg) {
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(s.pos.getX(), s.pos.getY(), s.pos.getZ(), s.pos.getX(), s.pos.getY() + 1, s.pos.getZ() + 1), r, g, b, a);
            }
            if (s.ypos) {
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(s.pos.getX(), s.pos.getY() + 1, s.pos.getZ(), s.pos.getX() + 1, s.pos.getY() + 1, s.pos.getZ() + 1), r, g, b, a);
            }
            if (s.yneg) {
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(s.pos.getX(), s.pos.getY(), s.pos.getZ(), s.pos.getX() + 1, s.pos.getY(), s.pos.getZ() + 1), r, g, b, a);
            }
            if (s.zpos) {
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(s.pos.getX(), s.pos.getY(), s.pos.getZ() + 1, s.pos.getX() + 1, s.pos.getY() + 1, s.pos.getZ() + 1), r, g, b, a);
            }
            if (s.zneg) {
                RenderUtils.drawFilledBlockBox(new AxisAlignedBB(s.pos.getX(), s.pos.getY(), s.pos.getZ(), s.pos.getX() + 1, s.pos.getY() + 1, s.pos.getZ()), r, g, b, a);
            }
        }
    }
    
    private void scan(final Chunk c) {
        if (!this.isToggled()) {
            return;
        }
        final List<BlockPos> air = new ArrayList<BlockPos>();
        final BlockPos start = new BlockPos(c.x * 16, 0, c.z * 16);
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = 0; y < this.mc.world.getTopSolidOrLiquidBlock(start.add(x, 0, z)).getY() - 1; ++y) {
                    if (c.getBlockState(x, y, z).getBlock() == Blocks.AIR) {
                        air.add(start.add(x, y, z));
                    }
                }
            }
        }
        final List<List<BlockPos>> rotations = Arrays.asList(Arrays.asList(new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, 1, 0), new BlockPos(0, -1, 0)), Arrays.asList(new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 1, 0), new BlockPos(0, -1, 0)));
        if (this.getSettings().get(0).toMode().mode == 0) {
            for (final BlockPos b : air) {
                if (air.contains(b.down()) && !air.contains(b.up()) && !air.contains(b.down(2))) {
                    if (this.isOnEdge(b)) {
                        if (this.mc.world.getBlockState(b.east()).getBlock() != Blocks.AIR && this.mc.world.getBlockState(b.east().down()).getBlock() != Blocks.AIR && this.mc.world.getBlockState(b.west()).getBlock() != Blocks.AIR && this.mc.world.getBlockState(b.west().down()).getBlock() != Blocks.AIR) {
                            this.spaces.add(new Space(b, true, true, true, false, false, false));
                            this.spaces.add(new Space(b.down(), true, true, false, true, false, false));
                        }
                        else {
                            if (air.contains(b.north()) || air.contains(b.north().down()) || air.contains(b.south()) || air.contains(b.south().down())) {
                                continue;
                            }
                            this.spaces.add(new Space(b, false, false, true, false, true, true));
                            this.spaces.add(new Space(b.down(), false, false, false, true, true, true));
                        }
                    }
                    else if (!air.contains(b.east()) && !air.contains(b.east().down()) && !air.contains(b.west()) && !air.contains(b.west().down())) {
                        this.spaces.add(new Space(b, true, true, true, false, false, false));
                        this.spaces.add(new Space(b.down(), true, true, false, true, false, false));
                    }
                    else {
                        if (air.contains(b.north()) || air.contains(b.north().down()) || air.contains(b.south()) || air.contains(b.south().down())) {
                            continue;
                        }
                        this.spaces.add(new Space(b, false, false, true, false, true, true));
                        this.spaces.add(new Space(b.down(), false, false, false, true, true, true));
                    }
                }
            }
        }
        else {
            final int max = (int)this.getSettings().get(1).toSlider().getValue();
            for (final List<BlockPos> rot : rotations) {
                final List<BlockPos> toExplore = new ArrayList<BlockPos>(air);
                while (!toExplore.isEmpty()) {
                    int found = 1;
                    final List<BlockPos> explored = new ArrayList<BlockPos>();
                    final List<BlockPos> exploring = new ArrayList<BlockPos>(Arrays.asList(toExplore.get(0)));
                    final List<Space> toSpacesAdd = new ArrayList<Space>(Arrays.asList(new Space(exploring.get(0), true, true, true, true, true, true)));
                    boolean shouldExit = false;
                    while (!exploring.isEmpty()) {
                        for (final BlockPos b2 : new ArrayList<BlockPos>(exploring)) {
                            for (final BlockPos r : rot) {
                                final BlockPos next = b2.add(r);
                                if (!explored.contains(next) && !exploring.contains(next) && toExplore.contains(next)) {
                                    toSpacesAdd.add(new Space(next, true, true, true, true, true, true));
                                    exploring.add(next);
                                    if (++found <= max) {
                                        continue;
                                    }
                                    shouldExit = true;
                                }
                            }
                            explored.add(b2);
                            exploring.remove(b2);
                        }
                    }
                    toExplore.removeAll(explored);
                    if (!shouldExit) {
                        int y2 = -1;
                        for (final Space s : toSpacesAdd) {
                            if (y2 == -1) {
                                y2 = s.pos.getY();
                            }
                            else {
                                if (s.pos.getY() != y2) {
                                    shouldExit = true;
                                    break;
                                }
                                continue;
                            }
                        }
                        if (!shouldExit) {
                            continue;
                        }
                        this.spaces.addAll(toSpacesAdd);
                    }
                }
            }
        }
    }
    
    private boolean isOnEdge(final BlockPos b) {
        return b.getX() % 16 == 0 || b.getX() % 16 == 15 || b.getZ() % 16 == 0 || b.getZ() % 16 == 15;
    }
    
    static {
        settings = Arrays.asList(new SettingMode("Mode: ", "1x2", "All (slow)"), new SettingSlider(2.0, 20.0, 10.0, 0, "All Max: "), new SettingSlider(0.0, 255.0, 100.0, 0, "Red: "), new SettingSlider(0.0, 255.0, 255.0, 0, "Green: "), new SettingSlider(0.0, 255.0, 100.0, 0, "Blue: "), new SettingSlider(0.0, 255.0, 100.0, 0, "Alpha: "));
    }
    
    class Space
    {
        public BlockPos pos;
        public boolean xpos;
        public boolean xneg;
        public boolean ypos;
        public boolean yneg;
        public boolean zpos;
        public boolean zneg;
        
        public Space(final BlockPos pos, final boolean xpos, final boolean xneg, final boolean ypos, final boolean yneg, final boolean zpos, final boolean zneg) {
            this.pos = pos;
            this.xpos = xpos;
            this.xneg = xneg;
            this.ypos = ypos;
            this.yneg = yneg;
            this.zpos = zpos;
            this.zneg = zneg;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Space)) {
                return false;
            }
            final Space s = (Space)o;
            return s.pos.equals(this.pos) && s.xpos == this.xpos && s.xneg == this.xneg && s.pos == this.pos && s.ypos == this.ypos && s.zpos == this.zpos && s.zneg == this.zneg;
        }
    }
}
