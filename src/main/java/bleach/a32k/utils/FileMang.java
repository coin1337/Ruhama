package bleach.a32k.utils;

import net.minecraft.client.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.*;
import bleach.a32k.module.*;
import bleach.a32k.settings.*;
import java.util.*;
import bleach.a32k.module.modules.*;
import org.apache.commons.lang3.tuple.*;
import bleach.a32k.gui.*;

public class FileMang
{
    private static Path dir;
    
    public static void init() {
        FileMang.dir = Paths.get(Minecraft.getMinecraft().gameDir.getPath(), "bleach", "ruhama/");
        if (!FileMang.dir.toFile().exists()) {
            FileMang.dir.toFile().mkdirs();
        }
    }
    
    public static Path getDir() {
        return FileMang.dir;
    }
    
    public static List<String> readFileLines(final String... file) {
        try {
            return Files.readAllLines(stringsToPath(file));
        }
        catch (IOException e) {
            System.out.println("Error Reading File: " + stringsToPath(file));
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }
    
    public static void createFile(final String... file) {
        try {
            if (fileExists(file)) {
                return;
            }
            FileMang.dir.toFile().mkdirs();
            Files.createFile(stringsToPath(file), new FileAttribute[0]);
        }
        catch (IOException e) {
            System.out.println("Error Creating File: " + file);
            e.printStackTrace();
        }
    }
    
    public static void createEmptyFile(final String... file) {
        try {
            FileMang.dir.toFile().mkdirs();
            if (!fileExists(file)) {
                Files.createFile(stringsToPath(file), new FileAttribute[0]);
            }
            final FileWriter writer = new FileWriter(stringsToPath(file).toFile());
            writer.write("");
            writer.close();
        }
        catch (IOException e) {
            System.out.println("Error Clearing/Creating File: " + file);
            e.printStackTrace();
        }
    }
    
    public static void appendFile(final String content, final String... file) {
        try {
            final FileWriter writer = new FileWriter(stringsToPath(file).toFile(), true);
            writer.write(content + "\n");
            writer.close();
        }
        catch (IOException e) {
            System.out.println("Error Appending File: " + file);
            e.printStackTrace();
        }
    }
    
    public static boolean fileExists(final String... file) {
        try {
            return stringsToPath(file).toFile().exists();
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public static void deleteFile(final String... file) {
        try {
            Files.deleteIfExists(stringsToPath(file));
        }
        catch (Exception e) {
            System.out.println("Error Deleting File: " + file);
            e.printStackTrace();
        }
    }
    
    public static Path stringsToPath(final String... strings) {
        Path path = FileMang.dir;
        for (final String s : strings) {
            path = path.resolve(s);
        }
        return path;
    }
    
    public static void saveSettings() {
        createEmptyFile("settings.txt");
        String lines = "";
        for (final Module m : ModuleManager.getModules()) {
            String line = m.getName();
            int count = 0;
            for (final SettingBase set : m.getSettings()) {
                if (set instanceof SettingSlider) {
                    line = line + ":" + m.getSettings().get(count).toSlider().getValue();
                }
                if (set instanceof SettingMode) {
                    line = line + ":" + m.getSettings().get(count).toMode().mode;
                }
                if (set instanceof SettingToggle) {
                    line = line + ":" + m.getSettings().get(count).toToggle().state;
                }
                ++count;
            }
            lines = lines + line + "\n";
        }
        appendFile(lines, "settings.txt");
    }
    
    public static void readSettings() {
        final List<String> lines = readFileLines("settings.txt");
        for (final Module m : ModuleManager.getModules()) {
            for (final String s : lines) {
                final String[] line = s.split(":");
                if (!line[0].startsWith(m.getName())) {
                    continue;
                }
                int count = 0;
                for (final SettingBase set : m.getSettings()) {
                    try {
                        if (set instanceof SettingSlider) {
                            m.getSettings().get(count).toSlider().value = Double.parseDouble(line[count + 1]);
                        }
                        if (set instanceof SettingMode) {
                            m.getSettings().get(count).toMode().mode = Integer.parseInt(line[count + 1]);
                        }
                        if (set instanceof SettingToggle) {
                            m.getSettings().get(count).toToggle().state = Boolean.parseBoolean(line[count + 1]);
                        }
                    }
                    catch (Exception ex) {}
                    ++count;
                }
            }
        }
    }
    
    public static void saveClickGui() {
        createEmptyFile("clickgui.txt");
        String text = "";
        for (final ModuleWindow w : ClickGui.clickGui.tabs) {
            text = text + w.getPos()[0] + ":" + w.getPos()[1] + "\n";
        }
        appendFile(text, "clickgui.txt");
        createEmptyFile("clickguitext.txt");
        String text2 = "";
        for (final MutableTriple<Module, Integer, TextWindow> e : NewRuhamaGui.textWins) {
            text2 = text2 + e.left.getName() + ":" + e.middle + ":" + e.getRight().posX + ":" + e.getRight().posY + "\n";
        }
        appendFile(text2, "clickguitext.txt");
    }
    
    public static void readClickGui() {
        final List<String> lines = readFileLines("clickgui.txt");
        try {
            int c = 0;
            for (final ModuleWindow w : ClickGui.clickGui.tabs) {
                w.setPos(Integer.parseInt(lines.get(c).split(":")[0]), Integer.parseInt(lines.get(c).split(":")[1]));
                ++c;
            }
        }
        catch (Exception ex) {}
        for (final String s : readFileLines("clickguitext.txt")) {
            final String[] split = s.split(":");
            for (final MutableTriple<Module, Integer, TextWindow> e : NewRuhamaGui.textWins) {
                try {
                    if (!e.left.getName().equals(split[0]) || !e.middle.equals(Integer.parseInt(split[1]))) {
                        continue;
                    }
                    e.right.posX = Integer.parseInt(split[2]);
                    e.right.posY = Integer.parseInt(split[3]);
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    public static void saveModules() {
        createEmptyFile("modules.txt");
        String lines = "";
        for (final Module m : ModuleManager.getModules()) {
            if (m.getName() != "ClickGui") {
                if (m.getName() == "Freecam") {
                    continue;
                }
                lines = lines + m.getName() + ":" + m.isToggled() + "\n";
            }
        }
        appendFile(lines, "modules.txt");
    }
    
    public static void readModules() {
        final List<String> lines = readFileLines("modules.txt");
        for (final Module m : ModuleManager.getModules()) {
            for (final String s : lines) {
                final String[] line = s.split(":");
                try {
                    if (line[0].contains(m.getName()) && line[1].contains("true")) {
                        m.toggle();
                        break;
                    }
                    continue;
                }
                catch (Exception ex) {}
            }
        }
    }
    
    public static void saveBinds() {
        createEmptyFile("binds.txt");
        String lines = "";
        for (final Module m : ModuleManager.getModules()) {
            lines = lines + m.getName() + ":" + m.getKey().getKeyCode() + "\n";
        }
        appendFile(lines, "binds.txt");
    }
    
    public static void readBinds() {
        final List<String> lines = readFileLines("binds.txt");
        for (final Module m : ModuleManager.getModules()) {
            for (final String s : lines) {
                final String[] line = s.split(":");
                if (!line[0].startsWith(m.getName())) {
                    continue;
                }
                try {
                    m.getKey().setKeyCode(Integer.parseInt(line[line.length - 1]));
                }
                catch (Exception ex) {}
            }
        }
    }
}
