package mc.bape.module.player;

import mc.bape.utils.BlockUtils;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import org.lwjgl.input.Keyboard;

public class AutoTools extends Module {
    public AutoTools() {
        super("AutoTools", Keyboard.KEY_NONE, ModuleType.Player,"Switch correct tools when you destroy blocks");
        Chinese="自动工具";
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (!mc.gameSettings.keyBindAttack.isKeyDown()) {
            return;
        }
        if (mc.objectMouseOver == null) {
            return;
        }
        BlockPos pos = mc.objectMouseOver.getBlockPos();
        if (pos == null) {
            return;
        }
        BlockUtils.updateTool(pos);
    }
}
