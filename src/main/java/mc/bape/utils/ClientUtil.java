package mc.bape.utils;

import mc.bape.module.render.HUD;
import mc.bape.utils.notication.Notification;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.util.EnumFacing;

import java.awt.*;
import java.util.ArrayList;

import static mc.bape.utils.Helper.mc;
import static java.lang.Math.asin;

public class ClientUtil {
    private static ArrayList<Notification> notifications = new ArrayList<>();
    static HUD hud = new HUD();

    public static float[] getDirectionToBlock(int var0, int var1, int var2, EnumFacing var3) {
        EntityEgg var4 = new EntityEgg(mc.theWorld);
        var4.posX = (double) var0 + 0.5;
        var4.posY = (double) var1 + 0.5;
        var4.posZ = (double) var2 + 0.5;
        var4.posX += (double) var3.getDirectionVec().getX() * 0.25;
        var4.posY += (double) var3.getDirectionVec().getY() * 0.25;
        var4.posZ += (double) var3.getDirectionVec().getZ() * 0.25;
        return getDirectionToEntity(var4);
    }

    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = ((float) 1 / 255) * c.getRed();
        float g = ((float) 1 / 255) * c.getGreen();
        float b = ((float) 1 / 255) * c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

    public static void drawNotifications() {
        try {
            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
            double startY = res.getScaledHeight() - 25;
            final double lastY = startY;
            for (int i = 0; i < notifications.size(); i++) {
                Notification not = notifications.get(i);
                if (not.shouldDelete()) {
                    notifications.remove(i);
                }
                if (hud.NotificationType.getValue() == HUD.NofiType.Black1) {
                    not.drawBlack1(startY, lastY);
                } else if(hud.NotificationType.getValue() == HUD.NofiType.Black2) {
                    not.drawBlack2(startY, lastY);
                }
                startY -= not.getHeight() + 1;
            }
        } catch (Throwable e) {

        }
    }

    public static void sendClientMessage(String message, Notification.Type type) {
        mc.thePlayer.playSound("random.click", 1, 1);
        if (notifications.size() > 8) {
            notifications.remove(0);
        }
        boolean has = false;
        for (Notification n : notifications) {
            if (n.getMessage().equals(message)) {
                has = true;
            }
        }
        if (!has) {
            notifications.add(new Notification(message, type));
        }
    }

    private static float[] getDirectionToEntity(Entity var0) {
        return new float[]{ClientUtil.getYaw(var0) + mc.thePlayer.rotationYaw, ClientUtil.getPitch(var0) + mc.thePlayer.rotationPitch};
    }

    public static float getYaw(Entity entity) {
        double x = entity.posX - mc.thePlayer.posX;
        double y = entity.posY - mc.thePlayer.posY;
        double z = entity.posZ - mc.thePlayer.posZ;
        double yaw = Math.atan2(x, z) * 57.29577951308232;
        yaw = -yaw;
        return (float) yaw;
    }

    public static float getPitch(Entity entity) {
        double x = entity.posX - mc.thePlayer.posX;
        double y = entity.posY - mc.thePlayer.posY;
        double z = entity.posZ - mc.thePlayer.posZ;
        double pitch = asin(y /= mc.thePlayer.getDistance(entity.posX,entity.posY,entity.posZ)) * 57.29577951308232;
        pitch = -pitch;
        return (float) pitch;
    }
    public static float getDirection() {
        float var1 = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0.0f) {
            var1 += 180.0f;
        }
        float forward = 1.0f;
        if (mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (mc.thePlayer.moveStrafing > 0.0f) {
            var1 -= 90.0f * forward;
        }
        if (mc.thePlayer.moveStrafing < 0.0f) {
            var1 += 90.0f * forward;
        }
        return var1 *= (float) Math.PI / 180;
    }
}
