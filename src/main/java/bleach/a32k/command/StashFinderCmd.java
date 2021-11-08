package bleach.a32k.command;

import net.minecraftforge.client.*;
import net.minecraft.command.*;
import net.minecraft.server.*;
import bleach.a32k.utils.*;
import bleach.a32k.module.modules.*;
import bleach.a32k.module.*;
import net.minecraft.util.math.*;

public class StashFinderCmd extends CommandBase implements IClientCommand
{
    public boolean allowUsageWithoutPrefix(final ICommandSender sender, final String message) {
        return false;
    }
    
    public String getName() {
        return "stashfinder";
    }
    
    public String getUsage(final ICommandSender sender) {
        return null;
    }
    
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) {
        if (args.length != 2) {
            RuhamaLogger.log("Invalid number of arguments, use /stashfinder x z");
            return;
        }
        try {
            ((StashFinder)ModuleManager.getModuleByName("StashFinder")).startChunk = new ChunkPos(Integer.parseInt(args[0]) >> 4, Integer.parseInt(args[1]) >> 4);
            RuhamaLogger.log("Set stashfinder start to: " + args[0] + ", " + args[1]);
        }
        catch (Exception e) {
            RuhamaLogger.log("wrong, /stashfinder x z");
        }
    }
    
    public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
        return true;
    }
}
