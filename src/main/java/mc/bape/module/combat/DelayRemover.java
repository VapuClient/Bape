package mc.bape.module.combat;

import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;

public class DelayRemover extends Module {

    private final Field leftClickCounterField;

    public DelayRemover() {
        super ("DelayRemover", Keyboard.KEY_NONE, ModuleType.Combat, "Remover your click delay");

        this.leftClickCounterField = ReflectionHelper.findField(Minecraft.class, "field_71429_W", "leftClickCounter");
        if (this.leftClickCounterField != null)
            this.leftClickCounterField.setAccessible(true);
    }

    @SubscribeEvent
    public void playerTickEvent(TickEvent.PlayerTickEvent e) {
        if (isPlayerInGame() && this.leftClickCounterField != null) {
            if (!mc.inGameHasFocus || mc.thePlayer.capabilities.isCreativeMode) {
                return;
            }
        }

        try {
            leftClickCounterField.set(mc, 0);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            this.disable();
        }
    }

    public static boolean isPlayerInGame() {
        return mc.thePlayer != null && mc.theWorld != null;
    }

}