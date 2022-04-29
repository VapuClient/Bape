package mc.bape.module.world;

import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import mc.bape.module.combat.AntiBot;
import mc.bape.utils.Helper;
import mc.bape.values.Option;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MurderMystery extends Module {

    public static boolean a1;
    public static int a2;
    private static List<EntityPlayer> m;
    private static List<EntityPlayer> bw;

    private Option<Boolean> Murder = new Option<Boolean>("Tell Everyone Murder","Tell Everyone Murder", false);
    private Option<Boolean> Bow= new Option<Boolean>("Tell Everyone Bow","Tell Everyone Bow", false);

    public MurderMystery() {
        super("MurderMystery", Keyboard.KEY_NONE, ModuleType.World,"Detection Murders in Murder game");
        this.addValues(this.Murder,this.Bow);
        Chinese="杀手检查";
    }


    @Override
    public void disable() {
        MurderMystery.a1 = false;
        MurderMystery.a2 = 0;
    }

    @SubscribeEvent
    public void o(final RenderWorldLastEvent ev) {
        if (MurderMystery.mc.thePlayer.getWorldScoreboard() != null && MurderMystery.mc.thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(1) != null) {
            final String d = MurderMystery.mc.thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(1).getDisplayName();
            if (d.contains("MURDER") || d.contains("MYSTERY")) {
                for (final EntityPlayer en : MurderMystery.mc.theWorld.playerEntities) {
                    if (en != MurderMystery.mc.thePlayer && !en.isInvisible() && !AntiBot.isServerBot(en)) {
                        if (en.getHeldItem() != null && en.getHeldItem().hasDisplayName()) {
                            final Item i = en.getHeldItem().getItem();
                            if (i instanceof ItemSword || i instanceof ItemAxe || en.getHeldItem().getDisplayName().replaceAll("§", "").equals("aKnife")) {
                                if (!MurderMystery.m.contains(en)) {
                                    MurderMystery.m.add(en);
                                        MurderMystery.mc.thePlayer.playSound("note.pling", 1.0f, 1.0f);
                                    Helper.sendMessage(en.getName() + " is the murderer!");
                                    if(this.Murder.getValue()){
                                        mc.thePlayer.sendChatMessage(en.getName() + " is the murderer!");
                                    }
                                }
                            }
                            else if (i instanceof ItemBow && !MurderMystery.bw.contains(en)) {
                                MurderMystery.bw.add(en);
                                Helper.sendMessage("[WARNING]"+en.getName()+" have bow! he maybe will kill you.");
                                if(this.Bow.getValue()){
                                    mc.thePlayer.sendChatMessage(en.getName() + " have bow.");
                                }
                            }
                        }
                        int rgb = Color.green.getRGB();
                        if ((MurderMystery.m.contains(en) && !MurderMystery.bw.contains(en)) || (MurderMystery.m.contains(en) && MurderMystery.bw.contains(en))) {
                            rgb = Color.red.getRGB();
                        }
                        if (!MurderMystery.m.contains(en) && MurderMystery.bw.contains(en)) {
                            rgb = Color.orange.getRGB();
                        }
                    }
                }
            }
            else {
                this.c();
            }
        }
        else {
            this.c();
        }
    }

    private void c() {
        if (MurderMystery.m.size() > 0) {
            MurderMystery.m.clear();
        }
        if (MurderMystery.bw.size() > 0) {
            MurderMystery.bw.clear();
        }
    }

    static {
        MurderMystery.a1 = false;
        MurderMystery.a2 = 0;
        MurderMystery.m = new ArrayList<EntityPlayer>();
        MurderMystery.bw = new ArrayList<EntityPlayer>();
    }



}
