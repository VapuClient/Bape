package mc.bape.module.player;

import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mc.bape.manager.ModuleManager;
import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import mc.bape.module.combat.AntiBot;
import mc.bape.module.combat.Reach;
import mc.bape.utils.Nameplate;
import mc.bape.utils.RenderHelper;
import mc.bape.utils.TimerUtil;
import mc.bape.vapu.Client;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Queue;

public class ReachAssist extends Module {
    public TimerUtil timer = new TimerUtil();
    public Queue<Nameplate> tags;
    public ReachAssist() {
        super("ReachAssist", Keyboard.KEY_NONE, ModuleType.Player,"Display Reach");
    }


    @SubscribeEvent
    public void onPreRender(RenderPlayerEvent.Pre event) {
        if(Client.nullCheck())
            return;
        double v = 0.3;
        Scoreboard sb = event.entityPlayer.getWorldScoreboard();
        ScoreObjective sbObj = sb.getObjectiveInDisplaySlot(2);
        if (sbObj != null && !event.entityPlayer.getDisplayNameString().equals(mc.thePlayer.getDisplayNameString()) && event.entityPlayer.getDistanceSqToEntity((Entity)mc.thePlayer) < 100.0) {
            v *= 2.0;
        }
        if(event.entityPlayer.isDead)
            return;
        if(AntiBot.isServerBot(event.entityPlayer))
            return;
        if(event.entityPlayer == mc.thePlayer)
            return;
        if(event.entityPlayer.isInvisible())
            return;
        String str = (int) mc.thePlayer.getDistanceSqToEntity(event.entityPlayer) + "L";
        if(ModuleManager.getModule("Reach").getState()) {
            if (mc.thePlayer.getDistanceSqToEntity(event.entityPlayer) / 2.5 < Reach.MinReach.getValue()) {
                str = "§6Attack Ready";
                if (mc.objectMouseOver.entityHit != null && mc.objectMouseOver.entityHit == event.entityPlayer) {
                    RenderHelper.drawESP(event.entityPlayer, new Color(34, 255,0).getRGB(),true, 2);
                    str = "§aOK";
                } else {
                    RenderHelper.drawESP(event.entityPlayer, new Color(255, 183, 0).getRGB(),true, 2);
                }
            }
        } else {
            if (mc.thePlayer.getDistanceSqToEntity(event.entityPlayer) / 2.5 < 3.0) {
                str = "§6Attack Ready";
                if (mc.objectMouseOver.entityHit != null && mc.objectMouseOver.entityHit == event.entityPlayer) {
                    RenderHelper.drawESP(event.entityPlayer, new Color(34, 255,0).getRGB(),true, 2);
                    str = "§aOK";
                } else {
                    RenderHelper.drawESP(event.entityPlayer, new Color(255, 183, 0).getRGB(),true, 2);
                }
            }
        }
        Nameplate np = new Nameplate(str, event.x - 1, event.y - 1, event.z + 0.1, event.entityLiving);
        np.renderNewPlate(new Color(150,150,150));
        }
    }
