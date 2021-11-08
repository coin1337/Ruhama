package bleach.a32k.command;

import net.minecraftforge.client.*;
import net.minecraft.command.*;
import net.minecraft.server.*;
import java.net.*;
import com.mojang.authlib.yggdrasil.*;
import com.mojang.authlib.*;
import net.minecraft.client.*;
import bleach.a32k.utils.*;
import net.minecraft.util.*;

public class LoginCmd extends CommandBase implements IClientCommand
{
    public boolean allowUsageWithoutPrefix(final ICommandSender sender, final String message) {
        return false;
    }
    
    public String getName() {
        return "ruhamalogin";
    }
    
    public String getUsage(final ICommandSender sender) {
        return null;
    }
    
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) {
        try {
            if (this.login(args[0], args[1]) == "") {
                RuhamaLogger.log("Logged in");
            }
            else {
                RuhamaLogger.log("Invalid login");
            }
        }
        catch (Exception ex) {}
    }
    
    public String login(final String email, final String password) {
        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(email);
        auth.setPassword(password);
        try {
            auth.logIn();
            ReflectUtils.getField(Minecraft.class, "session", "session").set(Minecraft.getMinecraft(), new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang"));
            return "";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "\u00ef¿½4\u00ef¿½loops!";
        }
    }
    
    public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
        return true;
    }
}
