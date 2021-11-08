package bleach.a32k.module.modules;

import net.minecraft.entity.*;
import bleach.a32k.module.*;
import bleach.a32k.settings.*;
import net.minecraft.client.particle.*;
import net.minecraft.util.math.*;
import java.util.*;
import bleach.a32k.utils.*;

public class StrengthESP extends Module
{
    private HashMap<Entity, Integer> players;
    private final List<List<Float>> effects;
    
    public StrengthESP() {
        super("StrengthESP", 0, Category.RENDER, "Shows people with strength (only works with particles on)", null);
        this.players = new HashMap<Entity, Integer>();
        this.effects = Arrays.asList(Arrays.asList(0.5764706f, 0.14117648f, 0.13725491f), Arrays.asList(0.48235294f, 0.3137255f, 0.4627451f), Arrays.asList(0.4f, 0.3019608f, 0.4117647f), Arrays.asList(0.65882355f, 0.28627452f, 0.18039216f), Arrays.asList(0.6666667f, 0.34509805f, 0.1764706f), Arrays.asList(0.7137255f, 0.44705883f, 0.2f), Arrays.asList(0.5411765f, 0.3882353f, 0.43137255f), Arrays.asList(0.49019608f, 0.39215687f, 0.38431373f), Arrays.asList(0.72156864f, 0.44313726f, 0.20784314f), Arrays.asList(0.5137255f, 0.3529412f, 0.44705883f), Arrays.asList(0.4509804f, 0.3529412f, 0.39607844f), Arrays.asList(0.69803923f, 0.38039216f, 0.19607843f));
    }
    
    @Override
    public void onUpdate() {
        if (this.mc.currentScreen != null) {
            return;
        }
        for (final Map.Entry<Entity, Integer> e : new HashMap<Entity, Integer>(this.players).entrySet()) {
            if (e.getValue() <= 0) {
                this.players.remove(e.getKey());
            }
            else {
                this.players.replace(e.getKey(), e.getValue() - 1);
            }
        }
        if (this.mc.world.playerEntities.size() <= 1) {
            return;
        }
        try {
            int count = 0;
            int playerCount = 0;
            for (final ArrayDeque[] array2 : (ArrayDeque[][])ReflectUtils.getField(ParticleManager.class, "fxLayers", "fxLayers").get(this.mc.effectRenderer)) {
                final ArrayDeque<Particle>[] p2 = (ArrayDeque<Particle>[])array2;
                for (final ArrayDeque<Particle> p3 : array2) {
                    for (final Particle p4 : p3) {
                        if (p4 != null) {
                            if (!(p4 instanceof ParticleSpell)) {
                                continue;
                            }
                            if (count > 250) {
                                return;
                            }
                            ++count;
                            final Vec3d pos = new Vec3d((double)ReflectUtils.getField(Particle.class, "posX", "posX").get(p4), (double)ReflectUtils.getField(Particle.class, "posY", "posY").get(p4), (double)ReflectUtils.getField(Particle.class, "posZ", "posZ").get(p4));
                            for (final Entity e2 : this.mc.world.playerEntities) {
                                if (e2 != this.mc.player) {
                                    if (this.players.containsKey(e2)) {
                                        continue;
                                    }
                                    if (pos.distanceTo(e2.getPositionVector()) >= 2.0 || !this.effects.contains(Arrays.asList(p4.getRedColorF(), p4.getGreenColorF(), p4.getBlueColorF()))) {
                                        continue;
                                    }
                                    this.players.put(e2, 10);
                                    if (++playerCount >= this.mc.world.playerEntities.size() - 1) {
                                        return;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e3) {
            e3.printStackTrace();
        }
    }
    
    @Override
    public void onOverlay() {
        int c = Gui.arrayListEnd + 15;
        for (final Map.Entry<Entity, Integer> e : this.players.entrySet()) {
            this.mc.fontRenderer.drawStringWithShadow(e.getKey().getName(), 2.0f, (float)c, 12591136);
            c += 10;
        }
        if (c != Gui.arrayListEnd + 15) {
            this.mc.fontRenderer.drawStringWithShadow("Strength Players: ", 2.0f, (float)(Gui.arrayListEnd + 5), 14688288);
        }
        Gui.arrayListEnd = c;
    }
    
    @Override
    public void onRender() {
        for (final Map.Entry<Entity, Integer> e : this.players.entrySet()) {
            RenderUtils.drawFilledBlockBox(e.getKey().getEntityBoundingBox(), 1.0f, 0.0f, 0.0f, 0.3f);
        }
    }
}
