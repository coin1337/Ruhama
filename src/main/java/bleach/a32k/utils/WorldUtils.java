package bleach.a32k.utils;

import net.minecraft.client.*;
import net.minecraft.block.*;
import net.minecraft.util.*;
import net.minecraft.network.*;
import net.minecraft.network.play.client.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.client.entity.*;
import net.minecraft.init.*;
import java.util.*;

public class WorldUtils
{
    private static Minecraft mc;
    public static List<Block> emptyBlocks;
    public static List<Block> rightclickableBlocks;
    
    public static void openBlock(final BlockPos pos) {
        for (final EnumFacing f : EnumFacing.values()) {
            final Block neighborBlock = WorldUtils.mc.world.getBlockState(pos.offset(f)).getBlock();
            if (WorldUtils.emptyBlocks.contains(neighborBlock)) {
                WorldUtils.mc.playerController.processRightClickBlock(WorldUtils.mc.player, WorldUtils.mc.world, pos, f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
                return;
            }
        }
    }
    
    public static boolean placeBlock(final BlockPos pos, final int slot, final boolean rotate, final boolean rotateBack) {
        if (!isBlockEmpty(pos)) {
            return false;
        }
        if (slot != WorldUtils.mc.player.inventory.currentItem) {
            WorldUtils.mc.player.inventory.currentItem = slot;
        }
        for (final EnumFacing f : EnumFacing.values()) {
            final Block neighborBlock = WorldUtils.mc.world.getBlockState(pos.offset(f)).getBlock();
            final Vec3d vec = new Vec3d(pos.getX() + 0.5 + f.getXOffset() * 0.5, pos.getY() + 0.5 + f.getYOffset() * 0.5, pos.getZ() + 0.5 + f.getZOffset() * 0.5);
            if (!WorldUtils.emptyBlocks.contains(neighborBlock) && WorldUtils.mc.player.getPositionEyes(WorldUtils.mc.getRenderPartialTicks()).distanceTo(vec) <= 4.25) {
                final float[] rot = { WorldUtils.mc.player.rotationYaw, WorldUtils.mc.player.rotationPitch };
                if (rotate) {
                    rotatePacket(vec.x, vec.y, vec.z);
                }
                if (WorldUtils.rightclickableBlocks.contains(neighborBlock)) {
                    WorldUtils.mc.player.connection.sendPacket(new CPacketEntityAction(WorldUtils.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }
                WorldUtils.mc.playerController.processRightClickBlock(WorldUtils.mc.player, WorldUtils.mc.world, pos.offset(f), f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
                if (WorldUtils.rightclickableBlocks.contains(neighborBlock)) {
                    WorldUtils.mc.player.connection.sendPacket(new CPacketEntityAction(WorldUtils.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
                if (rotateBack) {
                    WorldUtils.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], WorldUtils.mc.player.onGround));
                }
                return true;
            }
        }
        return false;
    }
    
    public static boolean isBlockEmpty(final BlockPos pos) {
        if (!WorldUtils.emptyBlocks.contains(WorldUtils.mc.world.getBlockState(pos).getBlock())) {
            return false;
        }
        final AxisAlignedBB box = new AxisAlignedBB(pos);
        for (final Entity e : WorldUtils.mc.world.loadedEntityList) {
            if (e instanceof EntityLivingBase && box.intersects(e.getEntityBoundingBox())) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean canPlaceBlock(final BlockPos pos) {
        if (!isBlockEmpty(pos)) {
            return false;
        }
        for (final EnumFacing f : EnumFacing.values()) {
            if (!WorldUtils.emptyBlocks.contains(WorldUtils.mc.world.getBlockState(pos.offset(f)).getBlock()) && WorldUtils.mc.player.getPositionEyes(WorldUtils.mc.getRenderPartialTicks()).distanceTo(new Vec3d(pos.getX() + 0.5 + f.getXOffset() * 0.5, pos.getY() + 0.5 + f.getYOffset() * 0.5, pos.getZ() + 0.5 + f.getZOffset() * 0.5)) <= 4.25) {
                return true;
            }
        }
        return false;
    }
    
    public static EnumFacing getClosestFacing(final BlockPos pos) {
        return EnumFacing.DOWN;
    }
    
    public static void rotateClient(final double x, final double y, final double z) {
        final double diffX = x - WorldUtils.mc.player.posX;
        final double diffY = y - (WorldUtils.mc.player.posY + WorldUtils.mc.player.getEyeHeight());
        final double diffZ = z - WorldUtils.mc.player.posZ;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        final EntityPlayerSP player = WorldUtils.mc.player;
        player.rotationYaw += MathHelper.wrapDegrees(yaw - WorldUtils.mc.player.rotationYaw);
        final EntityPlayerSP player2 = WorldUtils.mc.player;
        player2.rotationPitch += MathHelper.wrapDegrees(pitch - WorldUtils.mc.player.rotationPitch);
    }
    
    public static void rotatePacket(final double x, final double y, final double z) {
        final double diffX = x - WorldUtils.mc.player.posX;
        final double diffY = y - (WorldUtils.mc.player.posY + WorldUtils.mc.player.getEyeHeight());
        final double diffZ = z - WorldUtils.mc.player.posZ;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        WorldUtils.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, WorldUtils.mc.player.onGround));
    }
    
    static {
        WorldUtils.mc = Minecraft.getMinecraft();
        WorldUtils.emptyBlocks = Arrays.asList(Blocks.AIR, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.VINE, Blocks.SNOW_LAYER, Blocks.TALLGRASS, Blocks.FIRE);
        WorldUtils.rightclickableBlocks = Arrays.asList(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.ANVIL, Blocks.WOODEN_BUTTON, Blocks.STONE_BUTTON, Blocks.UNPOWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.POWERED_COMPARATOR, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.BREWING_STAND, Blocks.DISPENSER, Blocks.DROPPER, Blocks.LEVER, Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.BEACON, Blocks.BED, Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR, Blocks.CAKE, Blocks.ENCHANTING_TABLE, Blocks.DRAGON_EGG, Blocks.HOPPER, Blocks.REPEATING_COMMAND_BLOCK, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.CRAFTING_TABLE);
    }
}
