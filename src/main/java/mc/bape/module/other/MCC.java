package mc.bape.module.other;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import mc.bape.module.Module;
import mc.bape.utils.Helper;
import mc.bape.module.ModuleType;
import org.lwjgl.input.Keyboard;

import static org.lwjgl.input.Keyboard.KEY_B;

public class MCC extends Module {
    public MCC() {
        super("NameChecker", Keyboard.KEY_NONE, ModuleType.Other, "Check entity name in you pressed");
        Chinese=("Debug");
    }

    @SubscribeEvent
    public void keyInput(InputEvent.KeyInputEvent event) {
        if(this.state) {
            if (Keyboard.isKeyDown(KEY_B)){
                if (mc.objectMouseOver.entityHit != null) {
                    EntityPlayer player = (EntityPlayer) mc.objectMouseOver.entityHit;
                    String playername = player.getName();
                    Helper.sendMessage(playername);
                    System.out.print(playername);
                    System.out.print(player.getDisplayName());
                }
            }
        }
    }
}
