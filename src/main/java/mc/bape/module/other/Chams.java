package mc.bape.module.other;

import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class Chams extends Module {
    public Chams() {
        super("Chams", Keyboard.KEY_NONE, ModuleType.Other, "Allows you to Xray other players");
    }

    @SubscribeEvent
    public void onRenderPlayer(final RenderPlayerEvent.Pre e) {
        GL11.glEnable(32823);
        GL11.glPolygonOffset(1.0f, -1100000.0f);
    }

    @SubscribeEvent
    public void onRenderPlayer(final RenderPlayerEvent.Post e) {
        GL11.glDisable(32823);
        GL11.glPolygonOffset(1.0f, 1100000.0f);
    }

}
