package mc.bape.module.movement;

import mc.bape.manager.FontManager;
import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import mc.bape.values.Option;
import mc.bape.vapu.Client;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class Sprint extends Module {
    private Option<Boolean> Sprint = new Option<Boolean>("KeepSprint","KeepSprint", false);
    public Sprint() {
        super("Sprint", Keyboard.KEY_NONE, ModuleType.Movement,"Force sprint when you moving");
        this.addValues(this.Sprint);
        Chinese="强制疾跑";
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.PlayerTickEvent event) {
        if(!mc.thePlayer.isCollidedHorizontally && mc.thePlayer.moveForward > 0) {
            //FontManager.C18.drawStringWithShadow("Sprinting (Toggled)",350,15,new Color(255, 255, 255).hashCode());
            mc.thePlayer.setSprinting(true);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(Client.nullCheck())
            return;
        if(this.Sprint.getValue()) {
            if(!mc.thePlayer.isSprinting()) mc.thePlayer.setSprinting(true);
        }
    }
}
