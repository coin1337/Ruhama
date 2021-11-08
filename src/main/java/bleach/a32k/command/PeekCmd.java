package bleach.a32k.command;

import net.minecraft.entity.item.*;
import net.minecraftforge.event.entity.*;
import net.minecraft.entity.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.*;
import bleach.a32k.module.*;
import bleach.a32k.settings.*;
import net.minecraft.item.*;
import net.minecraft.client.*;
import net.minecraft.util.text.*;
import net.minecraft.inventory.*;
import net.minecraft.nbt.*;
import net.minecraftforge.client.*;
import net.minecraft.command.*;
import net.minecraft.server.*;
import net.minecraft.tileentity.*;
import net.minecraft.init.*;

public class PeekCmd
{
    public static int metadataTicks;
    public static int guiTicks;
    public static ItemStack shulker;
    public static EntityItem drop;
    public static InventoryBasic toOpen;
    
    @SubscribeEvent
    public void onEntitySpawn(final EntityJoinWorldEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof EntityItem) {
            PeekCmd.drop = (EntityItem)entity;
            PeekCmd.metadataTicks = 0;
        }
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (!ModuleManager.getModuleByName("Peek").isToggled() || !ModuleManager.getModuleByName("Peek").getSettings().get(2).toToggle().state) {
            return;
        }
        if (event.phase == TickEvent.Phase.END) {
            if (PeekCmd.guiTicks > -1) {
                ++PeekCmd.guiTicks;
            }
            if (PeekCmd.metadataTicks > -1) {
                ++PeekCmd.metadataTicks;
            }
        }
        if (PeekCmd.metadataTicks == 20) {
            PeekCmd.metadataTicks = -1;
            if (PeekCmd.drop.getItem().getItem() instanceof ItemShulkerBox) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString("New shulker found! use /peek to view its content " + TextFormatting.GREEN + "(" + PeekCmd.drop.getItem().getDisplayName() + ")"));
                PeekCmd.shulker = PeekCmd.drop.getItem();
            }
        }
        if (PeekCmd.guiTicks == 20) {
            PeekCmd.guiTicks = -1;
            Minecraft.getMinecraft().player.displayGUIChest(PeekCmd.toOpen);
        }
    }
    
    public static NBTTagCompound getShulkerNBT(final ItemStack stack) {
        final NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey("BlockEntityTag", 10)) {
            final NBTTagCompound tags = compound.getCompoundTag("BlockEntityTag");
            if (tags.hasKey("Items", 9)) {
                return tags;
            }
        }
        return null;
    }
    
    static {
        PeekCmd.metadataTicks = -1;
        PeekCmd.guiTicks = -1;
        PeekCmd.shulker = ItemStack.EMPTY;
    }
    
    public static class PeekCommand extends CommandBase implements IClientCommand
    {
        public boolean allowUsageWithoutPrefix(final ICommandSender sender, final String message) {
            return false;
        }
        
        public String getName() {
            return "peek";
        }
        
        public String getUsage(final ICommandSender sender) {
            return null;
        }
        
        public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) {
            if (!ModuleManager.getModuleByName("Peek").isToggled() || !ModuleManager.getModuleByName("Peek").getSettings().get(2).toToggle().state) {
                return;
            }
            if (!PeekCmd.shulker.isEmpty()) {
                final NBTTagCompound shulkerNBT = PeekCmd.getShulkerNBT(PeekCmd.shulker);
                final TileEntityShulkerBox fakeShulker = new TileEntityShulkerBox();
                String customName = "container.shulkerBox";
                boolean hasCustomName = false;
                if (shulkerNBT != null) {
                    fakeShulker.loadFromNbt(shulkerNBT);
                    if (shulkerNBT.hasKey("CustomName", 8)) {
                        customName = shulkerNBT.getString("CustomName");
                        hasCustomName = true;
                    }
                }
                final InventoryBasic inv = new InventoryBasic(customName, hasCustomName, 27);
                for (int i = 0; i < 27; ++i) {
                    final ItemStack stack = fakeShulker.getStackInSlot(i);
                    inv.setInventorySlotContents(i, (stack == null) ? new ItemStack(Items.AIR) : stack);
                }
                PeekCmd.toOpen = inv;
                PeekCmd.guiTicks = 0;
            }
            else {
                sender.sendMessage(new TextComponentString("No shulker detected! please drop and pickup your shulker."));
            }
        }
        
        public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
            return true;
        }
    }
}
