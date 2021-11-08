package bleach.a32k.command;

import net.minecraftforge.client.*;
import net.minecraft.command.*;
import net.minecraft.server.*;
import net.minecraft.client.*;
import bleach.a32k.utils.*;

public class InvSorterCmd extends CommandBase implements IClientCommand
{
    public boolean allowUsageWithoutPrefix(final ICommandSender sender, final String message) {
        return false;
    }
    
    public String getName() {
        return "invsorter";
    }
    
    public String getUsage(final ICommandSender sender) {
        return null;
    }
    
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) {
        String s = "";
        for (int i = 0; i <= 9; ++i) {
            s = s + Minecraft.getMinecraft().player.inventory.getStackInSlot(i).getItem().getRegistryName().toString() + "\n";
        }
        FileMang.createEmptyFile("invsorter.txt");
        FileMang.appendFile(s, "invsorter.txt");
        RuhamaLogger.log("Saved Inventory");
    }
    
    public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
        return true;
    }
}
