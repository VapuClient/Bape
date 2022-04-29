package mc.bape.module.other;

import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import org.lwjgl.input.Keyboard;

public class NoCommand extends Module {
    public NoCommand() {
        super("NoCommand", Keyboard.KEY_NONE, ModuleType.Other, "No command.");
        Chinese=("没有指令");
    }

}
