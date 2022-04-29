package mc.bape.module.combat;

import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import mc.bape.module.blatant.Killaura;
import mc.bape.utils.TimerUtil;
import mc.bape.values.Numbers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import mc.bape.values.Option;
import org.lwjgl.input.Keyboard;

public class AutoClicker extends Module {
    private final TimerUtil timer = new TimerUtil();
    private Numbers<Double> cps = new Numbers<Double>("CPSMax", "CpsMax",5.0, 1.0, 20.0,1.0);
    private Numbers<Double> cpsMin = new Numbers<Double>("CPSMin", "CpsMin",5.0, 1.0, 20.0,1.0);
    private Option<Boolean> autoblock = new Option<Boolean>("AutoBlock","AutoBlock", false);
    public boolean doBlock = true;
    public AutoClicker() {
        super("AutoClicker", Keyboard.KEY_NONE, ModuleType.Combat,"auto Attack when you hold the attack button");
        this.addValues(this.cps,cpsMin,this.autoblock);
        Chinese="连点器";
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        try {
            this.status = this.cps.getValue().toString();
            int key = mc.gameSettings.keyBindAttack.getKeyCode();
            if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                float delays = (float) (Killaura.getRandomDoubleInRange(cpsMin.getValue(), cps.getValue()) + 2);
                if (timer.delay(delays * 10)) {
                    mc.thePlayer.swingItem();
                    KeyBinding.onTick(key);
                    try {
                        if ((Boolean) this.autoblock.getValue()){
                            if (mc.thePlayer.getCurrentEquippedItem() == null) {
                                return;
                            }
                            if (!(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword)) {
                                return;
                            }
                            if (this.autoblock.getValue() && mc.objectMouseOver.entityHit != null && mc.objectMouseOver.entityHit.isEntityAlive()){
                                if (mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword && timer.delay(100)) {
                                    mc.thePlayer.getCurrentEquippedItem().useItemRightClick(mc.theWorld, mc.thePlayer);
                                    timer.reset();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    timer.reset();
                }
            }
        } catch (NullPointerException e){
//            Helper.sendMessage("傻逼，你他妈会不会写代码，你写的代码全是" + e.toString());
        }
    }
}
