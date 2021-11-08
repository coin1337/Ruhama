package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import bleach.a32k.utils.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.*;
import org.lwjgl.opengl.*;
import bleach.a32k.settings.*;
import java.util.*;

public class HopperRadius extends Module
{
    private static final List<SettingBase> settings;
    
    public HopperRadius() {
        super("HopperRadius", 0, Category.RENDER, "Shows the line around hoppers where they close", HopperRadius.settings);
    }
    
    @Override
    public void onRender() {
        RenderUtils.glSetup();
        final double red = this.getSettings().get(0).toSlider().getValue() / 255.0;
        final double green = this.getSettings().get(1).toSlider().getValue() / 255.0;
        final double blue = this.getSettings().get(2).toSlider().getValue() / 255.0;
        for (final TileEntity t : this.mc.world.loadedTileEntityList) {
            if (t instanceof TileEntityHopper) {
                final Vec3d pos = new Vec3d(t.getPos().getX() + 0.5 - RenderUtils.rPos()[0], t.getPos().getY() - RenderUtils.rPos()[1], t.getPos().getZ() + 0.5 - RenderUtils.rPos()[2]);
                if (this.getSettings().get(3).toToggle().state) {
                    GL11.glBegin(9);
                    GL11.glColor4d(red, green, blue, 0.25);
                    for (int i = 0; i <= 360; ++i) {
                        GL11.glVertex3d(pos.x + Math.sin(i * 3.141592653589793 / 180.0) * 7.35, pos.y, pos.z + Math.cos(i * 3.141592653589793 / 180.0) * 7.35);
                    }
                    GL11.glEnd();
                }
                if (!this.getSettings().get(4).toToggle().state) {
                    continue;
                }
                GL11.glBegin(1);
                GL11.glColor4d(red, green, blue, 0.7);
                for (int i = 0; i <= 360; ++i) {
                    GL11.glVertex3d(pos.x + Math.sin(i * 3.141592653589793 / 180.0) * 7.35, pos.y, pos.z + Math.cos(i * 3.141592653589793 / 180.0) * 7.35);
                }
                GL11.glEnd();
            }
        }
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
        RenderUtils.glCleanup();
    }
    
    static {
        settings = Arrays.asList(new SettingSlider(0.0, 255.0, 128.0, 0, "Red: "), new SettingSlider(0.0, 255.0, 128.0, 0, "Green: "), new SettingSlider(0.0, 255.0, 128.0, 0, "Blue: "), new SettingToggle(true, "Fill"), new SettingToggle(true, "Outline"));
    }
}
