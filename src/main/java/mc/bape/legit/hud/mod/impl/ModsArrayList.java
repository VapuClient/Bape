package mc.bape.legit.hud.mod.impl;

import mc.bape.legit.hud.mod.HudMod;
import mc.bape.manager.FontManager;
import mc.bape.manager.ModuleManager;
import mc.bape.module.Module;
import mc.bape.module.render.HUD;
import mc.bape.utils.ColorUtils;
import mc.bape.utils.RenderUtils;
import mc.bape.values.Option;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class ModsArrayList extends HudMod {

    int width = new ScaledResolution(mc).getScaledWidth();

    Option<Boolean> option = HUD.ArrayList;

    public ModsArrayList() {
        super("ModsArrayList", 100, 100);
    }

    @Override
    public void draw() {
        if (option.getValue()) {
            ArrayList<Module> enabledModules = new ArrayList<>();
            RenderUtils.drawImage(getX() - 70, getY() - 27, 95, 30, new ResourceLocation("JAT/BapeV4.png"), new Color(255, 255, 255));
            for (Module m : ModuleManager.getModules()) {
                if (m.state) {
                    enabledModules.add(m);
                }
            }
            enabledModules.sort(new Comparator<Module>() {
                @Override
                public int compare(Module o1, Module o2) {
                    return FontManager.F20.getStringWidth(o2.getName()) - FontManager.F20.getStringWidth(o1.getName());
                }
            });
            int r = 0;
            int count = 0;
            for (Module m : enabledModules) {
                if (m != null && m.getState()) {
                    int moduleWidth = FontManager.F20.getStringWidth(m.name);
                    FontManager.F20.drawStringWithShadow(m.name, getX() - moduleWidth - 1, getY() + count * FontManager.F20.getHeight() + 2, ColorUtils.rainbow(1) + r);
                    y += FontManager.F20.getHeight();
                    r = r + 10;
                }
                count++;
            }
        }
        super.draw();
    }

    @Override
    public void renderDummy(int mouseX, int mouseY) {
        if (option.getValue()) {
            ArrayList<Module> enabledModules = new ArrayList<>();
            RenderUtils.drawImage(getX() - 62, getY() - 40, 85, 15, new ResourceLocation("JAT/TextGUI.png"), new Color(255, 255, 255));
            RenderUtils.drawImage(getX() - 70, getY() - 27, 95, 30, new ResourceLocation("JAT/BapeV4.png"), new Color(255, 255, 255));
            for (Module m : ModuleManager.getModules()) {
                if (m.state) {
                    enabledModules.add(m);
                }
            }
            enabledModules.sort(new Comparator<Module>() {
                @Override
                public int compare(Module o1, Module o2) {
                    return FontManager.F20.getStringWidth(o2.getName()) - FontManager.F20.getStringWidth(o1.getName());
                }
            });
            int r = 0;
            int count = 0;
            for (Module m : enabledModules) {
                if (m != null && m.getState()) {
                    int moduleWidth = FontManager.F20.getStringWidth(m.name);
                    FontManager.F20.drawStringWithShadow(m.name, getX() - moduleWidth - 1, getY() + count * FontManager.F20.getHeight() + 2, ColorUtils.rainbow(1) + r);
                    r = r + 10;
                }
                count++;
            }
            super.renderDummy(mouseX, mouseY);
        }
    }
}
