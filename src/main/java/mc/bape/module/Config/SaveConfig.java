package mc.bape.module.Config;

import mc.bape.manager.ConfigManager;
import mc.bape.manager.ModuleManager;
import mc.bape.vapu.Client;
import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import mc.bape.utils.Helper;
import mc.bape.values.Value;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Iterator;

import static org.lwjgl.input.Keyboard.KEY_NONE;

public class SaveConfig extends Module {
    public SaveConfig() {
        super("SaveConfig", KEY_NONE, ModuleType.Global,"Save your module setting (config)");
        Chinese="保存配置";
        NoToggle=true;
    }

    public void enable() {
        String values = "";
        for (Module m : ModuleManager.getModules()) {
            for (Value v : m.getValues()) {
                values = String.valueOf(values) + String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator());
            }
        }
        ConfigManager.save(Client.CLIENT_CONFIG +"-values.cfg", values, false);
        String enabled = "";
        ArrayList<Module> modules = new ArrayList<>();
        for (Module m : ModuleManager.getModules()) {
            modules.add(m);
        }
        for (Module m : modules) {
            if (m != null && m.getState() && m != this) {
                enabled = String.valueOf(enabled) + String.format("%s%s", m.getName(), System.lineSeparator());
            }
        }
        String content = "";
        Module m;
        for(Iterator var4 = ModuleManager.getModules().iterator(); var4.hasNext(); content = content + String.format("%s:%s%s", new Object[]{m.getName(), Keyboard.getKeyName(m.getKey()), System.lineSeparator()})) {
            m = (Module)var4.next();
        }
        ConfigManager.save(Client.CLIENT_CONFIG +"-binds.cfg", content, false);
        ConfigManager.save(Client.CLIENT_CONFIG +"-modules.cfg", enabled, false);
        ConfigManager.save(Client.CLIENT_CONFIG +"-client.cfg", Client.CLIENT_NAME, false);
        Helper.sendMessage("Configs Saved.");
        state=false;
    }
}
