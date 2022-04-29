package mc.bape.module.player;

import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import mc.bape.vapu.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;

public class NoJumpDelay extends Module {

    private final Field jumpTicksFiled;
    private final Field nextStepDistanceFiled;

    public NoJumpDelay() {
        super("NoJumpDelay", Keyboard.KEY_NONE, ModuleType.Player, "disable jump delay");

        this.jumpTicksFiled = ReflectionHelper.findField(EntityLivingBase.class, "field_70773_bE", "jumpTicks");
        this.nextStepDistanceFiled = ReflectionHelper.findField(Entity.class, "field_70150_b", "nextStepDistance");
        if (this.jumpTicksFiled != null)
            this.jumpTicksFiled.setAccessible(true);
        if (this.nextStepDistanceFiled != null)
            this.nextStepDistanceFiled.setAccessible(true);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent e){
        if (Client.nullCheck() && this.jumpTicksFiled == null && this.nextStepDistanceFiled == null) {
            if (!mc.inGameHasFocus || mc.thePlayer.capabilities.isCreativeMode) {
                return;
            }
        }

        try {
            jumpTicksFiled.set(mc.thePlayer, 0);
            nextStepDistanceFiled.set(mc.thePlayer, 0);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            this.disable();
        }
    }
}
