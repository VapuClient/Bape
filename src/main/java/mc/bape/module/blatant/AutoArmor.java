package mc.bape.module.blatant;

import mc.bape.manager.ModuleManager;
import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import mc.bape.utils.TimerUtil;
import mc.bape.values.Mode;
import mc.bape.values.Numbers;
import mc.bape.values.Option;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class AutoArmor extends Module {
    public static Numbers<Double> DELAY = new Numbers<Double>("DELAY", "DELAY", 1.0, 0.0, 10.0, 1.0);
    public static Mode<Enum> MODE = new Mode("MODE", "MODE", (Enum[])EMode.values(), (Enum)EMode.Basic);
    private TimerUtil timer = new TimerUtil();

    public AutoArmor() {
        super("AutoArmor", Keyboard.KEY_NONE, ModuleType.Blatant,"Auto put armor on");
        this.addValues(DELAY, MODE);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (this.isMoving()) {
            return;
        }
//        if (ModuleManager.getModule("InvCleaner").getState()) {
//            return;
//        }
        long delay = ((Double)DELAY.getValue()).longValue() * 50L;
        if (MODE.getValue() == EMode.OpenInv && !(AutoArmor.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if ((AutoArmor.mc.currentScreen == null || AutoArmor.mc.currentScreen instanceof GuiInventory || AutoArmor.mc.currentScreen instanceof GuiChat) && this.timer.hasReached(delay)) {
            this.getBestArmor();
        }
    }

    public void getBestArmor() {
        int type = 1;
        while (type < 5) {
            block10: {
                block9: {
                    if (!mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) break block9;
                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                    if (AutoArmor.isBestArmor(is, type)) break block10;
                    if (this.MODE.getValue() == EMode.FakeInv) {
                        C16PacketClientStatus p = new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT);
                        mc.thePlayer.sendQueue.addToSendQueue(p);
                    }
                    this.drop(4 + type);
                }
                int i = 9;
                while (i < 45) {
                    if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                        if (AutoArmor.isBestArmor(is, type) && AutoArmor.getProtection(is) > 0.0f) {
                            this.shiftClick(i);
                            this.timer.reset();
                            if (((Double)DELAY.getValue()).longValue() > 0L) {
                                return;
                            }
                        }
                    }
                    ++i;
                }
            }
            ++type;
        }
    }

    public static boolean isBestArmor(ItemStack stack, int type) {
        float prot = AutoArmor.getProtection(stack);
        String strType = "";
        if (type == 1) {
            strType = "helmet";
        } else if (type == 2) {
            strType = "chestplate";
        } else if (type == 3) {
            strType = "leggings";
        } else if (type == 4) {
            strType = "boots";
        }
        if (!stack.getUnlocalizedName().contains(strType)) {
            return false;
        }
        int i = 5;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (AutoArmor.getProtection(is) > prot && is.getUnlocalizedName().contains(strType)) {
                    return false;
                }
            }
            ++i;
        }
        return true;
    }

    public void shiftClick(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
    }

    public void drop(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    }

    public static float getProtection(ItemStack stack) {
        float prot = 0.0f;
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)stack.getItem();
            prot = (float)((double)prot + ((double)armor.damageReduceAmount + (double)((100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075));
            prot = (float)((double)prot + (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100.0);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100.0);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) / 100.0);
        }
        return prot;
    }

    public boolean isMoving() {
        return (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F);
    }
    public static enum EMode {
        Basic,
        OpenInv,
        FakeInv;
    }
}
