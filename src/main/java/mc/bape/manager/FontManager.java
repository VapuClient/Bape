package mc.bape.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import mc.bape.gui.font.CFontRenderer;

import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;

public abstract class FontManager {

    public static CFontRenderer F14 = new CFontRenderer(FontManager.getFont(14), true, true);
    public static CFontRenderer F16 = new CFontRenderer(FontManager.getFont(16), true, true);
    public static CFontRenderer F18 = new CFontRenderer(FontManager.getFont(18), true, true);
    public static CFontRenderer F20 = new CFontRenderer(FontManager.getFont(20), true, true);
    public static CFontRenderer F22 = new CFontRenderer(FontManager.getFont(22), true, true);
    public static CFontRenderer F23 = new CFontRenderer(FontManager.getFont(23), true, true);
    public static CFontRenderer F24 = new CFontRenderer(FontManager.getFont(24), true, true);
    public static CFontRenderer F30 = new CFontRenderer(FontManager.getFont(30), true, true);
    public static CFontRenderer F40 = new CFontRenderer(FontManager.getFont(40), true, true);
    public static CFontRenderer C12 = new CFontRenderer(FontManager.getComfortaa(12), true, true);
    public static CFontRenderer C14 = new CFontRenderer(FontManager.getComfortaa(14), true, true);
    public static CFontRenderer C16 = new CFontRenderer(FontManager.getComfortaa(16), true, true);
    public static CFontRenderer C18 = new CFontRenderer(FontManager.getComfortaa(18), true, true);
    public static CFontRenderer C20 = new CFontRenderer(FontManager.getComfortaa(20), true, true);
    public static CFontRenderer C22 = new CFontRenderer(FontManager.getComfortaa(22), true, true);
    public static CFontRenderer C30 = new CFontRenderer(FontManager.getComfortaa(30), true, true);

    public static CFontRenderer Logo = new CFontRenderer(FontManager.getNovo(40), true, true);

    public static ArrayList<CFontRenderer> fonts = new ArrayList();

    public static CFontRenderer getFontRender(int size) {
        return fonts.get(size - 10);
    }

    public static Font getFont(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/AlibabaSans-Regular.otf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }


    public static Font getComfortaa(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("font/AlibabaSans-Regular.otf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    public static Font getNovo(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("font/NovICON.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

}

