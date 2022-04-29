package mc.bape.vapu;

import mc.bape.manager.HudManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import mc.bape.command.*;
import mc.bape.manager.*;
import mc.bape.module.Module;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class Client {
    public static boolean QQGetter = true;
    public static boolean ENABLE_DEBUG = false;

    public static String CLIENT_NAME = "Bape";
    public static String CLIENT_SHORT_NAME = "Bape";
    public static String Team = "Bape Developer Team";
    public static String CLIENT_VERSION = "4.00";
    public static String CLIENT_CONFIG = "default";
    public static final File CLIENT_PLUGINS = new File(System.getenv("APPDATA"), Client.CLIENT_NAME + "Plugins/");
    public static boolean CLIENT_STATE = false;

    public static Color THEME_COLOR = new Color(0, 156, 161, 255);
    public static int THEME_RGB_COLOR = THEME_COLOR.getRGB();
    public static int THEME_RED_COLOR = THEME_COLOR.getRed();
    public static int THEME_GREEN_COLOR = THEME_COLOR.getGreen();
    public static int THEME_BLUE_COLOR = THEME_COLOR.getBlue();

    public static boolean ENABLE_STATE_CHAT_MESSAGE = true;
    public static boolean STRING_GOD_DETECTION = false;
    public static boolean CHINESE = false;
    public static boolean MessageON = true;
    public static Client INSTANCE;

    public final FileManager fileManager = new FileManager();
    public static ModuleManager moduleManager = new ModuleManager();
    public HudManager hudManager = new HudManager();


    public Client() throws IOException {
        if (CLIENT_STATE) return;
        CLIENT_STATE = true;
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        INSTANCE = this;
        CommandInit();

        ConfigManager.init();
        FriendManager.init();
        PacketManager.INSTANCE.init();
        if(Minecraft.getMinecraft().getCurrentServerData() != null) {
            PacketManager.INSTANCE.onJoinServer(null);
        }
    }

    public static boolean nullCheck() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer == null || mc.theWorld == null;
    }

    private void CommandInit() {
        ClientCommandHandler.instance.registerCommand(new CommandHelp(Client.INSTANCE));
        ClientCommandHandler.instance.registerCommand(new CommandBind(Client.INSTANCE));
        ClientCommandHandler.instance.registerCommand(new CommandWatermark(Client.INSTANCE));
        ClientCommandHandler.instance.registerCommand(new CommandF(Client.INSTANCE));
        ClientCommandHandler.instance.registerCommand(new CommandToggle(Client.INSTANCE));
        ClientCommandHandler.instance.registerCommand(new CommandT(Client.INSTANCE));
        ClientCommandHandler.instance.registerCommand(new CommandEnable(Client.INSTANCE));
        ClientCommandHandler.instance.registerCommand(new CommandDisable(Client.INSTANCE));
        ClientCommandHandler.instance.registerCommand(new CommandConfig(Client.INSTANCE));
        ClientCommandHandler.instance.registerCommand(new CommandDelete(Client.INSTANCE));
        ClientCommandHandler.instance.registerCommand(new CommandSave(Client.INSTANCE));
        ClientCommandHandler.instance.registerCommand(new CommandN(Client.INSTANCE));
        ClientCommandHandler.instance.registerCommand(new CommandTitle(Client.INSTANCE));
        ClientCommandHandler.instance.registerCommand(new CommandEsu(Client.INSTANCE));
        ClientCommandHandler.instance.registerCommand(new CommandUnBind(Client.INSTANCE));
    }

    @SubscribeEvent
    public void keyInput(InputEvent.KeyInputEvent event) {
        for(Module m : moduleManager.getModules()) {
            if(Keyboard.isKeyDown(m.key) && m.getKey() != Keyboard.KEY_NONE) {
                m.toggle();
            }
//            if(Keyboard.isKeyDown(m.key) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
//                mc.displayGuiScreen(new BindScreen(m));
//            }
        }
    }

    public static void SaveConfig() throws IOException {
        Client.INSTANCE.fileManager.saveModules();
    }

    public static void LoadConfig() throws IOException {
        Client.INSTANCE.fileManager.loadModules();
    }

    public static Timer getTimer() {
        Minecraft mc = Minecraft.getMinecraft();
        try {
            final Class<Minecraft> c = Minecraft.class;
            final Field f = c.getDeclaredField(new String(new char[] { 't', 'i', 'm', 'e', 'r' }));
            f.setAccessible(true);
            return (Timer)f.get(mc);
        }
        catch (Exception er) {
            try {
                final Class<Minecraft> c2 = Minecraft.class;
                final Field f2 = c2.getDeclaredField(new String(new char[] { 'f', 'i', 'e', 'l', 'd', '_', '7', '1', '4', '2', '8', '_', 'T' }));
                f2.setAccessible(true);
                return (Timer)f2.get(mc);
            }
            catch (Exception er2) {
                return null;
            }
        }
    }


}
