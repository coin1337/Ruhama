package bleach.a32k.utils;

import net.minecraft.client.*;
import net.minecraft.util.text.*;

public class RuhamaLogger
{
    public static void log(final String text) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(TextFormatting.RED + "Ruhama: " + TextFormatting.RESET + text));
    }
}
