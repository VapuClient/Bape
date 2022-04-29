package mc.bape.module.render;

import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import mc.bape.utils.RenderHelper;
import mc.bape.utils.ColorUtil;
import mc.bape.values.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class StorageESP extends Module {
    private Option<Boolean> Chest = new Option<Boolean>("Chest","Chest", true);
    private Option<Boolean> EnderChest = new Option<Boolean>("EnderChest","EnderChest", false);
    private Option<Boolean> iron = new Option<Boolean>("Iron","Iron", false);
    private Option<Boolean> gold = new Option<Boolean>("Gold","Gold", false);
    private Option<Boolean> diamond = new Option<Boolean>("Diamond","Diamond", false);
    private Option<Boolean> emerald = new Option<Boolean>("Emerald","Emerald", false);
    private Option<Boolean> coal = new Option<Boolean>("Coal","Coal", false);
    public StorageESP() {
        super("StorageESP", Keyboard.KEY_NONE, ModuleType.Render,"Chest and Ore Renderer ESP");
        this.addValues(this.Chest,this.EnderChest,this.iron,this.gold,this.diamond,this.emerald,this.coal);
        Chinese="ExampleModule";
    }

    @SubscribeEvent
    public void onRender(final RenderWorldLastEvent ev) {
        for (final TileEntity te : mc.theWorld.loadedTileEntityList) {
            if (te instanceof TileEntityChest && this.Chest.getValue()) {
                int rgb = 0;
                rgb = ColorUtil.getRainbow().getRGB();
                RenderHelper.drawBlockESP(te.getPos(), rgb);
            }
            if(te instanceof TileEntityEnderChest && this.EnderChest.getValue()){
                int rgb = 0;
                rgb = ColorUtil.getRainbow().getRGB();
                RenderHelper.drawBlockESP(te.getPos(), rgb);
            }
        }
    }

    private static Minecraft mc;



    static {
        StorageESP.mc = Minecraft.getMinecraft();
    }
}
