package mc.bape.module.player;

import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import mc.bape.utils.TimerUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class AntiAFK extends Module {
    public TimerUtil timer = new TimerUtil();
    public AntiAFK() {
        super("AntiAFK", Keyboard.KEY_NONE, ModuleType.Player,"Prevent you kicked of AFK");
        Chinese="AntiAFK";
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (this.timer.delay((long) 10.0)) {
            if(mc.thePlayer.onGround){
                mc.thePlayer.jump();
            }
            this.timer.reset();
        }
    }
}
