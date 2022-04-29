package mc.bape.utils;

import mc.bape.manager.FontManager;
import net.minecraft.client.gui.FontRenderer;

public class TextUtil {

    public static void drawCenteredString(String text, float x, float y, int color, boolean shadow) {
        FontManager.F18.drawString(text, x - FontManager.F18.getStringWidth(text) / 2f, y, color, shadow);
    }
}
