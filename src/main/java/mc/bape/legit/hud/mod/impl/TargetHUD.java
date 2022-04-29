package mc.bape.legit.hud.mod.impl;

import mc.bape.gui.font.CFontRenderer;
import mc.bape.legit.hud.mod.HudMod;
import mc.bape.manager.FontManager;
import mc.bape.module.blatant.Killaura;
import mc.bape.module.render.HUD;
import mc.bape.utils.ColorUtils;
import mc.bape.utils.PlayerUtils;
import mc.bape.utils.RenderUtils;
import mc.bape.utils.TextUtil;
import mc.bape.values.Option;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TargetHUD extends HudMod {
    EntityLivingBase target;
    private float easingHP = 0f;

    Option<Boolean> option = HUD.TargetHUD;

    public TargetHUD() {
        super("TargetHUD", 500, 270);
    }

    @Override
    public void draw() {
        if (option.getValue()) {
            this.renderTargetHud();
        }
        super.draw();
    }

    @Override
    public void renderDummy(int mouseX, int mouseY) {
        target = mc.thePlayer;
        easingHP = getHealth(target);

        if (option.getValue()) {
            CFontRenderer font = FontManager.F18;
            int additionalWidth = Math.max(font.getStringWidth(target.getName()), 75);
            RenderUtils.drawRoundedCornerRect(getX() + 0.0F, getY() + 0.0F, getX() + 45.0F + (float)additionalWidth, getY() + 40.0F, 7.0F, (new Color(0, 0, 0, 110)).getRGB());

            // circle player avatar
            GL11.glColor4f(1F, 1F, 1F, 1F);
            mc.getTextureManager().bindTexture(PlayerUtils.getskin(target));
            RenderUtils.drawScaledCustomSizeModalCircle(getX() + 5, getY() + 5, 8f, 8f, 8, 8, 30, 30, 64f, 64f);
            RenderUtils.drawScaledCustomSizeModalCircle(getX() + 5, getY() + 5, 40f, 8f, 8, 8, 30, 30, 64f, 64f);

            // info text
            TextUtil.drawCenteredString(target.getName(), getX() + 40 + (additionalWidth / 2f), getY() + 5f, Color.WHITE.getRGB(), false);

            // hp bar
            RenderUtils.drawRoundedCornerRect(getX() + 40f, getY() + 28f, getX() + 40f + additionalWidth, getY() + 33f, 2.5f, new Color(0, 0, 0, 70).getRGB());
            RenderUtils.drawRoundedCornerRect(getX() + 40f, getY() + 28f, getX() + 40f + (easingHP / target.getMaxHealth()) * additionalWidth, getY() + 33f, 2.5f, ColorUtils.rainbow(100));
        }

        super.renderDummy(mouseX, mouseY);
    }

    private void renderTargetHud() {
        target = Killaura.target;
        easingHP = getHealth(target);

        if (target != null) {
            CFontRenderer font = FontManager.F18;
            int additionalWidth = Math.max(font.getStringWidth(target.getName()), 75);
            RenderUtils.drawRoundedCornerRect(getX() + 0.0F, getY() + 0.0F, getX() + 45.0F + (float)additionalWidth, getY() + 40.0F, 7.0F, (new Color(0, 0, 0, 110)).getRGB());

            // circle player avatar
            GL11.glColor4f(1F, 1F, 1F, 1F);
            try {
                mc.getTextureManager().bindTexture(PlayerUtils.getskin(target));
            } catch (Exception e) {
                mc.getTextureManager().bindTexture(PlayerUtils.getskin(mc.thePlayer));
            }
            RenderUtils.drawScaledCustomSizeModalCircle(getX() + 5, getY() + 5, 8f, 8f, 8, 8, 30, 30, 64f, 64f);
            RenderUtils.drawScaledCustomSizeModalCircle(getX() + 5, getY() + 5, 40f, 8f, 8, 8, 30, 30, 64f, 64f);

            // info text
            TextUtil.drawCenteredString(target.getName(), getX() + 40 + (additionalWidth / 2f), getY() + 5f, Color.WHITE.getRGB(), false);

            // hp bar
            RenderUtils.drawRoundedCornerRect(getX() + 40f, getY() + 28f, getX() + 40f + additionalWidth, getY() + 33f, 2.5f, new Color(0, 0, 0, 70).getRGB());
            RenderUtils.drawRoundedCornerRect(getX() + 40f, getY() + 28f, getX() + 40f + (easingHP / target.getMaxHealth()) * additionalWidth, getY() + 33f, 2.5f, ColorUtils.rainbow(100));
        }
    }

    private float getHealth(EntityLivingBase entity) {
        if (entity != null) {
            return entity.getHealth();
        }
        return 0f;
    }

    @Override
    public int getWidth() {
        return 100;
    }
}
