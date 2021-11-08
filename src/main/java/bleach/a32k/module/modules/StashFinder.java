package bleach.a32k.module.modules;

import java.time.format.*;
import net.minecraft.network.*;
import net.minecraft.network.play.server.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import net.minecraft.init.*;
import net.minecraft.util.text.*;
import java.time.*;
import java.time.temporal.*;
import org.apache.commons.lang3.*;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.*;
import net.minecraft.tileentity.*;
import bleach.a32k.module.*;
import net.minecraft.network.play.client.*;
import net.minecraft.client.settings.*;
import java.util.*;
import bleach.a32k.utils.*;
import net.minecraft.util.math.*;
import net.minecraft.client.entity.*;
import bleach.a32k.settings.*;

public class StashFinder extends Module
{
    public List<ChunkPos> chunks;
    public List<ChunkPos> nextChunks;
    public int range;
    public ChunkPos nextChunk;
    public ChunkPos startChunk;
    public List<ChunkPos> chestList;
    public List<UUID> dupeList;
    public List<BlockPos> shulkers;
    public List<BlockPos> signs;
    public boolean elytraing;
    public int elytratime;
    public int timeout;
    private final DateTimeFormatter dtf;
    private static final List<SettingBase> settings;
    
    public StashFinder() {
        super("StashFinder", 0, Category.MISC, "Explores Chunks around you using the elytra and logs stashes (saves in \".minecraft/bleach/ruhama/\", /stashfinder x z to set a start point)", StashFinder.settings);
        this.chunks = new ArrayList<ChunkPos>();
        this.nextChunks = new ArrayList<ChunkPos>();
        this.range = 0;
        this.chestList = new ArrayList<ChunkPos>();
        this.dupeList = new ArrayList<UUID>();
        this.shulkers = new ArrayList<BlockPos>();
        this.signs = new ArrayList<BlockPos>();
        this.timeout = 0;
        this.dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    }
    
    @Override
    public void onDisable() {
        this.chunks = new ArrayList<ChunkPos>();
        this.nextChunks = new ArrayList<ChunkPos>();
        this.nextChunk = null;
        this.startChunk = null;
        this.range = 0;
        this.shulkers.clear();
        this.signs.clear();
    }
    
    @Override
    public boolean onPacketRead(final Packet<?> packet) {
        if (this.getSettings().get(8).toToggle().state && packet instanceof SPacketChunkData && this.mc.world.getBiome(this.mc.player.getPosition()) != Biomes.HELL) {
            final SPacketChunkData chunkPack = (SPacketChunkData)packet;
            final Chunk chunk = new Chunk(this.mc.world, chunkPack.getChunkX(), chunkPack.getChunkZ());
            chunk.read(chunkPack.getReadBuffer(), chunkPack.getExtractedSize(), chunkPack.isFullChunk());
            final List<Block> blocks = Arrays.asList(Blocks.BEDROCK, Blocks.BARRIER, Blocks.END_PORTAL_FRAME, Blocks.END_PORTAL);
            String content = "";
            for (int x = 0; x < 15; ++x) {
                for (int y = 0; y < 255; ++y) {
                    for (int z = 0; z < 15; ++z) {
                        final Block b = chunk.getBlockState(x, y, z).getBlock();
                        if (blocks.contains(b) && (b != Blocks.BEDROCK || y > 5)) {
                            this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("§3Illegal: " + b.getLocalizedName() + ": " + (chunk.x * 16 + x) + ", " + y + ", " + (chunk.z * 16 + z)));
                            content = content + "Illegal | " + b.getLocalizedName() + " | " + (chunk.x * 16 + x) + ", " + y + ", " + (chunk.z * 16 + z) + " | " + this.dtf.format(LocalDateTime.now()) + "\n";
                        }
                    }
                }
            }
            if (!content.isEmpty()) {
                FileMang.createFile("stashfinder.txt");
                FileMang.appendFile(StringUtils.chop(content), "stashfinder.txt");
            }
        }
        return false;
    }
    
    @Override
    public void onEnable() {
        if (this.startChunk != null) {
            RuhamaLogger.log("StashFinder: Starting from " + this.startChunk.getXStart() + ", " + this.startChunk.getZStart());
            this.range = ((int)Math.max(Math.abs(this.mc.player.posX - this.startChunk.getXStart()), Math.abs(this.mc.player.posZ - this.startChunk.getZStart())) >> 4) - 1;
        }
        else if (this.getSettings().get(0).toMode().mode == 1) {
            this.range = ((int)Math.max(Math.abs(this.mc.player.posX), Math.abs(this.mc.player.posZ)) >> 4) - 1;
            if (this.range < 0) {
                this.range = 0;
            }
            this.startChunk = new ChunkPos(0, 0);
        }
        else {
            this.startChunk = new ChunkPos(this.mc.player.getPosition());
        }
        if (this.getSettings().get(8).toToggle().state) {
            this.mc.renderGlobal.loadRenderers();
        }
    }
    
    @Override
    public void onUpdate() {
        if (!this.getSettings().get(10).toToggle().state) {
            return;
        }
        if (this.getSettings().get(4).toToggle().state && this.mc.player.ticksExisted % 10 == 0) {
            String content = "";
            for (final TileEntity t : this.mc.world.loadedTileEntityList) {
                if (t instanceof TileEntityShulkerBox && !this.shulkers.contains(t.getPos())) {
                    int count = 0;
                    for (final TileEntity t2 : this.mc.world.loadedTileEntityList) {
                        if (t2 != t && (t2 instanceof TileEntityChest || t2 instanceof TileEntityShulkerBox) && t2.getPos().distanceSq(t.getPos()) < 20.0) {
                            ++count;
                        }
                    }
                    this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("§3Shulker: " + t.getPos().getX() + ", " + t.getPos().getY() + ", " + t.getPos().getZ()));
                    content = content + t.getDisplayName().getUnformattedText() + " | " + Block.REGISTRY.getNameForObject(t.getBlockType()) + " | " + t.getPos().getX() + ", " + t.getPos().getY() + ", " + t.getPos().getZ() + " | " + this.dtf.format(LocalDateTime.now()) + " (" + count + " nearby)\n";
                    this.shulkers.add(t.getPos());
                }
            }
            if (!content.isEmpty()) {
                FileMang.createFile("stashfinder.txt");
                FileMang.appendFile(StringUtils.chop(content), "stashfinder.txt");
            }
        }
        if (this.getSettings().get(5).toToggle().state && this.mc.player.ticksExisted % 10 == 0) {
            String content = "";
            final HashMap<ChunkPos, Integer> chunkMap = new HashMap<ChunkPos, Integer>();
            for (final TileEntity t3 : this.mc.world.loadedTileEntityList) {
                if (t3 instanceof TileEntityChest) {
                    final Integer i = chunkMap.get(new ChunkPos(t3.getPos()));
                    chunkMap.put(new ChunkPos(t3.getPos()), (i == null) ? 0 : (i + 1));
                }
            }
            for (final Map.Entry<ChunkPos, Integer> e : chunkMap.entrySet()) {
                if (this.chestList.contains(e.getKey())) {
                    continue;
                }
                if (e.getValue() < this.getSettings().get(9).toSlider().getValue()) {
                    continue;
                }
                this.chestList.add(e.getKey());
                final String text = e.getValue() + "x Chest | " + e.getKey().getXStart() + ", " + e.getKey().getZStart();
                this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("§3" + text));
                content = content + text + " | " + this.dtf.format(LocalDateTime.now()) + "\n";
            }
            if (!content.isEmpty()) {
                FileMang.createFile("stashfinder.txt");
                FileMang.appendFile(StringUtils.chop(content), "stashfinder.txt");
            }
        }
        if (this.getSettings().get(6).toToggle().state && this.mc.player.ticksExisted % 10 == 0) {
            String content = "";
            for (final Entity e2 : this.mc.world.loadedEntityList) {
                if (e2 instanceof AbstractChestHorse) {
                    final AbstractChestHorse e3 = (AbstractChestHorse)e2;
                    if (!e3.hasChest() || this.dupeList.contains(e3.getUniqueID())) {
                        continue;
                    }
                    this.dupeList.add(e3.getUniqueID());
                    this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("§3Dupe / " + e3.getDisplayName().getUnformattedText() + ": " + (int)e3.posX + ", " + (int)e3.posY + ", " + (int)e3.posZ));
                    content = "Dupe | " + e3.getDisplayName().getUnformattedText() + " | " + (int)e3.posX + ", " + (int)e3.posY + ", " + (int)e3.posZ + " | " + this.dtf.format(LocalDateTime.now()) + "\n";
                }
            }
            if (!content.isEmpty()) {
                FileMang.createFile("stashfinder.txt");
                FileMang.appendFile(StringUtils.chop(content), "stashfinder.txt");
            }
        }
        if (this.getSettings().get(7).toToggle().state && this.mc.player.ticksExisted % 10 == 0) {
            String content = "";
            for (final TileEntity t : this.mc.world.loadedTileEntityList) {
                if (t instanceof TileEntitySign) {
                    final TileEntitySign t4 = (TileEntitySign)t;
                    if (this.signs.contains(t4.getPos()) || t4.signText == new ITextComponent[] {new TextComponentString(""), new TextComponentString(""), new TextComponentString(""), new TextComponentString("")}) {
                        continue;
                    }
                    this.signs.add(t4.getPos());
                    this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("§3Sign: <\"" + t4.signText[0].getUnformattedText() + " | " + t4.signText[1].getUnformattedText() + " | " + t4.signText[2].getUnformattedText() + " | " + t4.signText[3].getUnformattedText() + "\"> at: " + t4.getPos().getX() + ", " + t4.getPos().getY() + ", " + t4.getPos().getZ()));
                    content = "Sign <" + t4.signText[0].getUnformattedText() + " | " + t4.signText[1].getUnformattedText() + " | " + t4.signText[2].getUnformattedText() + " | " + t4.signText[3].getUnformattedText() + "> | " + t4.getPos().getX() + ", " + t4.getPos().getY() + ", " + t4.getPos().getZ() + " | " + this.dtf.format(LocalDateTime.now()) + "\n";
                }
            }
            if (!content.isEmpty()) {
                FileMang.createFile("stashfinder.txt");
                FileMang.appendFile(StringUtils.chop(content), "stashfinder.txt");
            }
        }
        if (!this.mc.player.isElytraFlying() && this.elytraing) {
            this.elytraing = false;
            this.elytratime = 40;
        }
        --this.elytratime;
        final boolean flat = ModuleManager.getModuleByName("ElytraFly").isToggled() && ModuleManager.getModuleByName("ElytraFly").getSettings().get(0).toMode().mode == 0;
        if (!flat && this.getSettings().get(3).toToggle().state && this.elytratime > 0 && !this.mc.player.onGround && this.mc.currentScreen != null) {
            this.mc.player.connection.sendPacket(new CPacketEntityAction(this.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            this.timeout = 80;
        }
        if (this.timeout > 0) {
            --this.timeout;
        }
        if (!flat || this.mc.gameSettings.keyBindBack.isKeyDown() || (!flat && this.timeout > 0)) {
            return;
        }
        this.elytraing = true;
        this.elytratime = 0;
        if (this.startChunk == null) {
            this.startChunk = new ChunkPos(this.mc.player.getPosition());
        }
        final int view = 16;
        final int step = 1;
        boolean sorted = false;
        for (int x = -view; x <= view; x += 16) {
            for (int z = -view; z <= view; z += 16) {
                final ChunkPos c = new ChunkPos(this.mc.player.getPosition().add(x, 0, z));
                if (!this.chunks.contains(c)) {
                    this.chunks.add(c);
                    if (this.nextChunks.contains(c)) {
                        this.nextChunks.remove(c);
                        if (!sorted) {
                            this.nextChunks.sort((a, b) -> Double.compare(a.getBlock(8, 0, 8).distanceSq((Vec3i)this.mc.player.getPosition()), b.getBlock(8, 0, 8).distanceSq((Vec3i)this.mc.player.getPosition())));
                            sorted = true;
                        }
                    }
                }
            }
        }
        if (this.nextChunks.isEmpty()) {
            this.chunks.clear();
            this.range += (int)this.getSettings().get(1).toSlider().getValue();
            for (int x = this.startChunk.x - this.range; x <= this.startChunk.x + this.range; ++x) {
                for (int z = this.startChunk.z - this.range; z <= this.startChunk.z + this.range; ++z) {
                    if (Math.abs(x - this.startChunk.x) > this.range - step || Math.abs(z - this.startChunk.z) > this.range - step) {
                        final ChunkPos c = new ChunkPos(x, z);
                        if (!this.chunks.contains(c)) {
                            this.nextChunks.add(c);
                        }
                    }
                }
            }
            this.nextChunks.sort((a, b) -> Double.compare(a.getBlock(8, 0, 8).distanceSq((Vec3i)this.mc.player.getPosition()), b.getBlock(8, 0, 8).distanceSq((Vec3i)this.mc.player.getPosition())));
            return;
        }
        this.nextChunk = this.nextChunks.get(0);
        this.facePos(this.nextChunk.getXStart() + 8, this.nextChunk.getZStart() + 8);
        if (flat) {
            final Vec3d forward = new Vec3d(0.0, 0.0, ModuleManager.getModuleByName("ElytraFly").getSettings().get(4).toSlider().getValue()).rotateYaw(-(float)Math.toRadians(this.mc.player.rotationYaw));
            this.mc.player.setVelocity(forward.x, forward.y, forward.z);
        }
        else {
            this.mc.player.movementInput.forwardKeyDown = true;
            KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), true);
        }
    }
    
    @Override
    public void onRender() {
        if (!this.getSettings().get(2).toToggle().state) {
            return;
        }
        for (final ChunkPos c : this.chunks) {
            RenderUtils.drawFilledBlockBox(new AxisAlignedBB(c.getBlock(0, 0, 0), c.getBlock(16, 0, 16)), 1.0f, 0.0f, 0.0f, 0.3f);
        }
        for (final ChunkPos c : this.nextChunks) {
            RenderUtils.drawFilledBlockBox(new AxisAlignedBB(c.getBlock(0, 0, 0), c.getBlock(16, 0, 16)), 0.0f, 0.0f, 1.0f, 0.3f);
        }
        if (this.nextChunk != null) {
            RenderUtils.drawFilledBlockBox(new AxisAlignedBB(this.nextChunk.getBlock(0, 0, 0), this.nextChunk.getBlock(16, 0, 16)), 0.0f, 1.0f, 0.0f, 0.3f);
        }
    }
    
    public void facePos(final double x, final double z) {
        final double diffX = x - this.mc.player.posX;
        final double diffZ = z - this.mc.player.posZ;
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final EntityPlayerSP player = this.mc.player;
        player.rotationYaw += MathHelper.wrapDegrees(yaw - this.mc.player.rotationYaw);
    }
    
    static {
        settings = Arrays.asList(new SettingMode("Mode: ", "Current", "0, 0"), new SettingSlider(0.0, 10.0, 4.0, 0, "Fly Gap: "), new SettingToggle(false, "Debug"), new SettingToggle(true, "AutoReopen"), new SettingToggle(true, "Shulker Log"), new SettingToggle(true, "Dupe Log"), new SettingToggle(true, "Chest Log"), new SettingToggle(true, "Sign Log"), new SettingToggle(false, "Illegal Log"), new SettingSlider(0.0, 50.0, 20.0, 0, "Min Chest: "), new SettingToggle(true, "Active"));
    }
}
