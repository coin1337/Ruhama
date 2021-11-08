package bleach.a32k.utils;

import net.minecraft.client.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;

public class RenderUtils
{
    private static Minecraft mc;
    
    public static double[] rPos() {
        try {
            return new double[] { (double)ReflectUtils.getField(RenderManager.class, "renderPosX", "renderPosX").get(RenderUtils.mc.getRenderManager()), (double)ReflectUtils.getField(RenderManager.class, "renderPosY", "renderPosY").get(RenderUtils.mc.getRenderManager()), (double)ReflectUtils.getField(RenderManager.class, "renderPosZ", "renderPosZ").get(RenderUtils.mc.getRenderManager()) };
        }
        catch (Exception e) {
            return new double[] { 0.0, 0.0, 0.0 };
        }
    }
    
    public static void drawFilledBlockBox(AxisAlignedBB box, final float r, final float g, final float b, final float a) {
        try {
            glSetup();
            final double[] rPos = rPos();
            box = new AxisAlignedBB(box.minX - rPos[0], box.minY - rPos[1], box.minZ - rPos[2], box.maxX - rPos[0], box.maxY - rPos[1], box.maxZ - rPos[2]);
            RenderGlobal.renderFilledBox(box, r, g, b, a);
            RenderGlobal.drawSelectionBoundingBox(box, r, g, b, a * 1.5f);
            glCleanup();
        }
        catch (Exception ex) {}
    }
    
    public static void glSetup() {
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(2.0f);
    }
    
    public static void glCleanup() {
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    static {
        RenderUtils.mc = Minecraft.getMinecraft();
    }
}
