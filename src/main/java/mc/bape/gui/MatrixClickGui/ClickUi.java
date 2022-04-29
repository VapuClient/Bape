package mc.bape.gui.MatrixClickGui;

import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import mc.bape.manager.ModuleManager;
import mc.bape.module.ModuleType;
import mc.bape.module.render.ClickGUI;
import mc.bape.utils.RenderUtils;
import mc.bape.values.Mode;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import static mc.bape.utils.render.ColorUtils.reAlpha;

public class ClickUi extends GuiScreen {
    public static ArrayList<Window> windows = Lists.newArrayList();
    public static boolean binding = false;
    public double opacity = 0.0;
    public int scrollVelocity;
    private int scroll;
    Mode<Enum> mode = ClickGUI.SexyMode;

    public ClickUi() {
        updateScreen();
        if (windows.isEmpty()) {
            int x = 5;
            ModuleType[] arrmoduleType = ModuleType.values();
            int n = arrmoduleType.length;
            int n2 = 0;
            while (n2 < n) {
                ModuleType c = arrmoduleType[n2];
                windows.add(new Window(c, x, 5));
                x += 105;
                ++n2;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.translate(0,scroll,0);
        mouseY-=scroll;
        this.opacity = this.opacity + 10.0 < 200.0 ? (this.opacity += 10.0) : 200.0;
        drawRect((int) 0.0, (int) 0.0, Display.getWidth(), Display.getHeight(), reAlpha(1, 0.3F));// ����
        ScaledResolution res = new ScaledResolution(this.mc);
        GlStateManager.pushMatrix();
        ScaledResolution scaledRes = new ScaledResolution(this.mc);
        float scale = (float) scaledRes.getScaleFactor() / (float) Math.pow(scaledRes.getScaleFactor(), 2.0);
        int finalMouseY = mouseY;
        windows.forEach(w -> w.render(mouseX, finalMouseY));

        int finalMouseY1 = mouseY;
        windows.forEach(w -> w.mouseScroll(mouseX, finalMouseY1, this.scrollVelocity));
        super.drawScreen(mouseX, mouseY, partialTicks);

        GlStateManager.popMatrix();
        if (mode.getValue()  == ClickGUI.SexyMode1.None) {
            RenderUtils.drawImage(800,600, 100, 55, new ResourceLocation("JAT/BapelogoR.png"), new Color(255, 255, 255));
//            case "none":
//                break;
//            case "keqing":
//                RenderUtils.drawImage(2,65, 300, 470, new ResourceLocation("JAT/H2.png"), new Color(220, 220, 220));
//            case "mona":
        }else if(mode.getValue() == ClickGUI.SexyMode1.Mona){
            //RenderUtils.drawImage(240,240, 50, 70, new ResourceLocation("JAT/BapelogoR.png"), new Color(255, 255, 255));
            RenderUtils.drawImage(700,65, 300, 470, new ResourceLocation("JAT/Mona.png"), new Color(220, 220, 220));
            RenderUtils.drawImage(835,440, 120, 55, new ResourceLocation("JAT/BapelogoR.png"), new Color(255, 255, 255));
          }else if(mode.getValue() == ClickGUI.SexyMode1.KeQing){
            //RenderUtils.drawImage(240,240, 50, 70, new ResourceLocation("JAT/BapelogoR.png"), new Color(255, 255, 255));
            RenderUtils.drawImage(2,65, 300, 470, new ResourceLocation("JAT/H2.png"), new Color(220, 220, 220));
            RenderUtils.drawImage(835,440, 120, 55, new ResourceLocation("JAT/BapelogoR.png"), new Color(255, 255, 255));
//
        }else if(mode.getValue() == ClickGUI.SexyMode1.Misaka){
            RenderUtils.drawImage(835,440, 120, 55, new ResourceLocation("JAT/BapelogoR.png"), new Color(255, 255, 255));
//
            // RenderUtils.drawImage(240,240, 50, 70, new ResourceLocation("JAT/BapelogoR.png"), new Color(255, 255, 255));
            RenderUtils.drawImage(350,65, 300, 470, new ResourceLocation("JAT/Misaka.png"), new Color(220, 220, 220));
        }else if(mode.getValue() == ClickGUI.SexyMode1.zerotwo){
            RenderUtils.drawImage(835,440, 120, 55, new ResourceLocation("JAT/BapelogoR.png"), new Color(255, 255, 255));
            RenderUtils.drawImage(350,95, 330, 400, new ResourceLocation("JAT/02.png"), new Color(220, 220, 220));
        }

        if (Mouse.hasWheel()) {
            int wheel = Mouse.getDWheel();
            this.scrollVelocity = wheel < 0 ? -120 : (wheel > 0 ? 120 : 0);
            if(wheel<0){
                scroll-=15;
            } else if(wheel>0){
                scroll+=15;
                if(scroll>0){
                    scroll=0;
                }
            }
        }

        //RenderUtils.drawImage(90,500, 1700, 2466, new ResourceLocation("JAT/H2.png"), new Color(220, 220, 220));


    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        mouseY-=scroll;
        int finalMouseY = mouseY;
        windows.forEach(w -> w.click(mouseX, finalMouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1 && !binding) {
            this.mc.displayGuiScreen(null);
            return;
        }
        windows.forEach(w -> w.key(typedChar, keyCode));
    }

    public void initGui() {
        super.initGui();
    }
    public void onGuiClosed() {
        ModuleManager.getModule("ClickGui").setState(false);
    }

    public synchronized void sendToFront(Window window) {
        RenderUtils.drawImage(105,105, 160, 200, new ResourceLocation("vapeclickgui/module.png"), new Color(220, 220, 220));
        int panelIndex = 0;
        int i = 0;
        while (i < windows.size()) {
            if (windows.get(i) == window) {
                panelIndex = i;
                break;
            }
            ++i;
        }
        Window t = windows.get(windows.size() - 1);
        windows.set(windows.size() - 1, windows.get(panelIndex));
        windows.set(panelIndex, t);
    }
}
