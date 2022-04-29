package mc.bape.utils;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class Nameplate {
    private  double y;
    private  int width;
    private  String name;
    private  EntityLivingBase owner;
    private  Minecraft mc;
    private  RenderManager rm;
    private  double z;
    private  double x;

    public void renderNewPlate(Color col) {
        float distance = mc.thePlayer.getDistanceToEntity(owner);
        float absDistance = Math.abs(distance / 4.0f);
        
        float lllllIIlIIlIlll = 1.6f;
        float lllllIIlIIlIllI = 0.022133334f;
        float lllllIIlIIlIlIl = 2.0f;
        
        float v = (float)(-width) - 2.0f;
        
        float lllllIIlIIlIIll = -2.0f;
        
        float v1 = (float)width + 2.0f;
        
        float lllllIIlIIlIIIl = 10.0f;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)((float)x + 0.0f), ((float)y + owner.height + 0.5f), ((float)z));
        GL11.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)(-rm.playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)rm.playerViewX, (float)1.0f, (float)0.0f, (float)0.0f);
        GlStateManager.scale((float)-0.022133334f, (float)-0.022133334f, (float)0.022133334f);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        boolean lllllIIlIIIlllI = false;
        int width = rm.getFontRenderer().getStringWidth(name) / 2;
        GlStateManager.disableTexture2D();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos((double)(-width - 1), -1.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
        worldRenderer.pos((double)(-width - 1), 8.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
        worldRenderer.pos((double)(width + 1), 8.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
        worldRenderer.pos((double)(width + 1), -1.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
        tessellator.draw();
        GL11.glTranslated((double)0.0, (double)(-(absDistance * 7.0f)), (double)0.0);
        GL11.glScaled((double)absDistance, (double)absDistance, (double)absDistance);
        Gui.drawRect((int)(v), -2, (int)(v1), 10, 0x3F000000);
        GlStateManager.enableTexture2D();
        rm.getFontRenderer().drawString(name, -rm.getFontRenderer().getStringWidth(name) / 2, 0, 0x20FFFFFF);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        rm.getFontRenderer().drawString(name, -rm.getFontRenderer().getStringWidth(name) / 2, 0, -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.popMatrix();
    }

    public Nameplate(String Name, double X, double Y, double Z, EntityLivingBase livingBase) {
        mc = Minecraft.getMinecraft();
        name = Name;
        x = X;
        y = Y;
        z = Z;
        owner = livingBase;
        width = mc.fontRendererObj.getStringWidth(name) / 2;
        rm = mc.getRenderManager();
    }
}

