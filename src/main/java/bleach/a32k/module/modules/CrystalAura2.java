package bleach.a32k.module.modules;

import bleach.a32k.module.*;
import net.minecraft.entity.item.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.passive.*;
import java.util.stream.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.client.entity.*;
import net.minecraft.init.*;
import java.util.function.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.enchantment.*;
import net.minecraft.util.math.*;
import net.minecraft.potion.*;
import bleach.a32k.utils.*;
import bleach.a32k.settings.*;
import java.util.*;

public class CrystalAura2 extends Module
{
    private static final List<SettingBase> settings;
    private BlockPos render;
    private boolean togglePitch;
    private boolean switchCooldown;
    private boolean isAttacking;
    private int oldSlot;
    private int newSlot;
    private int breaks;
    private boolean isSpoofingAngles;
    
    public CrystalAura2() {
        super("CrystalAura2", 0, Category.COMBAT, "Crystal Aura 2: electric boogaloo", CrystalAura2.settings);
        this.togglePitch = false;
        this.switchCooldown = false;
        this.isAttacking = false;
        this.oldSlot = -1;
    }
    
    @Override
    public void onUpdate() {
        final EntityEnderCrystal crystal = (EntityEnderCrystal)this.mc.world.loadedEntityList.stream().filter(entityx -> entityx instanceof EntityEnderCrystal).map(entityx -> entityx).min(Comparator.comparing(c -> this.mc.player.getDistance(c))).orElse(null);
        if (this.getSettings().get(5).toToggle().state && crystal != null && this.mc.player.getDistance(crystal) <= this.getSettings().get(11).toSlider().getValue()) {
            if (this.getSettings().get(7).toToggle().state && this.mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                if (!this.isAttacking) {
                    this.oldSlot = this.mc.player.inventory.currentItem;
                    this.isAttacking = true;
                }
                this.newSlot = -1;
                for (int crystalSlot = 0; crystalSlot < 9; ++crystalSlot) {
                    final ItemStack stack = this.mc.player.inventory.getStackInSlot(crystalSlot);
                    if (stack != ItemStack.EMPTY) {
                        if (stack.getItem() instanceof ItemSword) {
                            this.newSlot = crystalSlot;
                            break;
                        }
                        if (stack.getItem() instanceof ItemTool) {
                            this.newSlot = crystalSlot;
                            break;
                        }
                    }
                }
                if (this.newSlot != -1) {
                    this.mc.player.inventory.currentItem = this.newSlot;
                    this.switchCooldown = true;
                }
            }
            this.lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, this.mc.player);
            this.mc.playerController.attackEntity(this.mc.player, crystal);
            this.mc.player.swingArm(EnumHand.MAIN_HAND);
            ++this.breaks;
            if (this.breaks == 2 && !this.getSettings().get(8).toToggle().state) {
                if (this.getSettings().get(9).toToggle().state) {
                    this.resetRotation();
                }
                this.breaks = 0;
                return;
            }
            if (this.getSettings().get(8).toToggle().state && this.breaks == 1) {
                if (this.getSettings().get(9).toToggle().state) {
                    this.resetRotation();
                }
                this.breaks = 0;
                return;
            }
        }
        else {
            if (this.getSettings().get(9).toToggle().state) {
                this.resetRotation();
            }
            if (this.oldSlot != -1) {
                this.mc.player.inventory.currentItem = this.oldSlot;
                this.oldSlot = -1;
            }
            this.isAttacking = false;
        }
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
        final List<Entity> entities = new ArrayList<Entity>();
        if (this.getSettings().get(1).toToggle().state) {
            entities.addAll(this.mc.world.playerEntities);
        }
        entities.addAll(this.mc.world.loadedEntityList.stream().filter(entityx -> (entityx instanceof EntityLivingBase && entityx instanceof EntityAnimal) ? this.getSettings().get(3).toToggle().state : this.getSettings().get(2).toToggle().state).collect(Collectors.toList()));
        BlockPos q = null;
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
                        if (self > d && d >= ((EntityLivingBase)entity).getHealth()) {
                            continue;
                        }
                        if (self - 0.5 > this.mc.player.getHealth()) {
                            continue;
                        }
                        damage = d;
                        q = blockPos;
                    }
                }
            }
        }
        if (damage == 0.5) {
            this.render = null;
            if (this.getSettings().get(9).toToggle().state) {
                this.resetRotation();
            }
            return;
        }
        this.render = q;
        if (this.getSettings().get(4).toToggle().state) {
            if (!offhand && this.mc.player.inventory.currentItem != crystalSlot) {
                if (this.getSettings().get(0).toToggle().state) {
                    this.mc.player.inventory.currentItem = crystalSlot;
                    if (this.getSettings().get(9).toToggle().state) {
                        this.resetRotation();
                    }
                    this.switchCooldown = true;
                }
                return;
            }
            this.lookAtPacket(q.getX() + 0.5, q.getY() - 0.5, q.getZ() + 0.5, this.mc.player);
            EnumFacing f;
            if (!this.getSettings().get(10).toToggle().state) {
                f = EnumFacing.UP;
            }
            else {
                final RayTraceResult result = this.mc.world.rayTraceBlocks(new Vec3d(this.mc.player.posX, this.mc.player.posY + this.mc.player.getEyeHeight(), this.mc.player.posZ), new Vec3d(q.getX() + 0.5, q.getY() - 0.5, q.getZ() + 0.5));
                if (result != null && result.sideHit != null) {
                    f = result.sideHit;
                }
                else {
                    f = EnumFacing.UP;
                }
                if (this.switchCooldown) {
                    this.switchCooldown = false;
                    return;
                }
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
        }
    }
    
    private void lookAtPacket(final double px, final double py, final double pz, final EntityPlayer me) {
        final double[] v = this.calculateLookAt(px, py, pz, me);
        this.setYawAndPitch((float)v[0], (float)v[1]);
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
        positions.addAll(this.getSphere(this.getPlayerPos(), (float)this.getSettings().get(11).toSlider().getValue(), (int)this.getSettings().get(11).toSlider().getValue(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
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
        final float damage = (float)(int)((v * v + v) / 2.0 * 9.0 * doubleExplosionSize + 1.0);
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
    public void onEnable() {
        if (this.getSettings().get(6).toToggle().state) {
            RuhamaLogger.log("AutoCrystal: ON");
        }
    }
    
    @Override
    public void onDisable() {
        if (this.getSettings().get(6).toToggle().state) {
            RuhamaLogger.log("AutoCrystal: OFF");
        }
        this.render = null;
        this.resetRotation();
    }
    
    static {
        settings = Arrays.asList(new SettingToggle(true, "AutoSwitch"), new SettingToggle(true, "Players"), new SettingToggle(false, "Mobs"), new SettingToggle(false, "Animals"), new SettingToggle(true, "Place"), new SettingToggle(true, "Explode"), new SettingToggle(false, "Chat Alert"), new SettingToggle(false, "Anti Weakness"), new SettingToggle(false, "Slow"), new SettingToggle(false, "Rotate"), new SettingToggle(false, "RayTrace"), new SettingSlider(0.0, 6.0, 4.25, 2, "Range: "));
    }
}
