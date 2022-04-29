package mc.bape.gui.MatrixClickGui;

import com.google.common.collect.Lists;

import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.gui.Gui;
import mc.bape.gui.font.CFontRenderer;
import mc.bape.manager.FontManager;
import mc.bape.module.Module;
import mc.bape.utils.ClientUtil;
import mc.bape.values.Value;

public class Button {
    public Module cheat;
    CFontRenderer font = FontManager.F18;
    public Window parent;
    public int x;
    public int y;
    public int index;
    public int remander;
    public double opacity = 0.0;
    public ArrayList<ValueButton> buttons = Lists.newArrayList();
    public boolean expand;

    public Button(Module cheat, int x, int y) {
        this.cheat = cheat;
        this.x = x;
        this.y = y;
        int y2 = y + 14;
        for (Value v : cheat.getValues()) {
            this.buttons.add(new ValueButton(v, x + 5, y2));
            y2 += 15;
        }
        this.buttons.add(new KeyBindButton(cheat, x + 5, y2));
    }

    public void render(int mouseX, int mouseY) {
        CFontRenderer font = FontManager.F20;
        if (this.index != 0) {
            Button b2 = this.parent.buttons.get(this.index - 1);
            this.y = b2.y + 15 + (b2.expand ? 15 * b2.buttons.size() : 0);
        }
        int i = 0;
        while (i < this.buttons.size()) {
            this.buttons.get((int) i).y = this.y + 14 + 15 * i;
            this.buttons.get((int) i).x = this.x + 5;
            ++i;
        }
        Gui.drawRect(this.x - 5, this.y - 5, this.x + 85, this.y + font.getStringHeight(this.cheat.getName()) + 2,
                ClientUtil.reAlpha(1, 0.5f));
        if (this.cheat.state) {
//			RenderUtil.R2DUtils.drawRect(this.x - 5, this.y - 5, this.x + 85,
//					(float) (this.y + font.getStringHeight(this.cheat.getName()) + 3.8),
//					new Color(234, 234, 234).getRGB());
            Gui.drawRect(this.x - 5, this.y - 5, this.x + 85, (int) (this.y + font.getStringHeight(this.cheat.getName()) + 2), ClientUtil.reAlpha(1, 0.5f));
            font.drawString(this.cheat.getName(), this.x, this.y, new Color(47, 154, 241).getRGB());
        } else {
            font.drawString(this.cheat.getName(), this.x, this.y, new Color(184, 184, 184).getRGB());
        }
        if (!this.expand && this.buttons.size() > 1) {
            //FontLoaders.NovICON20.drawString("G", this.x + 76, this.y + 1, new Color(108, 108, 108).getRGB());
        }
        if (this.expand) {
            this.buttons.forEach(b -> b.render(mouseX, mouseY));
        }
    }

    public void key(char typedChar, int keyCode) {
        this.buttons.forEach(b -> b.key(typedChar, keyCode));
    }

    public void click(int mouseX, int mouseY, int button) {
        if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6
                && mouseY < this.y + font.getStringHeight(this.cheat.getName()) + 4) {
            if (button == 0) {
                this.cheat.setState(!this.cheat.getState());
            }
            if (button == 1 && !this.buttons.isEmpty()) {
                boolean bl = this.expand = !this.expand;
            }
        }
        if (this.expand) {
            this.buttons.forEach(b -> b.click(mouseX, mouseY, button));
        }
    }

    public void setParent(Window parent) {
        this.parent = parent;
        int i = 0;
        while (i < this.parent.buttons.size()) {
            if (this.parent.buttons.get(i) == this) {
                this.index = i;
                this.remander = this.parent.buttons.size() - i;
                break;
            }
            ++i;
        }
    }
}
