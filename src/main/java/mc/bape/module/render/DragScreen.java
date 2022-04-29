package mc.bape.module.render;

import mc.bape.legit.hud.HUDConfigScreen;
import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import org.lwjgl.input.Keyboard;

public class DragScreen extends Module {
    public DragScreen() {
        super("DragScreen", Keyboard.KEY_LMENU, ModuleType.Render, "A DragScreen");
        Chinese="可移动HUD";
    }

    @Override
    public void enable() {
        this.setState(false);
        mc.thePlayer.closeScreen();
        mc.displayGuiScreen(new HUDConfigScreen());
    }
}
