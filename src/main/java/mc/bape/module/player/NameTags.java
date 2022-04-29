package mc.bape.module.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mc.bape.manager.ModuleManager;
import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import mc.bape.module.combat.AntiBot;
import mc.bape.utils.Nameplate;
import mc.bape.utils.TimerUtil;
import mc.bape.vapu.Client;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Queue;

public class NameTags extends Module {
    public TimerUtil timer = new TimerUtil();
    public Queue<Nameplate> tags;
    public NameTags() {
        super("NameTags", Keyboard.KEY_NONE, ModuleType.Render,"Render facing NameTags for targets");
        Chinese="NameTags";
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
        if (!event.entityPlayer.getDisplayName().equals(mc.thePlayer.getDisplayName())) {
            ChatFormatting Format = ChatFormatting.WHITE;
            if(event.entityPlayer.getHealth() > 15){
                Format = ChatFormatting.WHITE;
            } else if(event.entityPlayer.getHealth() > 8 && event.entityPlayer.getHealth() < 15){
                Format = ChatFormatting.YELLOW;
            } else {
                Format = ChatFormatting.RED;
            }
            Nameplate np = new Nameplate(event.entityPlayer.getDisplayNameString(), event.x, event.y, event.z, event.entityLiving);
            np.renderNewPlate(new Color(150,150,150));
        }
    }
}
