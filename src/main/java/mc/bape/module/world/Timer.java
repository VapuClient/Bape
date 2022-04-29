package mc.bape.module.world;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import mc.bape.values.Numbers;
import org.lwjgl.input.Keyboard;

public class Timer extends Module {
    public Timer (){
        super("Timer", Keyboard.KEY_NONE, ModuleType.World, "Make world quickly");
        this.addValues(this.timer);
        Chinese=("变速齿轮");
    }

    private Numbers<Double> timer = new Numbers<Double>("Speed", "Speed",1.0, 1.0, 10.0,1.0);

    @SubscribeEvent
    public boolean onUpdate(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null) ;
        return false;

    }

}