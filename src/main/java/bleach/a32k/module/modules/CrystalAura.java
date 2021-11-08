package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import java.util.stream.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.client.entity.*;
import bleach.a32k.utils.*;
import net.minecraft.init.*;
import java.util.function.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.enchantment.*;
import net.minecraft.util.math.*;
import net.minecraft.potion.*;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.monster.*;
import bleach.a32k.settings.*;
import java.util.*;

public class CrystalAura extends Module
{
    private static final List<SettingBase> settings;
    private BlockPos render;
    private Entity renderEnt;
    private long systemTime;
    private boolean togglePitch;
    private boolean isSpoofingAngles;
    private long placeDelay;
    
    public CrystalAura() {
        super("CrystalAura", 0, Category.COMBAT, "Attacks Crystals", CrystalAura.settings);
        this.systemTime = -1L;
        this.togglePitch = false;
        this.placeDelay = 0L;
    }
    
    @Override
    public void onUpdate() {
        final EntityEnderCrystal crystal = (EntityEnderCrystal)this.mc.world.loadedEntityList.stream().filter(entityx -> entityx instanceof EntityEnderCrystal).map(entityx -> entityx).min(Comparator.comparing(c -> this.mc.player.getDistance(c))).orElse(null);
        if (crystal != null && this.mc.player.getDistance(crystal) <= this.getSettings().get(7).toSlider().getValue()) {
            if (System.nanoTime() / 1000000L - this.systemTime >= 250L) {
                this.mc.playerController.attackEntity(this.mc.player, crystal);
                this.mc.player.swingArm(EnumHand.MAIN_HAND);
                this.systemTime = System.nanoTime() / 1000000L;
            }
            return;
        }
        this.resetRotation();
        int crystalSlot = (this.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) ? this.mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (this.mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                    crystalSlot = l;
                    break;
                }
            }
        }
        boolean offhand = false;
        if (this.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        }
        else if (crystalSlot == -1) {
            return;
        }
        final List<BlockPos> blocks = this.findCrystalBlocks();
        final List<Entity> entities = this.mc.world.loadedEntityList.stream().filter(e -> (e instanceof EntityPlayer && this.getSettings().get(1).toToggle().state) || (e.isCreatureType(EnumCreatureType.MONSTER, false) && this.getSettings().get(2).toToggle().state) || (this.isPassive(e) && this.getSettings().get(3).toToggle().state)).collect(Collectors.toList());
        BlockPos q = this.mc.player.getPosition().add(0, 2, 0);
        double damage = 0.5;
        for (final Entity entity : entities) {
            if (entity != this.mc.player && ((EntityLivingBase)entity).getHealth() > 0.0f) {
                for (final BlockPos blockPos : blocks) {
                    final double b = entity.getDistanceSq(blockPos);
                    if (b < 169.0) {
                        final double d = this.calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, entity);
                        if (d <= damage) {
                            continue;
                        }
                        final double self = this.calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, this.mc.player);
                        if (self > d && d >= ((EntityLivingBase)entity).getHealth() && !this.getSettings().get(5).toToggle().state) {
                            continue;
                        }
                        if (self - 0.5 > this.mc.player.getHealth() && !this.getSettings().get(5).toToggle().state) {
                            continue;
                        }
                        damage = d;
                        q = blockPos;
                        this.renderEnt = entity;
                    }
                }
            }
        }
        if (damage == 0.5) {
            this.render = null;
            this.renderEnt = null;
            this.resetRotation();
            return;
        }
        this.render = q;
        if (this.getSettings().get(4).toToggle().state && (this.getSettings().get(0).toToggle().state || this.mc.player.inventory.getCurrentItem().getItem() == Items.END_CRYSTAL) && this.placeDelay + this.getSettings().get(8).toSlider().getValue() < System.currentTimeMillis()) {
            this.placeDelay = System.currentTimeMillis();
            if (!offhand && this.mc.player.inventory.currentItem != crystalSlot) {
                if (this.getSettings().get(0).toToggle().state) {
                    this.mc.player.inventory.currentItem = crystalSlot;
                    this.resetRotation();
                }
                return;
            }
            this.lookAtPacket(q.getX() + 0.5, q.getY() - 0.5, q.getZ() + 0.5, this.mc.player);
            final RayTraceResult result = this.mc.world.rayTraceBlocks(new Vec3d(this.mc.player.posX, this.mc.player.posY + this.mc.player.getEyeHeight(), this.mc.player.posZ), new Vec3d(q.getX() + 0.5, q.getY() - 0.5, q.getZ() + 0.5));
            EnumFacing f;
            if (result != null && result.sideHit != null) {
                f = result.sideHit;
            }
            else {
                f = EnumFacing.UP;
            }
            this.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        }
        if (this.isSpoofingAngles) {
            if (this.togglePitch) {
                final EntityPlayerSP var11 = this.mc.player;
                var11.rotationPitch += (float)4.0E-4;
                this.togglePitch = false;
            }
            else {
                final EntityPlayerSP var11 = this.mc.player;
                var11.rotationPitch -= (float)4.0E-4;
                this.togglePitch = true;
            }
        }
    }
    
    @Override
    public void onRender() {
        if (this.render != null) {
            RenderUtils.drawFilledBlockBox(new AxisAlignedBB(this.render), 1.0f, 1.0f, 1.0f, 0.3f);
            if (this.renderEnt != null) {}
        }
    }
    
    private void lookAtPacket(final double px, final double py, final double pz, final EntityPlayer me) {
        final double[] v = this.calculateLookAt(px, py, pz, me);
        this.setYawAndPitch((float)v[0], (float)v[1]);
    }
    
    private boolean canPlaceCrystal(final BlockPos blockPos) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        return (this.mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || this.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && this.mc.world.getBlockState(boost).getBlock() == Blocks.AIR && this.mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && this.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost)).isEmpty();
    }
    
    public BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(this.mc.player.posX), Math.floor(this.mc.player.posY), Math.floor(this.mc.player.posZ));
    }
    
    private List<BlockPos> findCrystalBlocks() {
        final NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(this.getSphere(this.getPlayerPos(), (float)this.getSettings().get(6).toSlider().getValue(), (int)this.getSettings().get(6).toSlider().getValue(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }
    
    public List<BlockPos> getSphere(final BlockPos loc, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final List<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = loc.getX();
        final int cy = loc.getY();
        final int cz = loc.getZ();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                for (int y = sphere ? (cy - (int)r) : cy; y < (sphere ? (cy + r) : ((float)(cy + h))); ++y) {
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        final BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }
    
    public float calculateDamage(final double posX, final double posY, final double posZ, final Entity entity) {
        final float doubleExplosionSize = 12.0f;
        final double distancedsize = entity.getDistance(posX, posY, posZ) / doubleExplosionSize;
        final Vec3d vec3d = new Vec3d(posX, posY, posZ);
        final double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        final double v = (1.0 - distancedsize) * blockDensity;
        final float damage = (float)(int)((v * v + v) / 2.0 * 7.0 * doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = this.getBlastReduction((EntityLivingBase)entity, this.getDamageMultiplied(damage), new Explosion(this.mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float)finald;
    }
    
    public float getBlastReduction(final EntityLivingBase entity, float damage, final Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            final EntityPlayer ep = (EntityPlayer)entity;
            final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float)ep.getTotalArmorValue(), (float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            final int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            final float f = MathHelper.clamp((float)k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(Potion.getPotionById(11))) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage - ep.getAbsorptionAmount(), 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float)entity.getTotalArmorValue(), (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }
    
    private float getDamageMultiplied(final float damage) {
        final int diff = this.mc.world.getDifficulty().getId();
        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }
    
    public float calculateDamage(final EntityEnderCrystal crystal, final Entity entity) {
        return this.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }
    
    private void setYawAndPitch(final float yaw1, final float pitch1) {
        this.isSpoofingAngles = true;
    }
    
    private void resetRotation() {
        if (this.isSpoofingAngles) {
            this.isSpoofingAngles = false;
        }
    }
    
    @Override
    public void onDisable() {
        this.render = null;
        this.renderEnt = null;
        this.resetRotation();
    }
    
    public boolean isPassive(final Entity e) {
        return (!(e instanceof EntityWolf) || !((EntityWolf)e).isAngry()) && (e instanceof EntityAnimal || e instanceof EntityAgeable || e instanceof EntityTameable || e instanceof EntityAmbientCreature || e instanceof EntitySquid || (e instanceof EntityIronGolem && ((EntityIronGolem)e).getRevengeTarget() == null));
    }
    
    public Vec3d getInterpolatedAmount(final Entity entity, final double x, final double y, final double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }
    
    public double[] calculateLookAt(final double px, final double py, final double pz, final EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;
        final double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0 / 3.141592653589793;
        yaw = yaw * 180.0 / 3.141592653589793;
        yaw += 90.0;
        return new double[] { yaw, pitch };
    }
    
    public Vec3d getInterpolatedPos(final Entity entity, final float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(this.getInterpolatedAmount(entity, ticks, ticks, ticks));
    }
    
    public Vec3d getInterpolatedRenderPos(final Entity entity, final float ticks) {
        final double[] rPos = RenderUtils.rPos();
        return this.getInterpolatedPos(entity, ticks).subtract(rPos[0], rPos[1], rPos[2]);
    }
    
    static {
        settings = Arrays.asList(new SettingToggle(true, "AutoSwitch"), new SettingToggle(true, "Players"), new SettingToggle(false, "Mobs"), new SettingToggle(false, "Animals"), new SettingToggle(true, "Place"), new SettingToggle(false, "AlwaysPlace"), new SettingSlider(0.0, 6.0, 4.5, 2, "PlaceRange: "), new SettingSlider(0.0, 6.0, 4.5, 2, "HitRange: "), new SettingSlider(0.0, 1000.0, 50.0, 2, "PlaceDelay: "));
    }
}
