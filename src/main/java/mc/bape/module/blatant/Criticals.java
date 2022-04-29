package mc.bape.module.blatant;

import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import mc.bape.utils.TimerUtil;
import mc.bape.values.Mode;
import mc.bape.values.Numbers;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import mc.bape.event.PacketEvent;
import org.lwjgl.input.Keyboard;

public class Criticals extends Module {
    public int noBlockTimer = 0;
    public static final Mode<Enum> mode = new Mode("Mode", "mode", (Enum[]) Criticals.CriticalsMode.values(), (Enum) Criticals.CriticalsMode.Jump);
    enum CriticalsMode {
        Legit,
        Jump,
        Watchdog
    }
    private final TimerUtil timer = new TimerUtil();
    public static Numbers<Double> delay = new Numbers<Double>("Delay", "delay", 50.0, 0.0, 100.0, 10.0);
    public Criticals() {
        super("Criticals", Keyboard.KEY_NONE, ModuleType.Blatant,"Make you Criticals on Attack");
        this.addValues(this.mode);
        Chinese="刀刀暴击";
    }

    public static boolean canJump() {
        if (mc.thePlayer.isOnLadder()) {
            return false;
        }
        if (mc.thePlayer.isInWater()) {
            return false;
        }
        if (mc.thePlayer.isInLava()) {
            return false;
        }
        if (mc.thePlayer.isSneaking()) {
            return false;
        }
        if (mc.thePlayer.isRiding()) {
            return false;
        }
        return true;
    }

    public boolean canCrit() {
        return mc.thePlayer.onGround && !mc.thePlayer.isInWater();
    }

    @SubscribeEvent
    public void onUpdate(TickEvent event){
        this.status = this.mode.getValue().toString();
    }

    @SubscribeEvent
    public void onPacket(PacketEvent e) {
        if(e.getSide() == PacketEvent.Side.CLIENT) {
            if (e.getPacket() instanceof C02PacketUseEntity && this.canCrit() && mode.getValue() == CriticalsMode.Jump) {
                mc.thePlayer.jump();
            }
            if (e.getPacket() instanceof C02PacketUseEntity && this.canCrit() && mode.getValue() == CriticalsMode.Watchdog) {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.114514, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0114514, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.014514, mc.thePlayer.posZ, false));
            }
        }
    }

    public static void doWatchdogCirt(){
        mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.114514, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0114514, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.014514, mc.thePlayer.posZ, false));
    }


    @SubscribeEvent
    public void onTick(TickEvent event){
        if(this.mode.getValue() == CriticalsMode.Legit){
            if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                if(mc.objectMouseOver.entityHit != null && canJump() && mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    mc.playerController.attackEntity(mc.thePlayer, Minecraft.getMinecraft().objectMouseOver.entityHit);
                }
            }
        }
    }
}
