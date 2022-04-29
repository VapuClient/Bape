package mc.bape.module.combat;

import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import mc.bape.utils.EntitySize;
import mc.bape.values.Numbers;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class HitBox extends Module {
    private Numbers<Double> heights = new Numbers<Double>("Height", "Height",2.0, 2.0, 5.0,1.0);
    private Numbers<Double> Widths = new Numbers<Double>("Width", "Width",1.0, 1.0, 5.0,1.0);
    public HitBox() {
        super("HitBox",  Keyboard.KEY_NONE, ModuleType.Combat,"Change hitbox");
        this.addValues(this.heights, this.Widths);
        Chinese="Åö×²Ïä";
    }
    public boolean check(EntityLivingBase entity) {
        if(entity instanceof EntityPlayerSP) { return false; }
        if(entity == mc.thePlayer) { return false; }
        if(entity.isDead) { return false; }
        return true;
    }
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(mc.thePlayer==null || mc.theWorld==null){
            return;
        }
        for(EntityPlayer player : getPlayersList()) {
            if(!check(player)) continue;
            float width = this.Widths.getValue().floatValue();
            float height = this.heights.getValue().floatValue();
            setEntityBoundingBoxSize(player, width, height);
        }
    }
    @Override
    public void enable() {

    }
    @Override
    public void disable() {
        for(EntityPlayer player : getPlayersList())
            setEntityBoundingBoxSize(player);
    }
    public static void setEntityBoundingBoxSize(Entity entity, float width, float height) {
        EntitySize size = getEntitySize(entity);
        entity.width = size.width;
        entity.height = size.height;
        double d0 = (double) (width) / 2.0D;
        entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, entity.posY, entity.posZ - d0, entity.posX + d0,
                entity.posY + (double) height, entity.posZ + d0));
    }

    public static void setEntityBoundingBoxSize(Entity entity) {
        EntitySize size = getEntitySize(entity);
        entity.width = size.width;
        entity.height = size.height;
        double d0 = (double) (entity.width) / 2.0D;
        entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, entity.posY, entity.posZ - d0, entity.posX + d0,
                entity.posY + (double) entity.height, entity.posZ + d0));
    }
    public static EntitySize getEntitySize(Entity entity) {
        EntitySize entitySize = new EntitySize(0.6F, 1.8F);
        return entitySize;
    }
    public static List<EntityPlayer> getPlayersList() {
        return mc.theWorld.playerEntities;
    }

}