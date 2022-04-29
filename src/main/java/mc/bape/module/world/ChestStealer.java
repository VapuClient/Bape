package mc.bape.module.world;

import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import mc.bape.utils.TimerUtil;
import mc.bape.values.Numbers;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class ChestStealer extends Module {
    private Numbers<Double> delay = new Numbers<Double>("Delay", "delay", 50.0, 0.0, 1000.0, 10.0);
    private TimerUtil timer = new TimerUtil();
    public ChestStealer() {
        super("ChestStealer", Keyboard.KEY_NONE, ModuleType.Blatant,"Auto Steal a chest when you open it");
        this.addValues(this.delay);
        Chinese="快速刷箱";
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.PlayerTickEvent event) {
            if(!this.getState())
            return;
            if (this.mc.thePlayer.openContainer != null && this.mc.thePlayer.openContainer instanceof ContainerChest) {
                ContainerChest container = (ContainerChest)this.mc.thePlayer.openContainer;
                int i = 0;
                while (i < container.getLowerChestInventory().getSizeInventory()) {
                    if (container.getLowerChestInventory().getStackInSlot(i) != null && this.timer.hasReached(this.delay.getValue())) {
                        mc.playerController.windowClick(container.windowId, i, 0, 1, this.mc.thePlayer);
                        this.timer.reset();
                    }
                    ++i;
                }
                if (this.isEmpty()) {
                    this.mc.thePlayer.closeScreen();
                }
            }
    }

    private boolean isEmpty() {
        if (this.mc.thePlayer.openContainer != null && this.mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest container = (ContainerChest)this.mc.thePlayer.openContainer;
            int i = 0;
            while (i < container.getLowerChestInventory().getSizeInventory()) {
                ItemStack itemStack = container.getLowerChestInventory().getStackInSlot(i);
                if (itemStack != null && itemStack.getItem() != null) {
                    return false;
                }
                ++i;
            }
        }
        return true;
    }

}
