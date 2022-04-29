package mc.bape.module.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import mc.bape.module.ModuleType;
import mc.bape.utils.ReflectionUtil;
import mc.bape.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mc.bape.values.Numbers;
import mc.bape.values.Option;
import mc.bape.vapu.Client;
import mc.bape.event.PacketEvent;

import org.lwjgl.input.Keyboard;

public class Velocity extends Module {
    private Numbers<Double> horizontal = new Numbers<Double>("Horizontal", "Horizontal",96.0, 0.0, 100.0,1.0);
    private Numbers<Double> vertical = new Numbers<Double>("Vertical", "Vertical",100.0, 0.0, 100.0,1.0);
    private Numbers<Double> chance = new Numbers<Double>("Chance", "Chance",100.0, 0.0, 100.0,1.0);
    private Option<Boolean> Targeting = new Option<Boolean>("On Targeting","On Targeting", false);
    private Option<Boolean> NoSword = new Option<Boolean>("No Sword","No Sword", false);
    public Velocity() {
        super("Velocity", Keyboard.KEY_NONE, ModuleType.Combat,"Reduces your knockback");
        this.addValues(this.horizontal,this.vertical,this.chance,this.Targeting,this.NoSword);
        Chinese="反击退";
    }
    @SubscribeEvent
    public void onPacket(PacketEvent e) {
        if(Client.nullCheck())
            return;

        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            if (this.Targeting.getValue() && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) {
                return;
            }

            if (this.NoSword.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
                return;
            }
            
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
            
            if(packet.getEntityID() != mc.thePlayer.getEntityId())
            	return;
            
            double motionX = packet.getMotionX(),
            	   motionY = packet.getMotionY(),
            	   motionZ = packet.getMotionZ();
            
            if (this.chance.getValue() != 100.0D) {
                double ch = Math.random();
                if (ch >= this.chance.getValue() / 100.0D) {
                    return;
                }
            }
            

            if (this.horizontal.getValue() != 100.0D) {
                motionX *= this.horizontal.getValue() / 100.0D;
                motionZ *= this.horizontal.getValue() / 100.0D;
            }

            if (this.vertical.getValue() != 100.0D) {
                motionY *= this.vertical.getValue() / 100.0D;
            }
            ReflectionUtil.setFieldValue(packet, (int)motionX, "motionX", "field_149415_b");
            ReflectionUtil.setFieldValue(packet, (int)motionY, "motionY", "field_149416_c");
            ReflectionUtil.setFieldValue(packet, (int)motionZ, "motionZ", "field_149414_d");
        }
        
        if (e.getPacket() instanceof S27PacketExplosion) {
            if (this.Targeting.getValue() && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) {
                return;
            }

            if (this.NoSword.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
                return;
            }
            
            S27PacketExplosion packet = (S27PacketExplosion) e.getPacket();
            
            float motionX = packet.func_149149_c(),
            	  motionY = packet.func_149144_d(),
            	  motionZ = packet.func_149147_e();
            
            if (this.chance.getValue() != 100.0D) {
                double ch = Math.random();
                if (ch >= this.chance.getValue() / 100.0D) {
                    return;
                }
            }
            

            if (this.horizontal.getValue() != 100.0D) {
                motionX *= this.horizontal.getValue() / 100.0D;
                motionZ *= this.horizontal.getValue() / 100.0D;
            }

            if (this.vertical.getValue() != 100.0D) {
                motionY *= this.vertical.getValue() / 100.0D;
            }
            ReflectionUtil.setFieldValue(packet, motionX, "field_149152_f");
            ReflectionUtil.setFieldValue(packet, motionY, "field_149153_g");
            ReflectionUtil.setFieldValue(packet, motionZ, "field_149159_h");
        }

    }

}
