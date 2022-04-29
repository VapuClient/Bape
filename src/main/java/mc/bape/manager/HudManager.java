package mc.bape.manager;

import mc.bape.legit.hud.mod.HudMod;
import mc.bape.legit.hud.mod.impl.*;

import java.util.ArrayList;

public class HudManager {
    public ArrayList<HudMod> hudMods = new ArrayList<>();

    public HudManager() {
        hudMods.add(new ModsArrayList());
        hudMods.add(new InventoryHUD());
        hudMods.add(new Sprint());
        hudMods.add(new TargetHUD());
    }

    public void renderMods() {
        for (HudMod m : hudMods) {
            m.draw();
        }
    }
}
