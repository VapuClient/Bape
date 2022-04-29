package mc.bape.module.render;

import mc.bape.vapu.Client;
import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import org.lwjgl.input.Keyboard;

public class StateMessage extends Module {
    public StateMessage() {
        super("NoStateMessage", Keyboard.KEY_NONE, ModuleType.Render,"Hide Modules State info");
        Chinese="无开关信息";
    }

    public void enable() {
        Client.ENABLE_STATE_CHAT_MESSAGE = true;
    }

    public void disable(){
        Client.ENABLE_STATE_CHAT_MESSAGE = false;
    }
}
