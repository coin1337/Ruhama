package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import net.minecraft.block.material.*;
import net.minecraft.init.*;
import net.minecraft.util.math.*;
import net.minecraft.client.entity.*;
import bleach.a32k.settings.*;
import java.util.*;

public class ElytraFly extends Module
{
    private static final List<SettingBase> settings;
    
    public ElytraFly() {
        super("ElytraFly", 0, Category.MISC, "Elytra booster", ElytraFly.settings);
    }
    
    @Override
    public void onDisable() {
        this.mc.player.capabilities.isFlying = false;
        this.mc.player.capabilities.setFlySpeed(0.05f);
        if (!this.mc.player.capabilities.isCreativeMode) {
            this.mc.player.capabilities.allowFlying = false;
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.getSettings().get(0).toMode().mode == 0) {
            if (this.mc.player.capabilities.isFlying) {
                this.mc.player.setVelocity(0.0, 0.0, 0.0);
                this.mc.player.setPosition(this.mc.player.posX, this.mc.player.posY - this.getSettings().get(3).toSlider().getValue(), this.mc.player.posZ);
                this.mc.player.capabilities.setFlySpeed((float)this.getSettings().get(4).toSlider().getValue());
                this.mc.player.setSprinting(false);
            }
            if (this.mc.player.onGround && !this.mc.player.capabilities.isCreativeMode) {
                this.mc.player.capabilities.allowFlying = false;
            }
            if (this.mc.player.isElytraFlying()) {
                this.mc.player.capabilities.setFlySpeed(0.915f);
                this.mc.player.capabilities.isFlying = true;
                if (!this.mc.player.capabilities.isCreativeMode) {
                    this.mc.player.capabilities.allowFlying = true;
                }
            }
        }
        else if (this.getSettings().get(0).toMode().mode == 1) {
            if (this.mc.player.isElytraFlying() && this.mc.world.getBlockState(this.mc.player.getPosition().add(0.0, -0.1, 0.0)).getMaterial() instanceof MaterialLiquid && this.mc.world.getBlockState(this.mc.player.getPosition().add(0, 1, 0)).getBlock() == Blocks.AIR && this.mc.player.motionY > 0.0) {
                this.mc.player.addVelocity(0.0, 0.05, 0.0);
            }
            if (!this.mc.player.isElytraFlying() || this.mc.player.motionY > -0.09) {
                return;
            }
            double speed;
            for (speed = Math.abs(this.mc.player.motionX) + Math.abs(this.mc.player.motionY) + Math.abs(this.mc.player.motionZ); speed > this.getSettings().get(1).toSlider().getValue(); speed = Math.abs(this.mc.player.motionX) + Math.abs(this.mc.player.motionY) + Math.abs(this.mc.player.motionZ)) {
                final EntityPlayerSP player = this.mc.player;
                player.motionX *= 0.95;
                final EntityPlayerSP player2 = this.mc.player;
                player2.motionY *= 0.95;
                final EntityPlayerSP player3 = this.mc.player;
                player3.motionZ *= 0.95;
            }
            Vec3d vec3d = new Vec3d(0.0, 0.0, 0.23).rotatePitch(-(float)Math.toRadians(this.mc.player.rotationPitch)).rotateYaw(-(float)Math.toRadians(this.mc.player.rotationYaw));
            if (this.getSettings().get(2).toToggle().state && MathHelper.clamp(speed / 2.0, 0.0, this.getSettings().get(1).toSlider().getValue() - 0.25) < 0.23) {
                vec3d = vec3d.scale(0.2);
            }
            this.mc.player.addVelocity(vec3d.x, vec3d.y, vec3d.z);
        }
    }
    
    static {
        settings = Arrays.asList(new SettingMode("Mode: ", "Flat", "Boost"), new SettingSlider(0.1, 4.0, 1.35, 2, "Boost Max: "), new SettingToggle(true, "Accelerate"), new SettingSlider(1.0E-5, 5.0E-4, 2.0E-4, 5, "Glide: "), new SettingSlider(0.01, 3.0, 1.0, 2, "Flat Speed: "));
    }
}
