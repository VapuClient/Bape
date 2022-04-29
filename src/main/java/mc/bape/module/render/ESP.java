package mc.bape.module.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import mc.bape.module.combat.AntiBot;
import mc.bape.utils.*;
import mc.bape.utils.ColorUtil;
import mc.bape.values.Option;
import mc.bape.vapu.Client;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

public class ESP extends Module {

    private final Option<Boolean> invisible = new Option<>("Invisible", "invisible", true);
    private final Option<Boolean> redOnDamage = new Option<>("RedOnDamage", "redOnDamage", true);
    private final Option<Boolean> item = new Option<>("Item", "item", true);

    public ESP() {
        super("ESP", Keyboard.KEY_NONE, ModuleType.Player,"Draw boxes for other player and item");
        this.addValues(this.invisible,this.redOnDamage);
        Chinese="透视";
    }

    @SubscribeEvent
    public void onTick(final RenderWorldLastEvent event) {
        for (final EntityPlayer entity : mc.theWorld.playerEntities) {
            if (entity != mc.thePlayer && (!AntiBot.isServerBot(entity))) {
                if (!this.invisible.getValue() && entity.isInvisible()) {
                    return;
                }
                int rgb;
                rgb = ColorUtil.getRainbow().getRGB();
                RenderHelper.drawESP(entity, rgb, this.redOnDamage.getValue(), (int) 2.0);
            }
        }
        if (item.getValue()) {
            for (Entity e : mc.theWorld.loadedEntityList) {
                if (!(e instanceof EntityItem))
                    continue;
                RenderManager renderManager = mc.getRenderManager();
                try {
                    double renderPosX = (double) ReflectionUtil.getFieldValue(renderManager, "renderPosX");
                    double renderPosY = (double) ReflectionUtil.getFieldValue(renderManager, "renderPosY");
                    double renderPosZ = (double) ReflectionUtil.getFieldValue(renderManager, "renderPosZ");
                    final double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * Objects.requireNonNull(Client.getTimer()).renderPartialTicks
                            - renderPosX;
                    final double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * Objects.requireNonNull(Client.getTimer()).renderPartialTicks
                            - renderPosY;
                    final double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * Objects.requireNonNull(Client.getTimer()).renderPartialTicks
                            - renderPosZ;
                    final AxisAlignedBB entityBox = e.getEntityBoundingBox();
                    final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                            entityBox.minX - e.posX + x - 0.05D,
                            entityBox.minY - e.posY + y,
                            entityBox.minZ - e.posZ + z - 0.05D,
                            entityBox.maxX - e.posX + x + 0.05D,
                            entityBox.maxY - e.posY + y + 0.15D,
                            entityBox.maxZ - e.posZ + z + 0.05D
                    );
                    GlStateManager.pushMatrix();
                    RenderUtil.R2DUtils.enableGL2D();
                    RenderUtils.glColor(ColorUtil.getRainbow());
                    RenderUtil.drawOutlinedBoundingBox(axisAlignedBB);
                    RenderUtil.R2DUtils.disableGL2D();
                    GlStateManager.popMatrix();
                } catch (NullPointerException ex){
//                    Helper.sendMessage("LLLLL");
                }
            }
        }
    }
}
