package mc.bape.legit.hud;

import mc.bape.legit.hud.mod.HudMod;
import mc.bape.vapu.Client;
import net.minecraft.client.gui.GuiScreen;

public class HUDConfigScreen extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        for (HudMod m : Client.INSTANCE.hudManager.hudMods) {
            m.renderDummy(mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
