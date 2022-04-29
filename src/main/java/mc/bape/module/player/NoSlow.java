package mc.bape.module.player;

import net.minecraft.util.BlockPos;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import mc.bape.values.Mode;

import mc.bape.event.PacketEvent;
import org.lwjgl.input.Keyboard;


public class NoSlow extends Module {

    private Mode<Enum> mode = new Mode("Mode", "mode", (Enum[]) NoSlow.NoSlowMode.values(), (Enum) NoSlowMode.WatchDog);

    public NoSlow(){
        super("NoSlow", Keyboard.KEY_NONE, ModuleType.Player, "Make you no slow.");
        this.addValues(this.mode);
        Chinese="�޼���";
    }

    static enum NoSlowMode{
        WatchDog
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if(event.getSide() == PacketEvent.Side.CLIENT){
            if(event.getPacket() instanceof C07PacketPlayerDigging && mode.getValue() == NoSlowMode.WatchDog){
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
            } else{
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, null, 0F, 0F, 0F));
            }
        }
    }

}
