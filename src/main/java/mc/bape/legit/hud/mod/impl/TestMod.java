package mc.bape.legit.hud.mod.impl;

import mc.bape.legit.hud.mod.HudMod;
import mc.bape.manager.FontManager;

public class TestMod extends HudMod {
    public TestMod() {
        super("testMod", 5, 5);
    }

    @Override
    public void draw() {
        FontManager.F18.drawString(name, getX(), getY(), -1);
        super.draw();
    }

    @Override
    public void renderDummy(int mouseX, int mouseY) {
        FontManager.F18.drawString(name,getX(), getY(), -1);

        super.renderDummy(mouseX, mouseY);
    }

    @Override
    public int getWidth() {
        return FontManager.F18.getStringWidth(name);
    }

    @Override
    public int getHeight() {
        return FontManager.F18.getStringWidth(name);
    }
}
