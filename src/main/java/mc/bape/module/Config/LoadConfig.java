package mc.bape.module.Config;

import mc.bape.manager.ConfigManager;
import mc.bape.manager.ModuleManager;
import mc.bape.vapu.Client;
import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import mc.bape.utils.Helper;
import mc.bape.values.Mode;
import mc.bape.values.Numbers;
import mc.bape.values.Option;
import mc.bape.values.Value;
import org.lwjgl.input.Keyboard;

import java.util.List;

import static org.lwjgl.input.Keyboard.KEY_NONE;

public class LoadConfig extends Module {
    public LoadConfig() {
        super("LoadConfig", KEY_NONE, ModuleType.Global,"Load your configs");
        Chinese="加载配置";
        NoToggle=true;
    }

    public void enable() {
        List<String> binds = ConfigManager.read(Client.CLIENT_CONFIG +"-binds.cfg");
        for (String v : binds) {
            String name = v.split(":")[0];
            String bind = v.split(":")[1];
            Module m = ModuleManager.getModule(name);
            if (m == null) continue;
            m.setKey(Keyboard.getKeyIndex((String)bind.toUpperCase()));
        }
        List<String> enabled = ConfigManager.read(Client.CLIENT_CONFIG +"-modules.cfg");
        for (String v : enabled) {
            Module m = ModuleManager.getModule(v);
            if (m == null) continue;
            m.setState(true);
        }
        List<String> vals = ConfigManager.read(Client.CLIENT_CONFIG +"-values.cfg");
        for (String v : vals) {
            String name = v.split(":")[0];
            String values = v.split(":")[1];
            Module m = ModuleManager.getModule(name);
            if (m == null) continue;
            for (Value value : m.getValues()) {
                if (!value.getName().equalsIgnoreCase(values)) continue;
                if (value instanceof Option) {
                    value.setValue(Boolean.parseBoolean(v.split(":")[2]));
                    continue;
                }
                if (value instanceof Numbers) {
                    value.setValue(Double.parseDouble(v.split(":")[2]));
                    continue;
                }
                ((Mode)value).setMode(v.split(":")[2]);
            }
        }
        List<String> cfg = ConfigManager.read(Client.CLIENT_CONFIG +"-client.cfg");
        if (cfg.size() != 0) {
            Client.CLIENT_NAME = cfg.get(0);
            Helper.sendMessage("Configs Loaded.");
        } else {
            Helper.sendMessage("Failed to load config");
        }
        this.state = false;
    }
}
