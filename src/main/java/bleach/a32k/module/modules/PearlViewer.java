package bleach.a32k.module.modules;

import net.minecraft.util.math.*;
import bleach.a32k.module.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import bleach.a32k.utils.*;
import org.lwjgl.opengl.*;
import java.util.*;
import bleach.a32k.settings.*;

public class PearlViewer extends Module
{
    private static final List<SettingBase> settings;
    private HashMap<UUID, List<Vec3d>> poses;
    private HashMap<UUID, Double> time;
    
    public PearlViewer() {
        super("PearlViewer", 0, Category.RENDER, "Shows Where Enderpearls Are Going", PearlViewer.settings);
        this.poses = new HashMap<UUID, List<Vec3d>>();
        this.time = new HashMap<UUID, Double>();
    }
    
    @Override
    public void onUpdate() {
        for (final Map.Entry<UUID, Double> e : new HashMap<UUID, Double>(this.time).entrySet()) {
            if (e.getValue() <= 0.0) {
                this.poses.remove(e.getKey());
                this.time.remove(e.getKey());
            }
            else {
                this.time.replace(e.getKey(), e.getValue() - 0.05);
            }
        }
        for (final Entity e2 : this.mc.world.loadedEntityList) {
            if (e2 instanceof EntityEnderPearl) {
                if (!this.poses.containsKey(e2.getUniqueID())) {
                    if (this.getSettings().get(0).toToggle().state) {
                        for (final Entity e3 : this.mc.world.playerEntities) {
                            if (e3.getDistance(e2) < 4.0f && e3.getName() != this.mc.player.getName()) {
                                RuhamaLogger.log(e3.getName() + " Threw a pearl");
                                break;
                            }
                        }
                    }
                    this.poses.put(e2.getUniqueID(), new ArrayList<Vec3d>(Arrays.asList(e2.getPositionVector())));
                    this.time.put(e2.getUniqueID(), this.getSettings().get(2).toSlider().getValue());
                }
                else {
                    this.time.replace(e2.getUniqueID(), this.getSettings().get(2).toSlider().getValue());
                    final List<Vec3d> v = this.poses.get(e2.getUniqueID());
                    v.add(e2.getPositionVector());
                }
            }
        }
    }
    
    @Override
    public void onRender() {
        if (!this.getSettings().get(1).toToggle().state) {
            return;
        }
        RenderUtils.glSetup();
        GL11.glLineWidth((float)this.getSettings().get(3).toSlider().getValue());
        for (final Map.Entry<UUID, List<Vec3d>> e : this.poses.entrySet()) {
            if (e.getValue().size() <= 2) {
                continue;
            }
            GL11.glBegin(1);
            final Random rand = new Random(e.getKey().hashCode());
            final double r = 0.5 + rand.nextDouble() / 2.0;
            final double g = 0.5 + rand.nextDouble() / 2.0;
            final double b = 0.5 + rand.nextDouble() / 2.0;
            GL11.glColor3d(r, g, b);
            final double[] rPos = RenderUtils.rPos();
            for (int i = 1; i < e.getValue().size(); ++i) {
                GL11.glVertex3d(e.getValue().get(i).x - rPos[0], e.getValue().get(i).y - rPos[1], e.getValue().get(i).z - rPos[2]);
                GL11.glVertex3d(e.getValue().get(i - 1).x - rPos[0], e.getValue().get(i - 1).y - rPos[1], e.getValue().get(i - 1).z - rPos[2]);
            }
            GL11.glEnd();
        }
        RenderUtils.glCleanup();
    }
    
    static {
        settings = Arrays.asList(new SettingToggle(true, "Chat"), new SettingToggle(true, "Render"), new SettingSlider(0.0, 20.0, 5.0, 1, "Render Time: "), new SettingSlider(0.0, 10.0, 3.5, 2, "Thick: "));
    }
}
