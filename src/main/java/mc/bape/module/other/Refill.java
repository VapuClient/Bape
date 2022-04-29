package mc.bape.module.other;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import mc.bape.utils.TimerUtil;
import mc.bape.values.Mode;
import mc.bape.values.Numbers;
import mc.bape.values.Option;
import org.lwjgl.input.Keyboard;

public class Refill extends Module {
    TimerUtil timer = new TimerUtil();
    Item TargetItem;
    private final Numbers<Double> delay = new Numbers<Double>("Delay", "Delay",100.0, 50.0, 1000.0,1.0);
    private final Option<Boolean> OpenInv = new Option<Boolean>("OpenInv","OpenInv", false);
    private final Mode<Enum> mode = new Mode("Mode", "mode", (Enum[]) Refill.RefillMode.values(), (Enum) Refill.RefillMode.Pot);


    static enum RefillMode {
        Pot,
        Soup
    }

    public Refill() {
        super("Refill", Keyboard.KEY_NONE, ModuleType.Other, "Refill your hot bar soup or pot");
        this.addValues(this.delay,this.OpenInv,this.mode);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.PlayerTickEvent event) {
        if (this.mode.getValue() == RefillMode.Soup) {
            this.TargetItem = Items.mushroom_stew;
        } else if (this.mode.getValue() == RefillMode.Pot) {
            ItemPotion itempotion = Items.potionitem;
            this.TargetItem = ItemPotion.getItemById(373);
        }

        this.refill();
    }

    private void refill() {
        if (!this.OpenInv.getValue() || mc.currentScreen instanceof GuiInventory) {
            if (!isHotbarFull() && this.timer.hasReached(delay.getValue())) {
                refill(this.TargetItem);
                this.timer.reset();
            }
        }
    }


    public static boolean isHotbarFull() {
        for (int i = 0; i <= 36; ++i) {
            ItemStack itemstack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemstack == null) {
                return false;
            }
        }

        return true;
    }


    public static void refill(Item value) {
        for (int i = 9; i < 37; ++i) {
            ItemStack itemstack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (itemstack != null && itemstack.getItem() == value) {
                mc.playerController.windowClick(0, i, 0, 1, mc.thePlayer);
                break;
            }
        }
    }
}
