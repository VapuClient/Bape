package mc.bape.module.blatant;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Timer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import mc.bape.utils.TimerUtil;
import mc.bape.values.Mode;
import mc.bape.vapu.Client;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Objects;

public class Fly extends Module {
    private float Flying;
    private int time;
    private int i;
    private float stage;
    private int ticks;
    private boolean doFly;
    private double x, y, z;
    private ArrayList<Packet> packets = new ArrayList<>();
    private boolean hasClipped;
    private double speedStage;
    private boolean t;
    private final TimerUtil timer = new TimerUtil();
    private Mode<Enum> mode = new Mode("Mode", "mode", (Enum[]) Fly.FlyModes.values(), (Enum) Fly.FlyModes.Zoom);
    public Fly() {
        super("Fly", Keyboard.KEY_NONE, ModuleType.Blatant,"Make you can flying");
        this.addValues(this.mode);
    }

    static enum FlyModes {
        Zoom,
        FastTimer,
        Guardian,
        Timer,
        OldWatchdog,
        Vanilla
    }

    @Override
    public void enable() {
        this.Flying = Fly.mc.thePlayer.stepHeight;
    }

    @Override
    public void disable() {
        this.i = 0;
        if (Objects.requireNonNull(Client.getTimer()).timerSpeed != 1.0f) {
            Client.getTimer().timerSpeed = 1.0f;
        }
        Client.getTimer().timerSpeed = 1.0f;
        Fly.mc.thePlayer.stepHeight = this.Flying;
        if (Fly.mc.thePlayer.capabilities.isFlying) {
            Fly.mc.thePlayer.capabilities.isFlying = false;
        }
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.PlayerTickEvent Event) {
        if (this.mode.getValue() != FlyModes.Vanilla && Fly.mc.thePlayer.capabilities.isFlying) {
            Fly.mc.thePlayer.capabilities.isFlying = false;
        }

        if(this.mode.getValue() == FlyModes.FastTimer){
            mc.thePlayer.cameraYaw = mc.thePlayer.cameraPitch = 0.05f;
            mc.thePlayer.posY = y;
            if (mc.thePlayer.onGround && stage == 0) {
                mc.thePlayer.motionY = 0.09;
            }
            stage++;
            if (mc.thePlayer.onGround && stage > 2 && !hasClipped) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.15, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.15, mc.thePlayer.posZ, true));
                hasClipped = true;
            }
            if (doFly) {
                mc.thePlayer.motionY = 0;
                mc.thePlayer.onGround = true;
                Objects.requireNonNull(Client.getTimer()).timerSpeed = 2;
            } else {
//                MovementUtils.setSpeed(0);
                Client.getTimer().timerSpeed = 5;
            }
        }

        if(this.mode.getValue() == FlyModes.OldWatchdog){
            mc.thePlayer.motionY = 0;
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
            final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
            final double x = -Math.sin(yaw) * 6;
            final double z = Math.cos(yaw) * 6;
            if (timer.hasReached(1500)) {
                mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY - 2f, mc.thePlayer.posZ + z);
                timer.reset();
            }
        }
        if (this.mode.getValue() == FlyModes.Zoom) {
            Fly.mc.thePlayer.motionY = 0.0;
            Fly.mc.thePlayer.onGround = true;
            for (int i = 0; i < 3; ++i) {
                Fly.mc.thePlayer.setPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY + 3.0E-12, Fly.mc.thePlayer.posZ);
                if (Fly.mc.thePlayer.ticksExisted % 3 == 0) {
                    Fly.mc.thePlayer.setPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY - 3.0E-12, Fly.mc.thePlayer.posZ);
                }
            }
        }
        else if (this.mode.getValue() == FlyModes.Guardian) {
            if (Fly.mc.gameSettings.keyBindForward.isKeyDown()) {
                Fly.mc.thePlayer.stepHeight = 0.0f;
                ++this.time;
                if (this.time == 2) {
                    Fly.mc.thePlayer.setPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY + 1.0E-10, Fly.mc.thePlayer.posZ);
                    this.time = 0;
                }
                Fly.mc.thePlayer.motionY = 0.0;
                Fly.mc.thePlayer.onGround = true;
            }
            if (Fly.mc.gameSettings.keyBindJump.isPressed()) {
                Fly.mc.thePlayer.setPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY + 0.4, Fly.mc.thePlayer.posZ);
            }
        }
        else if (this.mode.getValue() == FlyModes.Timer) {
            if (Objects.requireNonNull(Client.getTimer()).timerSpeed >= 2.0f) {
                Objects.requireNonNull(Client.getTimer()).timerSpeed = 1.0f;
            }
            Fly.mc.thePlayer.motionY = 0.0;
            Fly.mc.thePlayer.setPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY + 0.005, Fly.mc.thePlayer.posZ);
            Fly.mc.thePlayer.setPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY - 0.005, Fly.mc.thePlayer.posZ);
            if (Fly.mc.thePlayer.ticksExisted % 3 == 0) {
                Fly.mc.thePlayer.setPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY + 0.001, Fly.mc.thePlayer.posZ);
                Fly.mc.thePlayer.setPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY + 1.0E-9, Fly.mc.thePlayer.posZ);
            }
            if (Fly.mc.thePlayer.ticksExisted % 5 == 0) {
                if (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) {
                    final Timer vaputimer = Client.getTimer();
                    assert vaputimer != null;
                    vaputimer.timerSpeed += 0.05f;
                }
                else {
                    Objects.requireNonNull(Client.getTimer()).timerSpeed = 1.0f;
                }
            }
            Fly.mc.thePlayer.onGround = true;
        }
        else if (this.mode.getValue() == FlyModes.Vanilla) {
            Fly.mc.thePlayer.capabilities.isFlying = true;
            if (Fly.mc.gameSettings.keyBindJump.isPressed()) {
                final EntityPlayerSP thePlayer = Fly.mc.thePlayer;
                thePlayer.motionY += 0.2;
            }
            if (Fly.mc.gameSettings.keyBindSneak.isPressed()) {
                final EntityPlayerSP thePlayer2 = Fly.mc.thePlayer;
                thePlayer2.motionY -= 0.2;
            }
            if (Fly.mc.gameSettings.keyBindForward.isPressed()) {
                Fly.mc.thePlayer.capabilities.setFlySpeed(0.1f);
            }
        }
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent e) {
        if (e.phase.equals((Object)TickEvent.Phase.END) && this.mode.getValue() == FlyModes.Zoom && Fly.mc.gameSettings.keyBindJump.isPressed()) {
            Fly.mc.thePlayer.jump();
            Fly.mc.thePlayer.motionY = 0.41999998688697815;
            Fly.mc.thePlayer.onGround = true;
        }
    }
}
