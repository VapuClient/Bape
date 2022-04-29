package mc.bape.module.Config;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mc.bape.api.miliblue.EventHandler;
import mc.bape.api.miliblue.events.EventPreUpdate;
import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import mc.bape.utils.TimerUtil;
import mc.bape.values.Mode;
import mc.bape.event.PacketEvent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.input.Keyboard.KEY_NONE;

public class Disabler extends Module {
    public static float yawDiff;
    Queue<C0FPacketConfirmTransaction> confirmTransactionQueue = new ConcurrentLinkedQueue<>();
    Queue<C00PacketKeepAlive> keepAliveQueue = new ConcurrentLinkedQueue<>();
    public static TimerUtil timer = new TimerUtil();
    TimerUtil lastRelease = new TimerUtil();
    int lastUid, cancelledPackets;
    public static boolean hasDisabled;
    private final TimerUtil blocksMCTimerUtils = new TimerUtil();
    private final LinkedList<Packet<?>> blocksMCPacketList = new LinkedList<>();
    private final LinkedList<C0FPacketConfirmTransaction> minePlexPacketList = new LinkedList<>();

    private Mode<Enum> mode = new Mode("Mode", "mode", DisablerMode.values(), DisablerMode.Hypixel);
    public Disabler() {
        super("Disabler", KEY_NONE, ModuleType.Global,"Make Anticheat shutdown");
    }

    enum DisablerMode {
        Hypixel, BlocksMc, MinePlex
    }
    @Override
    public void disable() {
        if (this.mode.getValue() == DisablerMode.BlocksMc) {
            if (!blocksMCPacketList.isEmpty()) {
                Packet<?> packet;

                while ((packet = blocksMCPacketList.poll()) != null) {
                    mc.getNetHandler().addToSendQueue(packet);
                }
            }
        } else if (this.mode.getValue() == DisablerMode.MinePlex) {
            if (!minePlexPacketList.isEmpty()) {
                C0FPacketConfirmTransaction packet;

                while ((packet = minePlexPacketList.poll()) != null) {
                    mc.getNetHandler().addToSendQueue(packet);
                }
            }
        }
    }

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
        if (this.mode.getValue() == DisablerMode.BlocksMc) {
            if (blocksMCTimerUtils.hasReached(490)) {
                if (!blocksMCPacketList.isEmpty()) {
                    mc.getNetHandler().addToSendQueue(blocksMCPacketList.poll());
                }
            }

            if (mc.thePlayer.ticksExisted % 40 == 0) {
                mc.getNetHandler().addToSendQueue(new C0CPacketInput());
                e.setY(e.getY() - 0.114514);
                e.setOnground(false);
            }
        } else if (this.mode.getValue() == DisablerMode.MinePlex) {
            if (mc.thePlayer.ticksExisted % 10 == 5) {
                mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(0, (short) -1, false));
                mc.getNetHandler().addToSendQueue(new C00PacketKeepAlive(-1));

                e.setY(e.getY() + 1.0E-4);
            }
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent e) {
        if (this.mode.getValue() == DisablerMode.BlocksMc) {
            if (e.getPacket() instanceof C0FPacketConfirmTransaction) {
                blocksMCPacketList.add(e.getPacket());
                e.setCanceled(true);

                if(blocksMCPacketList.size() > 300) {
                    mc.getNetHandler().addToSendQueue(blocksMCPacketList.poll());
                }
            } else if (e.getPacket() instanceof S08PacketPlayerPosLook) {
                if (mc.thePlayer.ticksExisted >= 20) {
                    final ItemStack stack = mc.thePlayer.inventory.mainInventory[0];

                    if (!(stack != null && stack.getItem() == Items.compass && stack.getDisplayName().endsWith("Game Menu"))) {
                        final S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
                        final double x = packet.getX() - mc.thePlayer.posX;
                        final double y = packet.getY() - mc.thePlayer.posY;
                        final double z = packet.getZ() - mc.thePlayer.posZ;
                        final double diff = Math.sqrt(x * x + y * y + z * z);

                        if (diff <= 8) {
                            e.setCanceled(true);
                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), true));
                        }
                    }
                }
            }

            if (mc.thePlayer.ticksExisted <= 7) {
                blocksMCTimerUtils.reset();
                blocksMCPacketList.clear();
            }
        }
    }

}
