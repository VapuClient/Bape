package mc.bape.module.combat;

import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import mc.bape.utils.TimerUtil;
import mc.bape.values.Numbers;
import mc.bape.values.Option;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;

import static net.minecraft.realms.RealmsMth.sqrt;
import static net.minecraft.realms.RealmsMth.wrapDegrees;

public class Aimbot extends Module {
    private final TimerUtil timer = new TimerUtil();
    private Numbers<Double> Reach = new Numbers<Double>("Reach", "Reach",4.5, 3.0, 6.0,1.0);
    private Option<Boolean> magnetism = new Option<Boolean>("Magnetism","Magnetism", true);
    private Numbers<Double> yaw = new Numbers<Double>("Yaw", "Yaw",15.0, 1.0, 50.0,1.0);
    private Numbers<Double> pitch = new Numbers<Double>("Pitch", "Pitch",15.0, 1.0, 50.0,1.0);
    public EntityLivingBase target;
    public Aimbot() {
        super("Aimbot", Keyboard.KEY_NONE, ModuleType.Combat,"Automatically aim at your target");
        this.addValues(this.Reach,this.magnetism);
        Chinese="自瞄";
    }

    @Override
    public void disable() {
        this.target = null;
        super.disable();
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        this.status = this.Reach.getValue().toString();
        if(this.magnetism.getValue()){
            if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                this.updateTarget();
                assistFaceEntity(this.target, this.yaw.getValue().floatValue(), this.pitch.getValue().floatValue());
                this.target = null;
            }
        } else {
            this.updateTarget();
            assistFaceEntity(this.target, this.yaw.getValue().floatValue(), this.pitch.getValue().floatValue());
            this.target = null;
        }
    }

    public static void assistFaceEntity(Entity entity, float yaw, float pitch) {
        double yDifference;
        if (entity == null) {
            return;
        }
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            yDifference = entityLivingBase.posY + (double) entityLivingBase.getEyeHeight() - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        } else {
            yDifference = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        }
        double dist = sqrt(diffX * diffX + diffZ * diffZ);
        float rotationYaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float rotationPitch = (float) (-(Math.atan2(yDifference, dist) * 180.0 / Math.PI));
        if (yaw > 0.0f) {
            mc.thePlayer.rotationYaw = updateRotation(mc.thePlayer.rotationYaw, rotationYaw, yaw / 4.0f);
        }
        if (pitch > 0.0f) {
            mc.thePlayer.rotationPitch = updateRotation(mc.thePlayer.rotationPitch, rotationPitch, pitch / 4.0f);
        }
    }

    public static float updateRotation(float p_70663_1_, float p_70663_2_, float p_70663_3_) {
        float var4 = wrapDegrees(p_70663_2_ - p_70663_1_);
        if (var4 > p_70663_3_) {
            var4 = p_70663_3_;
        }
        if (var4 < -p_70663_3_) {
            var4 = -p_70663_3_;
        }
        return p_70663_1_ + var4;
    }

    void updateTarget() {
        try {
            for (Entity object : getEntityList()) {
                EntityLivingBase entity;
                if (!(object instanceof EntityLivingBase) || !this.check(entity = (EntityLivingBase) object)) continue;
                this.target = entity;
            }
        } catch (NullPointerException Ex){
//            Helper.sendMessage("傻逼吧你");
        }
    }

    public static List<Entity> getEntityList() {
        return mc.theWorld.getLoadedEntityList();
    }

    public boolean check(EntityLivingBase entity) {
        if (entity instanceof EntityArmorStand) {
            return false;
        }
        if (entity == mc.thePlayer) {
            return false;
        }
        if (entity.isDead) {
            return false;
        }
        if(AntiBot.isServerBot(entity)){
            return false;
        }
        if(entity.getDistanceToEntity(mc.thePlayer) > this.Reach.getValue()){
            return false;
        }
        return mc.thePlayer.canEntityBeSeen(entity);
    }
}
