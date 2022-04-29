package mc.bape.manager;

import mc.bape.module.*;
import mc.bape.module.movement.*;
import mc.bape.module.other.*;
import mc.bape.module.player.*;
import mc.bape.module.Config.*;
import mc.bape.module.blatant.*;
import mc.bape.module.combat.*;
import mc.bape.module.render.*;
import mc.bape.module.world.*;
import java.util.*;

public class ModuleManager {

    static ArrayList<Module> Modules = new ArrayList<Module>();

    public static ArrayList<Module> getModules() {
        return Modules;
    }

    public ModuleManager() {

    }

    public static void registerModule(Module tagrtModule){
        Modules.add(tagrtModule);
    }

    public static Module getModule(String name) {
        for (Module m : Modules) {
            if (m.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase()))
                return m;
        }
        return null;
    }

    public static List<Module> getModulesInType(ModuleType t) {
        ArrayList<Module> output = new ArrayList<Module>();
        ArrayList<Module> module = new ArrayList<Module>();
        module.addAll(module);
        for (Module m : module) {
            if (m.getCategory() != t) continue;
            output.add(m);
        }
        output.sort(Comparator.comparingInt((Module o) -> Character.toLowerCase(o.getName().charAt(0))).thenComparingInt(o -> o.getName().charAt(0)));
        return output;
    }

    static {
        // 没Add的都是有问题的，不要add
        ModuleManager.registerModule(new HitBox());
        ModuleManager.registerModule(new DelayRemover());
        ModuleManager.registerModule(new InvManager());
        ModuleManager.registerModule(new AutoArmor());
        ModuleManager.registerModule(new AutoGG());
        ModuleManager.registerModule(new AntiBot());
        ModuleManager.registerModule(new Speed());
        ModuleManager.registerModule(new Sprint());
        ModuleManager.registerModule(new ClickGUI());
        ModuleManager.registerModule(new IGN());
        ModuleManager.registerModule(new StateMessage());
        ModuleManager.registerModule(new HUD());
        ModuleManager.registerModule(new FullBright());
        ModuleManager.registerModule(new AutoTools());
        ModuleManager.registerModule(new AutoL());
        ModuleManager.registerModule(new LoadConfig());
        ModuleManager.registerModule(new SaveConfig());
        ModuleManager.registerModule(new Aimbot());
        ModuleManager.registerModule(new Uninject());
        ModuleManager.registerModule(new InvMove());
        ModuleManager.registerModule(new Killaura());
        ModuleManager.registerModule(new BowAimBot());
        ModuleManager.registerModule(new AutoMLG());
        ModuleManager.registerModule(new MurderMystery());
        ModuleManager.registerModule(new Reach());
        ModuleManager.registerModule(new StorageESP());
        ModuleManager.registerModule(new ESP());
        ModuleManager.registerModule(new ChestStealer());
        ModuleManager.registerModule(new AntiAFK());
        ModuleManager.registerModule(new AutoClicker());
        ModuleManager.registerModule(new NoJumpDelay());
        ModuleManager.registerModule(new NoCommand());
        ModuleManager.registerModule(new Criticals());
        ModuleManager.registerModule(new Teams());
        ModuleManager.registerModule(new Tracers());
        ModuleManager.registerModule(new Fly());
        ModuleManager.registerModule(new Velocity());
        ModuleManager.registerModule(new AutoPot());
        ModuleManager.registerModule(new Refill());
        ModuleManager.registerModule(new Chams());
        ModuleManager.registerModule(new FastPlace());
        ModuleManager.registerModule(new FuckServer());
        ModuleManager.registerModule(new Projectiles());
        ModuleManager.registerModule(new Disabler());
        ModuleManager.registerModule(new NameTags());
        ModuleManager.registerModule(new ReachAssist());
        ModuleManager.registerModule(new DragScreen());
    }
}