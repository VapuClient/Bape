package mc.bape.module.Config;

import net.minecraftforge.common.MinecraftForge;
import mc.bape.manager.PacketManager;
import mc.bape.vapu.Client;
import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import mc.bape.manager.ModuleManager;

import java.util.ArrayList;

import static org.lwjgl.input.Keyboard.KEY_NONE;

public class Uninject extends Module {
    public Uninject() {
        super("Uninject", KEY_NONE, ModuleType.Global,"Uninject "+ Client.CLIENT_NAME);
        Chinese="卸载";
        NoToggle=true;
    }

    public void enable() {
        mc.thePlayer.closeScreen();
        ArrayList<Module> modules = new ArrayList<>(ModuleManager.getModules());
        for (Module m : modules) {
            if (m != null) {
                m.setState(false);
            }
        }
        Client.CLIENT_STATE = false;
        if (Client.INSTANCE != null) {
            MinecraftForge.EVENT_BUS.unregister(Client.INSTANCE);
            PacketManager.INSTANCE.uninject();
            Client.INSTANCE =null;
        }
        state=false;
    }
}
