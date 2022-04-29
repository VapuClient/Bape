package mc.bape.legit.hud.mod.impl;

import mc.bape.legit.hud.mod.HudMod;
import mc.bape.manager.FontManager;
import mc.bape.manager.ModuleManager;
import mc.bape.module.render.HUD;
import mc.bape.utils.RenderUtil;
import mc.bape.utils.RenderUtils;
import mc.bape.values.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class InventoryHUD extends HudMod {
    Option<Boolean> option = HUD.InventoryHUD;

    public InventoryHUD() {
        super("InventoryHUD", 10, 20);
    }

    @Override
    public void draw() {
        if(option.getValue()) {
            GL11.glPushMatrix();
            RenderUtil.drawRect(getX(), getY() - 10, getX() + 182, getY() + 1, new Color(49, 147, 255, 255).getRGB());
            FontManager.F18.drawStringWithShadow("Inventory HUD", getX() + 1, getY() - 7, new Color(255, 255, 255, 255).getRGB());
            RenderUtil.drawRect(getX(), getY(), getX() + (20 * 9) + 2, getY() + (20 * 3) + 2, new Color(0, 0, 0, 160).getRGB());
            RenderHelper.enableGUIStandardItemLighting();
            for (int i = 0; i < 27; i++) {
                ItemStack[] itemStack = mc.thePlayer.inventory.mainInventory;
                int offsetX = getX() + 2 + (i % 9) * 20;
                int offsetY = getY() + (i / 9) * 20;
                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack[i + 9], offsetX, offsetY);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, itemStack[i + 9], offsetX, offsetY, null);
            }
            GL11.glPopMatrix();
        }

        super.draw();
    }

    @Override
    public void renderDummy(int mouseX, int mouseY) {
        if(option.getValue()) {
            GL11.glPushMatrix();
            RenderUtil.drawRect(getX(), getY() - 9, getX() + 182, getY() + 1, new Color(49, 147, 255, 255).getRGB());
            FontManager.F18.drawStringWithShadow("Inventory HUD", getX() + 1, getY() - 7, new Color(255, 255, 255, 255).getRGB());
            RenderUtil.drawRect(getX(), getY(), getX() + (20 * 9) + 2, getY() + (20 * 3) + 2, new Color(0, 0, 0, 160).getRGB());
            RenderHelper.enableGUIStandardItemLighting();
            for (int i = 0; i < 27; i++) {
                ItemStack[] itemStack = mc.thePlayer.inventory.mainInventory;
                int offsetX = getX() + 2 + (i % 9) * 20;
                int offsetY = getY() + (i / 9) * 20;
                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack[i + 9], offsetX, offsetY);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, itemStack[i + 9], offsetX, offsetY, null);
            }
            GL11.glPopMatrix();
        }


        super.renderDummy(mouseX, mouseY);
    }

}
