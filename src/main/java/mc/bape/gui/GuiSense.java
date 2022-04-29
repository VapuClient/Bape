package mc.bape.gui;

import net.minecraft.util.ResourceLocation;
import mc.bape.manager.FontManager;
import mc.bape.manager.Manager;
import net.minecraft.client.Minecraft;
import mc.bape.utils.RenderUtil;
import mc.bape.vapu.Client;

import java.awt.*;

public class GuiSense implements Manager {
    @Override
    public void init() {
        Minecraft mc = Minecraft.getMinecraft();
        String text = Client.CLIENT_NAME;
        FontManager.C18.drawStringWithShadow(" | " + (mc.isSingleplayer() ? "localhost:25565" : !mc.getCurrentServerData().serverIP.contains(":") ? mc.getCurrentServerData().serverIP + ":25565" : mc.getCurrentServerData().serverIP) + " | " + Minecraft.getDebugFPS() + "fps",63,25,new Color(255, 255, 255).hashCode());
        RenderUtil.drawImage(new ResourceLocation("JAT/Bape.png"),7,9,FontManager.C18.getStringWidth(text),40,255);
    }
}
