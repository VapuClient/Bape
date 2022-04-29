/*
* 警告：这个类的所有东西都是抄的
* 而且有可能引起不适的极度脑溢血命名
* 请谨慎查看代码
*/

package mc.bape.module.combat;
import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import mc.bape.values.Numbers;
import mc.bape.values.Option;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.client.event.MouseEvent;

import java.util.List;
import java.util.Random;
public class Reach extends Module {
    public Random rand;
    private Option<Boolean> RandomReach = new Option<Boolean>("RandomReach","RandomReach", true);
    private Option<Boolean> weaponOnly = new Option<Boolean>("WeaponOnly","weaponOnly", false);
    private Option<Boolean> movingOnly = new Option<Boolean>("MovingOnly","movingOnly", false);
    private Option<Boolean> sprintOnly = new Option<Boolean>("SprintOnly","sprintOnly", false);
    private Option<Boolean> hitThroughBlocks = new Option<Boolean>("HitThroughBlocks","sprintOnly", false);
    public static Numbers<Double> MinReach = new Numbers<Double>("Reach", "Reach",3.5, -2.0, 6.0,1.0);
    public Reach() {
            super("Reach", Keyboard.KEY_NONE, ModuleType.Combat,"Make you can attack far target");
            this.addValues(this.weaponOnly,this.movingOnly,this.sprintOnly,this.hitThroughBlocks,this.MinReach);
            Chinese="长臂猿";
        }
    @SubscribeEvent
    public void onMove(final MouseEvent ev) {
        if (true) {
            if (this.weaponOnly.getValue()) {
                if (mc.thePlayer.getCurrentEquippedItem() == null) {
                    return;
                }
                if (!(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) && !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemAxe)) {
                    return;
                }
            }
            if (this.movingOnly.getValue() && mc.thePlayer.moveForward == 0.0 && mc.thePlayer.moveStrafing == 0.0) {
                return;
            }
            if (this.sprintOnly.getValue() && !mc.thePlayer.isSprinting()) {
                return;
            }
            if (!this.hitThroughBlocks.getValue() && mc.objectMouseOver != null) {
                final BlockPos blocksReach = mc.objectMouseOver.getBlockPos();
                if (blocksReach != null && mc.theWorld.getBlockState(blocksReach).getBlock() != Blocks.air) {
                    return;
                }
            }
            if(false){
                double reachValues = 3.0 + this.rand.nextDouble() * (this.MinReach.getValue() - 3.0);
                final Object[] target = doReach(reachValues, 0.0, 0.0f);
                if (target == null) {
                    return;
                }
                mc.objectMouseOver = new MovingObjectPosition((Entity)target[0], (Vec3)target[1]);
                mc.pointedEntity = (Entity)target[0];
            } else {
                double Reach = this.MinReach.getValue();
                final Object[] reachs = doReach(Reach, 0.0, 0.0f);
                if (reachs == null) {
                    return;
                }
                mc.objectMouseOver = new MovingObjectPosition((Entity)reachs[0], (Vec3)reachs[1]);
                mc.pointedEntity = (Entity)reachs[0];
            }
        }
    }

    public static Object[] doReach(final double reachValue, final double AABB, final float cwc) {
        final Entity target = mc.getRenderViewEntity();
        Entity entity = null;
        if (target == null || mc.theWorld == null) {
            return null;
        }
        mc.mcProfiler.startSection("pick");
        final Vec3 targetEyes = target.getPositionEyes(0.0f);
        final Vec3 targetLook = target.getLook(0.0f);
        final Vec3 targetVector = targetEyes.addVector(targetLook.xCoord * reachValue, targetLook.yCoord * reachValue, targetLook.zCoord * reachValue);
        Vec3 targetVec = null;
        final List targetHitbox = mc.theWorld.getEntitiesWithinAABBExcludingEntity(target, target.getEntityBoundingBox().addCoord(targetLook.xCoord * reachValue, targetLook.yCoord * reachValue, targetLook.zCoord * reachValue).expand(1.0, 1.0, 1.0));
        double reaching = reachValue;
        for (int i = 0; i < targetHitbox.size(); ++i) {
            final Entity targetEntity = (Entity) targetHitbox.get(i);
            if (targetEntity.canBeCollidedWith()) {
                final float targetCollisionBorderSize = targetEntity.getCollisionBorderSize();
                AxisAlignedBB targetAABB = targetEntity.getEntityBoundingBox().expand(targetCollisionBorderSize, targetCollisionBorderSize, targetCollisionBorderSize);
                targetAABB = targetAABB.expand(AABB, AABB, AABB);
                final MovingObjectPosition tagetPosition = targetAABB.calculateIntercept(targetEyes, targetVector);
                if (targetAABB.isVecInside(targetEyes)) {
                    if (0.0 < reaching || reaching == 0.0) {
                        entity = targetEntity;
                        targetVec = ((tagetPosition == null) ? targetEyes : tagetPosition.hitVec);
                        reaching = 0.0;
                    }
                }
                else if (tagetPosition != null) {
                    final double targetHitVec = targetEyes.distanceTo(tagetPosition.hitVec);
                    if (targetHitVec < reaching || reaching == 0.0) {
                        final boolean canRiderInteract = false;
                        if (targetEntity == target.ridingEntity) {
                            if (reaching == 0.0) {
                                entity = targetEntity;
                                targetVec = tagetPosition.hitVec;
                            }
                        }
                        else {
                            entity = targetEntity;
                            targetVec = tagetPosition.hitVec;
                            reaching = targetHitVec;
                        }
                    }
                }
            }
        }
        if (reaching < reachValue && !(entity instanceof EntityLivingBase) && !(entity instanceof EntityItemFrame)) {
            entity = null;
        }
        mc.mcProfiler.endSection();
        if (entity == null || targetVec == null) {
            return null;
        }
        return new Object[] { entity, targetVec };
    }

    }

