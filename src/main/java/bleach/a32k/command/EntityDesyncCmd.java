package bleach.a32k.command;

import net.minecraftforge.client.*;
import net.minecraft.entity.*;
import net.minecraft.client.*;
import net.minecraft.command.*;
import net.minecraft.server.*;
import bleach.a32k.utils.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraft.util.math.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class EntityDesyncCmd extends CommandBase implements IClientCommand
{
    public Entity entity;
    public boolean dismounted;
    private Minecraft mc;
    
    public EntityDesyncCmd() {
        this.mc = Minecraft.getMinecraft();
    }
    
    public boolean allowUsageWithoutPrefix(final ICommandSender sender, final String message) {
        return false;
    }
    
    public String getName() {
        return "entitydesync";
    }
    
    public String getUsage(final ICommandSender sender) {
        return null;
    }
    
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) {
        if (args.length != 1 || (!args[0].equalsIgnoreCase("dismount") && !args[0].equalsIgnoreCase("remount"))) {
            RuhamaLogger.log("Invalid syntax, /entitydesync (dismount/remount)");
            return;
        }
        if (args[0].equalsIgnoreCase("dismount")) {
            this.dismounted = true;
        }
        else if (args[0].equalsIgnoreCase("remount")) {
            this.dismounted = false;
        }
        if (this.dismounted) {
            if (this.mc.player.getRidingEntity() == null) {
                RuhamaLogger.log("No entity to dismount");
                return;
            }
            this.mc.renderGlobal.loadRenderers();
            this.entity = this.mc.player.getRidingEntity();
            this.mc.player.dismountRidingEntity();
            this.mc.world.removeEntity(this.entity);
            MinecraftForge.EVENT_BUS.register(this);
            RuhamaLogger.log("Dismounted");
        }
        else if (!this.dismounted) {
            if (this.entity == null) {
                RuhamaLogger.log("No entity to remount");
                return;
            }
            this.entity.isDead = false;
            this.mc.world.loadedEntityList.add(this.entity);
            this.mc.player.startRiding(this.entity, true);
            this.entity = null;
            MinecraftForge.EVENT_BUS.unregister(this);
            RuhamaLogger.log("Remounted");
        }
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || this.mc.player == null || this.mc.world == null) {
            return;
        }
        if (!this.mc.world.isBlockLoaded(new BlockPos(this.mc.player.posX, 0.0, this.mc.player.posZ))) {
            return;
        }
        if (this.mc.player.getRidingEntity() != null) {
            this.entity = null;
        }
        if (this.entity == null && this.mc.player.getRidingEntity() != null) {
            this.entity = this.mc.player.getRidingEntity();
            this.mc.player.dismountRidingEntity();
            this.mc.world.removeEntity(this.entity);
        }
        if (this.entity != null) {
            this.entity.setPosition(this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ);
            this.mc.player.connection.sendPacket(new CPacketVehicleMove(this.entity));
        }
    }
    
    public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
        return true;
    }
}
