package mc.bape.api.miliblue.events;

import java.util.Random;

import net.minecraft.client.Minecraft;
import mc.bape.api.miliblue.Event;

public class EventPreUpdate extends Event {
	public static Random rd = new Random();
	private float yaw;
	public double x,z;
	private float pitch;
	public double y;
	public static boolean ground;

	public EventPreUpdate(float yaw, float pitch, double y, boolean ground) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.y = y;
		this.ground = ground;
	}
	
    public void setRotations(float[] rotations, boolean random) {
        if (random) {
            yaw = rotations[0] + (float) (EventPreUpdate.rd.nextBoolean() ? Math.random() : -Math.random());
            pitch = rotations[1] + (float) (EventPreUpdate.rd.nextBoolean() ? Math.random() : -Math.random());
        } else {
            yaw = rotations[0];
            pitch = rotations[1];
        }
        Minecraft.getMinecraft().thePlayer.rotationYawHead = yaw;
		Minecraft.getMinecraft().thePlayer.renderYawOffset = yaw;
        setPitch(pitch);
    }

	public float getYaw() {
		return this.yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public double getY() {
		return this.y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public boolean isOnground() {
		return this.ground;
	}

	public  void setOnground(boolean ground) {
		this.ground = ground;
	}
	public void setX(double x) {
		this.x = x;
	}
	public void setZ(double z) {
		this.z = z;
	}
}
