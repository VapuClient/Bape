package mc.bape.module.render;

import mc.bape.legit.hud.HUDConfigScreen;
import mc.bape.utils.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import mc.bape.manager.ModuleManager;
import mc.bape.module.ModuleType;
import mc.bape.utils.ColorUtils;
import mc.bape.utils.RenderUtils;
import mc.bape.values.Mode;
import mc.bape.vapu.Client;
import mc.bape.module.Module;
import mc.bape.manager.FontManager;
import mc.bape.values.Option;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;

public class HUD extends Module {
    private Mode<Enum> mode = new Mode("Mode", "mode", (Enum[]) HUD.MODE.values(), (Enum) MODE.Text);
    private Option<Boolean> Health = new Option<Boolean>("Health","Health", false);
    private Option<Boolean> WaterMark = new Option<Boolean>("WaterMark","WaterMark", true);
    private Mode<Enum> sexymode = new Mode("Mode", "mode", (Enum[]) HUD.SEXYMODE.values(), (Enum) SEXYMODE.None);
    public Mode<Enum> NotificationType = new Mode("NotificationRenderType", "NotificationRenderType", (Enum[]) HUD.NofiType.values(), (Enum) NofiType.Black1);
    private int width;

    //HudMods
    public static Option<Boolean> SprintRender = new Option<Boolean>("SprintRender","SprintRender", true);
    public static Option<Boolean> InventoryHUD = new Option<Boolean>("InventoryHUD","InventoryHUD", true);
    public static Option<Boolean> ArrayList = new Option<Boolean>("ArrayList","ArrayList", true);
    public static Option<Boolean> TargetHUD = new Option<Boolean>("TargetHUD","TargetHUD", true);

    public HUD() {
        super("HUD", Keyboard.KEY_H, ModuleType.Render,"Show " + Client.CLIENT_NAME + " HUD Screen");
        this.addValues(this.mode,this.sexymode,this.Health,this.WaterMark, this.NotificationType,
                SprintRender, InventoryHUD, ArrayList, TargetHUD);
        Chinese="HUD界面";
    }

    static enum MODE {
        Bape,
        Text,
        Rainbow,
        Lunar
    }

    public enum NofiType {
        Black1,
        Black2
    }

    static enum SEXYMODE {
        None,
        SexyHuTao,
        PaiMon,
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        ScaledResolution s = new ScaledResolution(mc);
        int width = new ScaledResolution(mc).getScaledWidth();
        int height = new ScaledResolution(mc).getScaledHeight();
        int y = 1;
        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiMainMenu)) return;

        if(this.mode.getValue() == MODE.Bape){
            String text = Client.CLIENT_NAME;
            FontManager.C18.drawStringWithShadow(" | " + (mc.isSingleplayer() ? "localhost:25565" : !mc.getCurrentServerData().serverIP.contains(":") ? mc.getCurrentServerData().serverIP + ":25565" : mc.getCurrentServerData().serverIP) + " | " + Minecraft.getDebugFPS() + "fps",65,25,new Color(255, 255, 255).hashCode());
            RenderUtils.drawImage(7,9, 62, 25, new ResourceLocation("JAT/Bape.png"), new Color(255, 255, 255));
            //RenderUtil.drawImage(new ResourceLocation("JAT/Bape.png"),7,9,FontManager.C18.getStringWidth(text),40,255);
        }else if(this.mode.getValue() == MODE.Text){
            FontManager.C18.drawStringWithShadow(" | " +Client.CLIENT_NAME + " | "+ (mc.isSingleplayer() ? "localhost:25565" : !mc.getCurrentServerData().serverIP.contains(":") ? mc.getCurrentServerData().serverIP + ":25565" : mc.getCurrentServerData().serverIP) + " | " + Minecraft.getDebugFPS() + "fps",7,9,new Color(255, 255, 255).hashCode());
        }else if(this.mode.getValue() == MODE.Rainbow){
            FontManager.C18.drawStringWithShadow(" | " +Client.CLIENT_NAME + " | "+ (mc.isSingleplayer() ? "localhost:25565" : !mc.getCurrentServerData().serverIP.contains(":") ? mc.getCurrentServerData().serverIP + ":25565" : mc.getCurrentServerData().serverIP) + " | " + Minecraft.getDebugFPS() + "fps",7,9,ColorUtils.rainbow(2));
        }else if(this.mode.getValue() == MODE.Lunar){
            RenderUtils.drawImage(7,9, 70, 70, new ResourceLocation("JAT/Lunar.png"), new Color(220, 220, 220));
            FontManager.C30.drawStringWithShadow("LUNAR CLIENT",77,25,new Color(255, 255, 255).hashCode());
        }
        ArrayList<Module> modules = new ArrayList<>();
        for (Module m : Client.INSTANCE.moduleManager.getModules()) {
            modules.add(m);
        }
        int i = 0;
        if(this.Health.getValue()){
            if (mc.thePlayer.getHealth() >= 0.0f && mc.thePlayer.getHealth() < 10.0f) {
                this.width = 3;
            }
            if (mc.thePlayer.getHealth() >= 10.0f && mc.thePlayer.getHealth() < 100.0f) {
                this.width = 5;
            }
            mc.fontRendererObj.drawStringWithShadow("♥" + MathHelper.ceiling_float_int(mc.thePlayer.getHealth()), (float) (new ScaledResolution(mc).getScaledWidth() / 2 - this.width), (float) (new ScaledResolution(mc).getScaledHeight() / 2 - 15), -1);
        }
        if(this.sexymode.getValue() == SEXYMODE.SexyHuTao){
            RenderUtils.drawImage(2,240, 200, 270, new ResourceLocation("JAT/H.png"), new Color(220, 220, 220));
        }else if(this.sexymode.getValue() == SEXYMODE.PaiMon){
            RenderUtils.drawImage(550,200, 200, 270, new ResourceLocation("JAT/PaiMon.png"), new Color(220, 220, 220));
        }else if(this.sexymode.getValue() == SEXYMODE.None){

        }

        if (ModuleManager.getModule("Sprint").getState() && this.mode.getValue() == MODE.Lunar){
            mc.fontRendererObj.drawStringWithShadow("Sprinting (Toggled)",(float) (new ScaledResolution(mc).getScaledWidth() / 2 - 40),25,new Color(255, 255, 255).hashCode());
        } else if(this.mode.getValue() == MODE.Lunar){
            mc.fontRendererObj.drawStringWithShadow("Sprinting (Vanilla)",(float) (new ScaledResolution(mc).getScaledWidth() / 2 - 40),25,new Color(255, 255, 255).hashCode());
        }

        /*
        ArrayList<Module> enabledModules = new ArrayList<>();
        for (Module m : ModuleManager.getModules()) {
            if (m.state) {
                enabledModules.add(m);
            }
        }
        enabledModules.sort(new Comparator<Module>() {
            @Override
            public int compare(Module o1, Module o2) {
               return FontManager.F20.getStringWidth(o2.getName()) -FontManager.F20.getStringWidth(o1.getName());
            }
        });
        int r = 0;
        for (Module m : enabledModules) {
            if (m != null && m.getState()) {
                int moduleWidth = FontManager.F20.getStringWidth(m.name);
                FontManager.F20.drawStringWithShadow(m.name, width - moduleWidth - 1, y, ColorUtils.rainbow(1) + r);
                y += mc.fontRendererObj.FONT_HEIGHT;
                r = r + 10;
            }
        }
         */
        ClientUtil.drawNotifications();
        if (!(mc.currentScreen instanceof HUDConfigScreen)) {
            Client.INSTANCE.hudManager.renderMods();
        }
    }
}
