package mc.bape.module.world;

import net.minecraft.client.Minecraft;
import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import mc.bape.utils.TimerUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;

public class FastPlace extends Module {
    private final TimerUtil timer = new TimerUtil();

    public FastPlace() {
        super("FastPlace", Keyboard.KEY_NONE, ModuleType.World, "Make you place the blocks faster");
        Chinese = "快速放置";
    }

    @SubscribeEvent
    public void onTick(final TickEvent.PlayerTickEvent event) {
            try {
                final Field rightClickDelay = Minecraft.class.getDeclaredField("field_71467_ac");
                rightClickDelay.setAccessible(true);
                rightClickDelay.set(FastPlace.mc, 0);
            }
            catch (Exception d) {
                try {
                    final Field e = Minecraft.class.getDeclaredField("rightClickDelayTimer");
                    e.setAccessible(true);
                    e.set(FastPlace.mc, 0);
                }
                catch (Exception f) {
                    this.disable();
                }
            }
    }

}
