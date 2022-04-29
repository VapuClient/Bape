package mc.bape.module.render;

import mc.bape.gui.LuneClickGui.Lune;
import mc.bape.gui.MatrixClickGui.ClickUi;
import mc.bape.gui.VapeClickGui.VapeClickGui;
import mc.bape.values.Mode;
import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import org.lwjgl.input.Keyboard;

public class ClickGUI extends Module {
	private Mode<Enum> mode = new Mode("Mode", "mode", (Enum[]) ClickGUI.GuiMode.values(), (Enum) GuiMode.Lune);
	public static Mode<Enum> SexyMode = new Mode("Mode", "mode", (Enum[]) ClickGUI.SexyMode1.values(), (Enum) SexyMode1.zerotwo);

	static enum GuiMode {
		Vape,
		Lune,
		Matrix
	}

	public static enum SexyMode1 {
		zerotwo,
		None,
		KeQing,
		Mona,
		Paimon,
		Misaka
	}

	public ClickGUI() {
		super("ClickGUI", Keyboard.KEY_RSHIFT, ModuleType.Render,"Open ClickGui");
		this.addValues(this.mode, this.SexyMode);
		Chinese="点击GUI";
		// TODO Auto-generated constructor stub
	}

	@Override
	public void enable() {
		this.setState(false);
		mc.thePlayer.closeScreen();
		if(this.mode.getValue() == GuiMode.Vape) {
			mc.displayGuiScreen(new VapeClickGui());
		} else if(this.mode.getValue() == GuiMode.Lune){
			mc.displayGuiScreen(new Lune());
		} else if(this.mode.getValue() == GuiMode.Matrix){
			mc.displayGuiScreen(new ClickUi());
		}
	}
}
