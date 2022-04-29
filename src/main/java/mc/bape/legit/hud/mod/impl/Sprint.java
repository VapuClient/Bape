package mc.bape.legit.hud.mod.impl;

import mc.bape.legit.hud.mod.HudMod;
import mc.bape.manager.FontManager;
import mc.bape.manager.ModuleManager;
import mc.bape.module.render.HUD;
import mc.bape.values.Option;

import java.awt.*;

public class Sprint extends HudMod {
    Option<Boolean> option = HUD.SprintRender;

    public Sprint() {
        super("Sprint", 500, 5);
    }

    @Override
    public void draw() {
        if (ModuleManager.getModule("Sprint").getState() && option.getValue()){
            mc.fontRendererObj.drawStringWithShadow("Sprinting (Toggled)",getX(),getY(),new Color(255, 255, 255).hashCode());
        } else if(option.getValue()){
            mc.fontRendererObj.drawStringWithShadow("Sprinting (Vanilla)",getX(),getY(),new Color(255, 255, 255).hashCode());
        }else if(mc.thePlayer.isSneaking() && option.getValue()){
            mc.fontRendererObj.drawStringWithShadow("Sneaking (Press)",getX(),getY(),new Color(255, 255, 255).hashCode());
        }
        super.draw();
    }

    @Override
    public void renderDummy(int mouseX, int mouseY) {
        if (ModuleManager.getModule("Sprint").getState() && option.getValue()){
            mc.fontRendererObj.drawStringWithShadow("Sprinting (Toggled)",getX(),getY(),new Color(255, 255, 255).hashCode());
        } else if(option.getValue()){
            mc.fontRendererObj.drawStringWithShadow("Sprinting (Vanilla)",getX(),getY(),new Color(255, 255, 255).hashCode());
        } else if(mc.thePlayer.isSneaking() && option.getValue()){
            mc.fontRendererObj.drawStringWithShadow("Sneaking (Press)",getX(),getY(),new Color(255, 255, 255).hashCode());
        }
        super.renderDummy(mouseX, mouseY);
    }
}

