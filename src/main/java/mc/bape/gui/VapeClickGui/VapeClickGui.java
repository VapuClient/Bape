package mc.bape.gui.VapeClickGui;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.*;

import mc.bape.gui.VapeClickGui.utils.EmptyInputBox;
import mc.bape.manager.ConfigManager;
import mc.bape.manager.FontManager;
import mc.bape.manager.ModuleManager;
import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import mc.bape.utils.RenderUtils;
import mc.bape.utils.TimerUtil;
import mc.bape.utils.Helper;
import mc.bape.utils.RenderUtil;
import mc.bape.values.Mode;
import mc.bape.values.Numbers;
import mc.bape.values.Option;
import mc.bape.values.Value;
import mc.bape.vapu.Client;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import static mc.bape.manager.ConfigManager.Configs;
import static mc.bape.utils.RenderUtil.drawRoundRect;
import static mc.bape.utils.RenderUtils.drawImage;

public class VapeClickGui extends GuiScreen {

    private boolean close = false;
    private boolean closed;

    class GetConfigs extends Thread {
        @Override
        public void run() {

        }
    }

    class LoadConfig extends Thread {
        private String configName;
        public LoadConfig(String configName) {
            List<String> binds = ConfigManager.read(configName +"-binds.cfg");
            for (String v : binds) {
                String name = v.split(":")[0];
                String bind = v.split(":")[1];
                Module m = ModuleManager.getModule(name);
                if (m == null) continue;
                m.setKey(Keyboard.getKeyIndex((String)bind.toUpperCase()));
            }
            List<String> enabled = ConfigManager.read(configName +"-modules.cfg");
            for (String v : enabled) {
                Module m = ModuleManager.getModule(v);
                if (m == null) continue;
                m.setState(true);
            }
            List<String> vals = ConfigManager.read(configName +"-values.cfg");
            for (String v : vals) {
                String name = v.split(":")[0];
                String values = v.split(":")[1];
                Module m = ModuleManager.getModule(name);
                if (m == null) continue;
                for (Value value : m.getValues()) {
                    if (!value.getName().equalsIgnoreCase(values)) continue;
                    if (value instanceof Option) {
                        value.setValue(Boolean.parseBoolean(v.split(":")[2]));
                        continue;
                    }
                    if (value instanceof Numbers) {
                        value.setValue(Double.parseDouble(v.split(":")[2]));
                        continue;
                    }
                    ((Mode)value).setMode(v.split(":")[2]);
                }
            }
            List<String> cfg = ConfigManager.read(configName +"-client.cfg");
            if (cfg.size() != 0) {
                Client.CLIENT_NAME = cfg.get(0);
                Helper.sendMessage("Configs Loaded.");
            } else {
                Helper.sendMessage("Failed to load config");
            }
        }

        @Override
        public void run() {
            new LoadConfig(configName);
        }
    }


    private float dragX, dragY;
    private boolean drag = false;
    private int valuemodx = 0;
    private static float modsRole, modsRoleNow;
    private static float valueRoleNow, valueRole;
    public static EmptyInputBox configInputBox;
    public float lastPercent;
    public float percent;
    public float percent2;
    public float lastPercent2;
    public float outro;
    public float lastOutro;
    private int scroll;

    @Override
    public void initGui() {
        super.initGui();
        percent = 1.33f;
        lastPercent = 1f;
        percent2 = 1.33f;
        lastPercent2 = 1f;
        outro = 1;
        lastOutro = 1;
        valuetimer.reset();
        Configs.clear();
        File file = new File(System.getenv("APPDATA"), Client.CLIENT_NAME);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory() && !Configs.contains(files[i].getName()) && files[i].getName() != "Plugins") {
                    Configs.add(files[i].getName());
                }
            }
        }
        new GetConfigs().start();
        configInputBox = new EmptyInputBox(2, mc.fontRendererObj, 0, 0, 85, 10);
    }


    /*
    主窗口宽度 = 500
    主窗口高度 = 310
    功能列表起始位置 = 100
    功能宽度 = 325(未开values)
    功能起始高度 = 60
     */


    static float windowX = 200, windowY = 200;
    static float width = 500, height = 310;

    static ClickType selectType = ClickType.Module;
    static ModuleType modCategory = ModuleType.Combat;
    static Module selectMod;

    float[] typeXAnim = new float[]{windowX + 10, windowX + 10, windowX + 10, windowX + 10};

    float hy = windowY + 40;

    TimerUtil valuetimer = new TimerUtil();

    public float smoothTrans(double current, double last){
        return (float) (current + (last - current) / (Minecraft.getDebugFPS() / 10));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution sResolution = new ScaledResolution(mc);
        ScaledResolution sr = new ScaledResolution(mc);

        FontManager.F18.drawStringWithShadow(Client.CLIENT_NAME + " " + Client.CLIENT_VERSION, 5, mc.displayHeight - 17, new Color(255, 255, 255).hashCode());
        FontManager.F18.drawStringWithShadow("Bape is a free client and it is Opensource, if you pay any money for it means you got deceived.", 5, mc.displayHeight - 5 , new Color(255, 255, 255).hashCode());
        //RenderUtils.drawImage(240,240, 50, 70, new ResourceLocation("JAT/BapelogoR.png"), new Color(255, 255, 255));

        float outro = smoothTrans(this.outro, lastOutro);
        if (mc.currentScreen == null) {
            GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
            GlStateManager.scale(outro, outro, 0);
            GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
        }


        //animation
        percent = smoothTrans(this.percent, lastPercent);
        percent2 = smoothTrans(this.percent2, lastPercent2);


        if (percent > 0.98) {
            GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
            GlStateManager.scale(percent, percent, 0);
            GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
        } else {
            if (percent2 <= 1) {
                GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
                GlStateManager.scale(percent2, percent2, 0);
                GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
            }
        }


        if(percent <= 1.5 && close) {
            percent = smoothTrans(this.percent, 2);
            percent2 = smoothTrans(this.percent2, 2);
        }

        if(percent >= 1.4  &&  close){
            percent = 1.5f;
            closed = true;
            mc.currentScreen = null;
        }

        
        drawGradientRect(0, 0, sResolution.getScaledWidth(), sResolution.getScaledHeight(), new Color(107, 147, 255, 100).getRGB(), new Color(0, 0, 0, 30).getRGB());
//        if (inAnim > 0) {
//            inAnim -= 5000f / mc.debugFPS;
//        } else {
//            inAnim += 0.1f;
//        }
//        if (inAnim > 5) {
//            GlStateManager.translate(inAnim, 0, 0);
//        }

        //拖动
        if (isHovered(windowX, windowY, windowX + width, windowY + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            if (dragX == 0 && dragY == 0) {
                dragX = mouseX - windowX;
                dragY = mouseY - windowY;
            } else {
                windowX = mouseX - dragX;
                windowY = mouseY - dragY;
            }
            drag = true;
        } else if (dragX != 0 || dragY != 0) {
            dragX = 0;
            dragY = 0;
        }


        //绘制主窗口
        drawRect((int) windowX, (int) windowY, (int) (windowX + width), (int) (windowY + height), new Color(21, 22, 25).getRGB());
        if (selectMod == null) {
            FontManager.F20.drawString(Client.CLIENT_NAME.toUpperCase(Locale.ROOT), windowX + 20, windowY + height - 20, new Color(77, 78, 84).getRGB());
        }
        //绘制顶部图标
        float typeX = windowX + 20;
        int i = 0;
        for (Enum<?> e : ClickType.values()) {
            if (!isHovered(windowX, windowY, windowX + width, windowY + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                if (typeXAnim[i] != typeX) {
                    typeXAnim[i] += (typeX - typeXAnim[i]) / 20;
                }
            } else {
                if (typeXAnim[i] != typeX) {
                    typeXAnim[i] = typeX;
                }
            }
            if (e != ClickType.Settings) {
                if (e == selectType) {
                    drawImage(typeXAnim[i], windowY + 10, 16, 16, new ResourceLocation("vapeclickgui/" + e.name().toLowerCase(Locale.ROOT) + ".png"), new Color(255, 255, 255));
                    FontManager.F18.drawString(e.name(), typeXAnim[i] + 20, windowY + 15, new Color(255, 255, 255).getRGB());
                    typeX += (32 + FontManager.F18.getStringWidth(e.name() + " "));
                } else {
                    drawImage(typeXAnim[i], windowY + 10, 16, 16, new ResourceLocation("vapeclickgui/" + e.name().toLowerCase(Locale.ROOT) + ".png"), new Color(79, 80, 86));
                    typeX += (32);
                }
            } else {
                drawImage(windowX + width - 20, windowY + 10, 16, 16, new ResourceLocation("vapeclickgui/" + e.name().toLowerCase(Locale.ROOT) + ".png"), e == selectType ? new Color(255, 255, 255) : new Color(79, 80, 86));
            }
            i++;
        }


        if (selectType == ClickType.Module) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(0, 2 * ((int) (sr.getScaledHeight_double() - (windowY + height))) + 40, (int) (sr.getScaledWidth_double() * 2), (int) ((height) * 2) - 160);
            if (selectMod == null) {
                //绘制类型列表
                float cateY = windowY + 65;
                for (ModuleType m : ModuleType.values()) {
                    if (m == modCategory) {
                        FontManager.F18.drawString(m.name(), windowX + 20, cateY, -1);
                        drawRoundRect(windowX + 20, hy + FontManager.F18.getStringHeight("") + 2, windowX + 30, hy + FontManager.F18.getStringHeight("") + 4, Client.THEME_RGB_COLOR);
                        if (isHovered(windowX, windowY, windowX + width, windowY + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                            hy = cateY;
                        } else {
                            if (hy != cateY) {
                                hy += (cateY - hy) / 20;
                            }
                        }
                    } else {
                        FontManager.F18.drawString(m.name(), windowX + 20, cateY, new Color(108, 109, 113).getRGB());
                    }


                    cateY += 25;
                }
            }
            if (selectMod != null) {
                if (valuemodx > -80) {
                    valuemodx -= 5;
                }
            } else {
                if (valuemodx < 0) {
                    valuemodx += 5;
                }
            }

            if (selectMod != null) {
                drawRoundRect(windowX + 430 + valuemodx, windowY + 60, windowX + width, windowY + height - 20, new Color(32, 31, 35).getRGB());
                drawRoundRect(windowX + 430 + valuemodx, windowY + 60, windowX + width, windowY + 85, new Color(39, 38, 42).getRGB());
                drawImage(windowX + 435 + valuemodx, windowY + 65, 16, 16, new ResourceLocation("vapeclickgui/back.png"), new Color(82, 82, 85));
                if (isHovered(windowX + 435 + valuemodx, windowY + 65, windowX + 435 + valuemodx + 16, windowY + 65 + 16, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                    selectMod = null;
                    valuetimer.reset();
                }


                //滚动
                GlStateManager.translate(0,scroll,0);
                mouseY -= scroll;


                int dWheel = Mouse.getDWheel();
                    if (dWheel < 0 && Math.abs(valueRole) + 170 < (selectMod.values.size() * 25)) {
                        valueRole -= 32;
                    }
                    if (dWheel > 0 && valueRole < 0) {
                        valueRole += 32;
                    }

                if (valueRoleNow != valueRole) {
                    valueRoleNow += (valueRole - valueRoleNow) / 20;
                    valueRoleNow = (int) valueRoleNow;
                }

                float valuey = windowY + 100 + valueRoleNow;

                if(selectMod == null) {
                    return;
                }

                for (Value v : selectMod.values) {
                    if (v instanceof Option) {
                        if (valuey + 4 > windowY + 100) {
                            if (((Boolean) v.getValue())) {
                                FontManager.F16.drawString(v.getName(), windowX + 445 + valuemodx, valuey + 4, -1);
                                v.optionAnim = 100;
                                RenderUtil.drawRoundedRect(windowX + width - 30, valuey + 2, windowX + width - 10, valuey + 12, 4, new Color(Client.THEME_RED_COLOR,Client.THEME_GREEN_COLOR,Client.THEME_BLUE_COLOR, (int) (v.optionAnimNow / 100 * 255)).getRGB());
                                RenderUtils.drawCircle(windowX + width - 25 + 10 * (v.optionAnimNow / 100f), valuey + 7, 3.5, new Color(255, 255, 255).getRGB());
                            } else {
                                FontManager.F16.drawString(v.getName(), windowX + 445 + valuemodx, valuey + 4, new Color(73, 72, 76).getRGB());
                                v.optionAnim = 0;
                                RenderUtil.drawRoundedRect(windowX + width - 30, valuey + 2, windowX + width - 10, valuey + 12, 4, new Color(59, 60, 65).getRGB());
                                RenderUtil.drawRoundedRect(windowX + width - 29, valuey + 3, windowX + width - 11, valuey + 11, 3, new Color(32, 31, 35).getRGB());
                                RenderUtils.drawCircle(windowX + width - 25 + 10 * (v.optionAnimNow / 100f), valuey + 7, 3.5, new Color(59, 60, 65).getRGB());
                            }
                            if (isHovered(windowX + width - 30, valuey + 2, windowX + width - 10, valuey + 12, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                                if (valuetimer.delay(300)) {
                                    v.setValue(!(Boolean) v.getValue());
                                    valuetimer.reset();
                                }
                            }
                        }

                        if (v.optionAnimNow != v.optionAnim) {
                            v.optionAnimNow += (v.optionAnim - v.optionAnimNow) / 20;
                        }
                        valuey += 25;
                    }
                    }
                for (Value v : selectMod.values) {
                    if (v instanceof Numbers) {
                        if (valuey + 4 > windowY + 100) {

                            float present = (float) (((windowX + width - 11) - (windowX + 450 + valuemodx))
                                    * (((Number) v.getValue()).floatValue() - ((Numbers) v).getMinimum().floatValue())
                                    / (((Numbers) v).getMaximum().floatValue() - ((Numbers) v).getMinimum().floatValue()));

                            FontManager.F16.drawString(v.getName(), windowX + 445 + valuemodx, valuey + 5, new Color(73, 72, 76).getRGB());
                            FontManager.F16.drawCenteredString(v.getValue().toString(), windowX + width - 20, valuey + 5, new Color(255, 255, 255).getRGB());
                            RenderUtil.drawRect(windowX + 450 + valuemodx, valuey + 20, windowX + width - 11, valuey + 21.5f, new Color(77, 76, 79).getRGB());
                            RenderUtil.drawRect(windowX + 450 + valuemodx, valuey + 20, windowX + 450 + valuemodx + present, valuey + 21.5f, Client.THEME_RGB_COLOR);
                            RenderUtils.drawCircle(windowX + 450 + valuemodx + present, valuey + 21f, 5, new Color(32, 31, 35).getRGB());
                            RenderUtils.drawCircle(windowX + 450 + valuemodx + present, valuey + 21f, 5, new Color(32, 31, 35).getRGB());
                            RenderUtils.drawCircle(windowX + 450 + valuemodx + present, valuey + 21f, 4, Client.THEME_RGB_COLOR);
                            RenderUtils.drawCircle(windowX + 450 + valuemodx + present, valuey + 21f, 4, Client.THEME_RGB_COLOR);

                            if (isHovered(windowX + 450 + valuemodx, valuey + 18, windowX + width - 11, valuey + 23.5f, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                                float render2 = ((Numbers) v).getMinimum().floatValue();
                                double max = ((Numbers) v).getMaximum().doubleValue();
                                double inc = 0.1;
                                double valAbs = (double) mouseX - ((double) (windowX + 450 + valuemodx));
                                double perc = valAbs / (((windowX + width - 11) - (windowX + 450 + valuemodx)));
                                perc = Math.min(Math.max(0.0D, perc), 1.0D);
                                double valRel = (max - render2) * perc;
                                double val = render2 + valRel;
                                val = (double) Math.round(val * (1.0D / inc)) / (1.0D / inc);
                                ((Numbers) v).setValue(Double.valueOf(val));
                            }
                        }
                        valuey += 25;
                    }
                }
                for (Value v : selectMod.values) {
                    if (v instanceof Mode) {
                        Mode<?> modeValue = (Mode<?>) v;

                        if (valuey + 4 > windowY + 100 & valuey < (windowY + height)) {
                            RenderUtil.drawRoundedRect(windowX + 445 + valuemodx, valuey + 2, windowX + width - 5, valuey + 22, 2, new Color(46, 45, 48).getRGB());
                            RenderUtil.drawRoundedRect(windowX + 446 + valuemodx, valuey + 3, windowX + width - 6, valuey + 21, 2, new Color(32, 31, 35).getRGB());
                            FontManager.F16.drawString(v.getName() + ":" + modeValue.getModeAsString(), windowX + 455 + valuemodx, valuey + 10, new Color(230, 230, 230).getRGB());
                            FontManager.F18.drawString(">", windowX + width - 15, valuey + 9, new Color(73, 72, 76).getRGB());
                            if (isHovered(windowX + 445 + valuemodx, valuey + 2, windowX + width - 5, valuey + 22, mouseX, mouseY) && Mouse.isButtonDown(0) && valuetimer.delay(300)) {
                                if (Arrays.binarySearch(modeValue.getModes(), (v.getValue()))
                                        + 1 < modeValue.getModes().length) {
                                    v.setValue(modeValue
                                            .getModes()[Arrays.binarySearch(modeValue.getModes(), (v.getValue())) + 1]);
                                } else {
                                    v.setValue(modeValue.getModes()[0]);
                                }
                                valuetimer.reset();
                            }
                        }
                        valuey += 25;
                    }
                }
//                for (NewMode v : selectMod.getNewModes()) {
//                    NewMode modeValue = (NewMode) v;
//
//                    if (valuey + 4 > windowY + 100 & valuey < (windowY + height)) {
//                        RenderUtil.drawRoundedRect(windowX + 445 + valuemodx, valuey + 2, windowX + width - 5, valuey + 22, 2, new Color(46, 45, 48).getRGB());
//                        RenderUtil.drawRoundedRect(windowX + 446 + valuemodx, valuey + 3, windowX + width - 6, valuey + 21, 2, new Color(32, 31, 35).getRGB());
//                        FontManager.F16.drawString(v.getName() + ":" + modeValue.getModeAsString(), windowX + 455 + valuemodx, valuey + 10, new Color(230, 230, 230).getRGB());
//                        FontManager.F18.drawString(">", windowX + width - 15, valuey + 9, new Color(73, 72, 76).getRGB());
//                        if (isHovered(windowX + 445 + valuemodx, valuey + 2, windowX + width - 5, valuey + 22, mouseX, mouseY) && Mouse.isButtonDown(0) && valuetimer.delay(300)) {
//                            if (Arrays.binarySearch(modeValue.getModes(), (v.getValue()))
//                                    + 1 < modeValue.getModes().length) {
//                                v.setValue(modeValue
//                                        .getModes()[Arrays.binarySearch(modeValue.getModes(), (v.getValue())) + 1]);
//                            } else {
//                                v.setValue(modeValue.getModes()[0]);
//                            }
//                            valuetimer.reset();
//                        }
//                    }
//                    valuey += 25;
//                }
            }

            float modY = windowY + 70 + modsRoleNow;
            for (Module m : ModuleManager.getModules()) {
                if (m.getCategory() != modCategory)
                    continue;

                if (isHovered(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                    if (valuetimer.delay(300) && modY + 40 > (windowY + 70) && modY < (windowY + height)) {
                        m.setState(!m.getState());
                        valuetimer.reset();
                    }
                } else if (isHovered(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, mouseX, mouseY) && Mouse.isButtonDown(1)) {
                    if (valuetimer.delay(300)) {
                        if (selectMod != m) {
                            valueRole = 0;
                            selectMod = m;
                        } else if (selectMod == m) {
                            selectMod = null;
                        }
                        valuetimer.reset();
                    }
                }

                if (isHovered(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, mouseX, mouseY)) {
                    if (m.getState()) {
                        drawRoundRect(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, new Color(43, 41, 45).getRGB());
                    } else {
                        drawRoundRect(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, new Color(35, 35, 35).getRGB());
                    }
                } else {
                    if (m.getState()) {
                        drawRoundRect(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, new Color(36, 34, 38).getRGB());
                    } else {
                        drawRoundRect(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, new Color(32, 31, 33).getRGB());
                    }
                }
                drawRoundRect(windowX + 100 + valuemodx, modY - 10, windowX + 125 + valuemodx, modY + 25, new Color(37, 35, 39).getRGB());
                drawRoundRect(windowX + 410 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, new Color(39, 38, 42).getRGB());
                FontManager.F20.drawString(".", windowX + 416 + valuemodx, modY - 5, new Color(66, 64, 70).getRGB());
                FontManager.F20.drawString(".", windowX + 416 + valuemodx, modY - 1, new Color(66, 64, 70).getRGB());
                FontManager.F20.drawString(".", windowX + 416 + valuemodx, modY + 3, new Color(66, 64, 70).getRGB());

                if (isHovered(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, mouseX, mouseY)) {
                    FontManager.F16.drawString(m.Descript, windowX + 225 + valuemodx, modY + 5, new Color(94, 95, 98).getRGB());
                } else {
                    FontManager.F16.drawString(m.Descript, windowX + 220 + valuemodx, modY + 5, new Color(94, 95, 98).getRGB());
                }

                if (m.getState()) {
                    FontManager.F18.drawString(m.getName(), windowX + 140 + valuemodx, modY + 5, new Color(220, 220, 220).getRGB());
                    drawRoundRect(windowX + 100 + valuemodx, modY - 10, windowX + 125 + valuemodx, modY + 25, new Color(Client.THEME_RED_COLOR,Client.THEME_GREEN_COLOR , Client.THEME_BLUE_COLOR, (int) (m.optionAnimNow / 100f * 255)).getRGB());
                    drawImage(windowX + 105 + valuemodx, modY, 16, 16, new ResourceLocation("vapeclickgui/module.png"), new Color(220, 220, 220));
                    m.optionAnim = 100;

//                    RenderUtil.drawRoundedRect(windowX + 380 + valuemodx, modY + 2, windowX + 400 + valuemodx, modY + 12, 4, new Color(33, 94, 181, (int) (m.optionAnimNow / 100f * 255)).getRGB());
//                    RenderUtils.drawCircle(windowX + 385 + 10 * m.optionAnimNow / 100 + valuemodx, modY + 7, 3.5, new Color(255, 255, 255).getRGB());
                } else {
                    FontManager.F18.drawString(m.getName(), windowX + 140 + valuemodx, modY + 5, new Color(108, 109, 113).getRGB());
                    drawImage(windowX + 105 + valuemodx, modY, 16, 16, new ResourceLocation("vapeclickgui/module.png"), new Color(92, 90, 94));
                    m.optionAnim = 0;

//                    RenderUtil.drawRoundedRect(windowX + 380 + valuemodx, modY + 2, windowX + 400 + valuemodx, modY + 12, 4, new Color(59, 60, 65).getRGB());
//                    RenderUtil.drawRoundedRect(windowX + 381 + valuemodx, modY + 3, windowX + 399 + valuemodx, modY + 11, 3, new Color(29, 27, 31).getRGB());
//                    RenderUtils.drawCircle(windowX + 385 + 10 * m.optionAnimNow / 100 + valuemodx, modY + 7, 3.5, new Color(59, 60, 65).getRGB());
                }

                if (m.optionAnimNow != m.optionAnim) {
                    m.optionAnimNow += (m.optionAnim - m.optionAnimNow) / 20;
                }


                modY += 40;
            }
            //滚动
            int dWheel2 = Mouse.getDWheel();
            if (isHovered(windowX + 100 + valuemodx, windowY + 60, windowX + 425 + valuemodx, windowY + height, mouseX, mouseY)) {
                if (dWheel2 < 0 && Math.abs(modsRole) + 220 < (ModuleManager.getModulesInType(modCategory).size() * 40)) {
                    modsRole -= 32;
                }
                if (dWheel2 > 0 && modsRole < 0) {
                    modsRole += 32;
                }
            }

            if (modsRoleNow != modsRole) {
                modsRoleNow += (modsRole - modsRoleNow) / 20;
                modsRoleNow = (int) modsRoleNow;
            }


            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
        if (selectType == ClickType.Profile) {
            FontManager.F18.drawString("Profiles", windowX + 20, windowY + 60, new Color(169, 170, 173).getRGB());
            drawRoundRect(windowX + 20, windowY + FontManager.F18.getStringHeight("") + 62, windowX + 30, windowY + FontManager.F18.getStringHeight("") + 64, Client.THEME_RGB_COLOR);
            configInputBox.xPosition = (int) (windowX + 20);
            configInputBox.yPosition = (int) (windowY + 100);
            configInputBox.drawTextBox();
            drawRoundRect(windowX + 20, windowY + 110, windowX + 105, windowY + 111, new Color(36, 36, 40).getRGB());
            if (configInputBox.getText().equals("")) {
                FontManager.F16.drawString("Profiles", windowX + 20, windowY + 97, new Color(63, 64, 67).getRGB());
            }
            RenderUtils.drawCircle(windowX + 100, windowY + 100, 5, Client.THEME_RGB_COLOR);
            RenderUtils.drawCircle(windowX + 100, windowY + 100, 5, Client.THEME_RGB_COLOR);
            FontManager.F20.drawString("+", windowX + 97f, windowY + 97.5f, -1);

            float cx = windowX + 140, cy = windowY + 60;
            for (String c : Configs) {
                drawRoundRect(cx, cy, cx + 100, cy + 100, new Color(32, 31, 35).getRGB());
                FontManager.F16.drawString(c, cx + 10, cy + 10, -1);
                drawImage(cx + 10, cy + 80, 10, 8, new ResourceLocation("vapeclickgui/profile.png"), new Color(91, 92, 98));
//                FontManager.F14.drawString(c.description, cx + 25, cy + 82, new Color(86, 85, 88).getRGB());
                if (c.equals(Client.CLIENT_CONFIG)) {
                    FontManager.F14.drawString("Using", cx + 70, cy + 82, Client.THEME_RGB_COLOR);
                }

                if (isHovered(cx, cy, cx + 100, cy + 100, mouseX, mouseY) && Mouse.isButtonDown(0) && valuetimer.delay(500)) {
                    ConfigManager.ConfigFile = "/" + c + "/";
                    System.err.print(ConfigManager.ConfigFile + "\n");
                    ConfigManager.ConfigFile = "/" + c + "/";
                    List<String> binds = ConfigManager.read(c +"-binds.cfg");
                    for (String v : binds) {
                        ConfigManager.ConfigFile = "/" + c + "/";
                        String name = v.split(":")[0];
                        String bind = v.split(":")[1];
                        Module m = ModuleManager.getModule(name);
                        if (m == null) continue;
                        m.setKey(Keyboard.getKeyIndex((String)bind.toUpperCase()));
                    }
                    List<String> enabled = ConfigManager.read(c +"-modules.cfg");
                    for (String v : enabled) {
                        ConfigManager.ConfigFile = "/" + c + "/";
                        Module m = ModuleManager.getModule(v);
                        if (m == null) continue;
                        m.setState(true);
                    }
                    List<String> vals = ConfigManager.read(c +"-values.cfg");
                    for (String v : vals) {
                        ConfigManager.ConfigFile = "/" + c + "/";
                        String name = v.split(":")[0];
                        String values = v.split(":")[1];
                        Module m = ModuleManager.getModule(name);
                        if (m == null) continue;
                        for (Value value : m.getValues()) {
                            if (!value.getName().equalsIgnoreCase(values)) continue;
                            if (value instanceof Option) {
                                value.setValue(Boolean.parseBoolean(v.split(":")[2]));
                                continue;
                            }
                            if (value instanceof Numbers) {
                                value.setValue(Double.parseDouble(v.split(":")[2]));
                                continue;
                            }
                            ((Mode)value).setMode(v.split(":")[2]);
                        }
                    }
                    Helper.sendMessage("Configs Loaded.");
                    Client.CLIENT_CONFIG = c;
                    valuetimer.reset();
                }

                cx += 105;
                if ((cx + 100) >= windowX + width) {
                    cx = windowX + 140;
                    cy += 110;
                }
            }


        }
        int dWheel2 = Mouse.getDWheel();
        if (isHovered(windowX + 100 + valuemodx, windowY + 60, windowX + 425 + valuemodx, windowY + height, mouseX, mouseY)) {
            if (dWheel2 < 0 && Math.abs(modsRole) + 220 < (ModuleManager.getModulesInType(modCategory).size() * 40)) {
                modsRole -= 16;
            }
            if (dWheel2 > 0 && modsRole < 0) {
                modsRole += 16;
            }
        }

        if (modsRoleNow != modsRole) {
            modsRoleNow += (modsRole - modsRoleNow) / 20;
            modsRoleNow = (int) modsRoleNow;
        }

        //处理输入框
        if (selectType == ClickType.Profile) {
            if (isHovered(windowX + 95, windowY + 95, windowX + 105, windowY + 105, mouseX, mouseY) && Mouse.isButtonDown(0) && valuetimer.delay(500) && Configs.size() < 6) {
                boolean has = false;
                for (String c : Configs) {
                    if (Configs.contains(configInputBox.getText())) {
                        ConfigManager.ConfigFile = "/" + configInputBox.getText() + "/";
                        String values = "";
                        for (Module m : ModuleManager.getModules()) {
                            for (Value v : m.getValues()) {
                                values = String.valueOf(values) + String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator());
                            }
                        }
                        ConfigManager.save(configInputBox.getText() +"-values.cfg", values, false);
                        String enabled = "";
                        ArrayList<Module> modules = new ArrayList<>();
                        for (Module m : ModuleManager.getModules()) {
                            modules.add(m);
                        }
                        for (Module m : modules) {
                            if (m != null && m.getState()) {
                                enabled = String.valueOf(enabled) + String.format("%s%s", m.getName(), System.lineSeparator());
                            }
                        }
                        String content = "";
                        Module m;
                        for(Iterator var4 = ModuleManager.getModules().iterator(); var4.hasNext(); content = content + String.format("%s:%s%s", new Object[]{m.getName(), Keyboard.getKeyName(m.getKey()), System.lineSeparator()})) {
                            m = (Module)var4.next();
                        }
                        ConfigManager.save(configInputBox.getText() + "-binds.cfg", content, false);
                        ConfigManager.save(configInputBox.getText() + "-modules.cfg", enabled, false);
                        ConfigManager.save(configInputBox.getText() + "-client.cfg", Client.CLIENT_NAME, false);
                        Helper.sendMessage("Configs Saved.");
                        valuetimer.reset();
                        has = true;
                    }
                }
                if (!has) {
                    ConfigManager.ConfigFile = "/" + configInputBox.getText() + "/";
                    Configs.add(configInputBox.getText());
                    Client.CLIENT_CONFIG = configInputBox.getText();
                    String values = "";
                    for (Module m : ModuleManager.getModules()) {
                        for (Value v : m.getValues()) {
                            values = String.valueOf(values) + String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator());
                        }
                    }
                    ConfigManager.save(Client.CLIENT_CONFIG +"-values.cfg", values, false);
                    String enabled = "";
                    ArrayList<Module> modules = new ArrayList<>();
                    for (Module m : ModuleManager.getModules()) {
                        modules.add(m);
                    }
                    for (Module m : modules) {
                        if (m != null && m.getState()) {
                            enabled = String.valueOf(enabled) + String.format("%s%s", m.getName(), System.lineSeparator());
                        }
                    }
                    String content = "";
                    Module m;
                    for(Iterator var4 = ModuleManager.getModules().iterator(); var4.hasNext(); content = content + String.format("%s:%s%s", new Object[]{m.getName(), Keyboard.getKeyName(m.getKey()), System.lineSeparator()})) {
                        m = (Module)var4.next();
                    }
                    ConfigManager.save(Client.CLIENT_CONFIG + "-binds.cfg", content, false);
                    ConfigManager.save(Client.CLIENT_CONFIG + "-modules.cfg", enabled, false);
                    ConfigManager.save(Client.CLIENT_CONFIG + "-client.cfg", Client.CLIENT_NAME, false);
                    Helper.sendMessage("Configs Saved.");
                    valuetimer.reset();
                }
            } else if (isHovered(windowX + 95, windowY + 95, windowX + 105, windowY + 105, mouseX, mouseY) && Mouse.isButtonDown(0) && valuetimer.delay(500) && Configs.size() >= 6) {
                Helper.sendMessage("Config is full.");
                valuetimer.reset();
            }



            if (isHovered(configInputBox.xPosition, configInputBox.yPosition, configInputBox.xPosition + configInputBox.getWidth(), configInputBox.yPosition + 10, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                configInputBox.mouseClicked(mouseX, mouseY, Mouse.getButtonCount());
            } else if (Mouse.isButtonDown(0) && !isHovered(configInputBox.xPosition, configInputBox.yPosition, configInputBox.xPosition + configInputBox.getWidth(), configInputBox.yPosition + 10, mouseX, mouseY)) {
                configInputBox.setFocused(false);
            }
        }


    }

    public int findArray(float[] a, float b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == b) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

        //顶部图标
        float typeX = windowX + 20;
        for (Enum<?> e : ClickType.values()) {
            // if else
            if (e != ClickType.Settings) {
                if (e == selectType) {
                    if (isHovered(typeX, windowY + 10, typeX + 16 + FontManager.F18.getStringWidth(e.name() + " "), windowY + 10 + 16, mouseX, mouseY)) {
                        selectType = (ClickType) e;
                    }
                    typeX += (32 + FontManager.F18.getStringWidth(e.name() + " "));
                } else {
                    if (isHovered(typeX, windowY + 10, typeX + 16, windowY + 10 + 16, mouseX, mouseY)) {
                        selectType = (ClickType) e;
                    }
                    typeX += (32);
                }
            }else if (isHovered(windowX + width - 32, windowY + 10, windowX + width, windowY + 10 + 16, mouseX, mouseY)) {
                    selectType = (ClickType) e;
                }
        }

        if (selectType == ClickType.Module) {
            //类型列表
            float cateY = windowY + 65;
            for (ModuleType m : ModuleType.values()) {

                if (isHovered(windowX, cateY - 8, windowX + 50, cateY + FontManager.F18.getStringHeight("") + 8, mouseX, mouseY)) {
                    if (modCategory != m) {
                        modsRole = 0;
                    }

                    modCategory = m;
                    for (Module mod : ModuleManager.getModules()){
                        mod.optionAnim = 0;
                        mod.optionAnimNow = 0;

                    }
                }

                cateY += 25;
            }

        }


    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if(!closed && keyCode == Keyboard.KEY_ESCAPE){
            close = true;
            mc.mouseHelper.grabMouseCursor();
            mc.inGameHasFocus = true;
            return;
        }

        if(close) {
            this.mc.displayGuiScreen((GuiScreen) null);
            mc.thePlayer.closeScreen();
        }

        try {
            super.keyTyped(typedChar, keyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (configInputBox.isFocused()) {
            configInputBox.textboxKeyTyped(typedChar, keyCode);
        }
    }

    public static boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    @Override
    public void onGuiClosed(){

    }
}
