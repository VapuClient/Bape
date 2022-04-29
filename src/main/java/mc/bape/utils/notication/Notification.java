package mc.bape.utils.notication;

import java.awt.Color;

import mc.bape.manager.FontManager;
import mc.bape.module.render.HUD;
import mc.bape.utils.Colors;
import mc.bape.utils.RenderUtil;
import mc.bape.utils.math.MathUtil;
import mc.bape.vapu.Client;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class Notification {

    private String message;
    private TimeHelper timer;
    private double lastY, posY, width, height, animationX;
    private int color, imageWidth;
    private ResourceLocation image;
    private long stayTime;

    Minecraft mc = Minecraft.getMinecraft();

    public Notification(String message, Type type) {
        this.message = message;
        timer = new TimeHelper();
        timer.reset();
        width = FontManager.F16.getStringWidth(message) + 35;
        height = 20;
        animationX = 0;
        stayTime = 1500;
        imageWidth = 16;
        posY = -1;
        image = new ResourceLocation("notification/" + type.name().toUpperCase() + ".png");
        if (type.equals(Type.INFO))
            color = Colors.DARKGREY.c;
        else if (type.equals(Type.ERROR))
            color = new Color(36, 36, 36).getRGB();
        else if (type.equals(Type.SUCCESS))
            color = new Color(36, 36, 36).getRGB();
        else if (type.equals(Type.WARNING))
            color = Colors.DARKGREY.c;
    }

    public String getMessage() {
        return message;
    }

    public void drawBlack2(double getY, double lastY) {
        width = FontManager.F16.getStringWidth(message) + 45;
        height = 22;
        imageWidth = 11;

        if (animationX < width && !timer.isDelayComplete(stayTime)) {
            animationX += 240f / mc.getDebugFPS();
        }
        if (timer.isDelayComplete(stayTime)) {
            animationX -= 240f / mc.getDebugFPS();
        }
        if (posY == -1)
            posY = getY;
        else
            posY = getY;

        //animationX = this.getAnimationState(animationX, isFinished() ? width : 0, Math.max(isFinished() ? 200 : 30, Math.abs(animationX - (isFinished() ? width : 0)) * 20) * 0.3);
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        int x1 = (int) (res.getScaledWidth() - animationX), x2 = (int) (res.getScaledWidth() + animationX), y1 = (int) posY - 22, y2 = (int) (y1 + height);
        GL11.glPushMatrix();
        RenderUtil.drawRect(x1, y1, x2, y2, MathUtil.reAlpha(color, 0.8f));
        this.drawImage(image, (int) (x1 + (height - imageWidth) / 2F) - 1, y1 + (int) ((height - imageWidth) / 2F), imageWidth, imageWidth);
        if (!timer.isDelayComplete(stayTime)) {
            RenderUtil.drawRect((float) x1, (float) (y2 - 1.5), x2 - (x2 - x1) * (float) ((System.currentTimeMillis() - timer.lastMs) / (float) stayTime), y2, MathUtil.reAlpha(Colors.BLUE.c, 1f));
        }
        //System.out.println((System.currentTimeMillis() - timer.lastMs) / (float)stayTime);
        y1 += 1;
        if (message.contains(" Enabled")) {
            FontManager.F16.drawString(message.replace(" Enabled", ""), (float) (x1 + 19), (float) (y1 + height / 4F), -1);
            FontManager.F16.drawString(" Enabled", (float) (x1 + 20 + FontManager.F16.getStringWidth(message.replace(" Enabled", ""))), (float) (y1 + height / 4F), Colors.GREY.c);
        } else if (message.contains(" Disabled")) {
            FontManager.F16.drawString(message.replace(" Disabled", ""), (float) (x1 + 19), (float) (y1 + height / 4F), -1);
            FontManager.F16.drawString(" Disabled", (float) (x1 + 20 + FontManager.F16.getStringWidth(message.replace(" Disabled", ""))), (float) (y1 + height / 4F), Colors.GREY.c);
        } else {
            FontManager.F16.drawString(message, (float) (x1 + 20), (float) (y1 + height / 4F), -1);
        }
        GL11.glColor3f(1, 1, 1);
        GL11.glPopMatrix();
    }

    public void drawBlack1(double getY, double lastY) {
        width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(message) + 25;
        height = 22;
        imageWidth = 11;
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

        if (animationX < width && !timer.isDelayComplete(stayTime)) {
            animationX += 240f / mc.getDebugFPS();
        }
        if (timer.isDelayComplete(stayTime)) {
            animationX -= 240f / mc.getDebugFPS();
        }
        posY = getY;

        //animationX = this.getAnimationState(animationX, isFinished() ? width : 0, Math.max(isFinished() ? 200 : 30, Math.abs(animationX - (isFinished() ? width : 0)) * 20) * 0.3);
        int x1 = (int) (res.getScaledWidth() - animationX), x2 = (int) (res.getScaledWidth() + animationX), y1 = (int) posY - 22, y2 = (int) (y1 + height);
        GL11.glPushMatrix();
        RenderUtil.drawRect(x1, y1, x2, y2, new Color(30, 30, 30, 230).getRGB());
        this.drawImage(image, (int) (x1 + (height - imageWidth) / 2F) - 1, y1 + (int) ((height - imageWidth) / 2F), imageWidth, imageWidth, new Color(Colors.WHITE.c));
        if (!timer.isDelayComplete(stayTime)) {
            RenderUtil.drawRect((float) x1, (float) (y2 - 1.5), x2 - (x2 - x1) * (float) ((System.currentTimeMillis() - timer.lastMs) / (float) stayTime), y2, new Color(107, 157, 255).getRGB());
        }
        //System.out.println((System.currentTimeMillis() - timer.lastMs) / (float)stayTime);
        y1 += 1;
        if (message.contains(" Enabled")) {
            FontManager.F20.drawString(message.replace(" Enabled", ""), (int) (x1 + 19), (int) (y1 + height / 4F), new Color(107, 157, 255).getRGB());
            FontManager.F20.drawString(" Enabled", (int) (x1 + 20 + FontManager.F22.getStringWidth(message.replace(" Enabled", ""))), (int) (y1 + height / 4F), Colors.WHITE.c);
        } else if (message.contains(" Disabled")) {
            FontManager.F20.drawString(message.replace(" Disabled", ""), (int) (x1 + 19), (int) (y1 + height / 4F), new Color(107, 157, 255).getRGB());
            FontManager.F20.drawString(" Disabled", (int) (x1 + 20 + FontManager.F22.getStringWidth(message.replace(" Disabled", ""))), (int) (y1 + height / 4F), Colors.WHITE.c);
        } else {
            FontManager.F20.drawString(message, (int) (x1 + 20), (int) (y1 + height / 4F), -1);
        }
        GL11.glColor3f(1, 1, 1);
        GL11.glPopMatrix();
    }

    public boolean shouldDelete() {
        //return false;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return timer.isDelayComplete(stayTime) && animationX <= 0;
    }

    private boolean isFinished() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        return timer.isDelayComplete(stayTime);
    }

    public double getHeight() {
        return height;
    }

    public enum Type {
        SUCCESS, INFO, WARNING, ERROR
    }

    public double getAnimationState(double animation, double finalState, double speed) {
        float add = (float) (RenderUtil.delta * speed);
        if (animation < finalState) {
            if (animation + add < finalState)
                animation += add;
            else
                animation = finalState;
        } else {
            if (animation - add > finalState)
                animation -= add;
            else
                animation = finalState;
        }
        return animation;
    }

    public void drawImage(ResourceLocation image, int x, int y, int width, int height) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public void drawImage(ResourceLocation image, int x, int y, int width, int height, Color color) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        //GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtil.color(color.getRGB());
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}