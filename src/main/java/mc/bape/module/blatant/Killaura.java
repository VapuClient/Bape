package mc.bape.module.blatant;

import com.ibm.icu.text.NumberFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import mc.bape.manager.FriendManager;
import mc.bape.manager.ModuleManager;
import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import mc.bape.module.combat.AntiBot;
import mc.bape.module.player.Teams;
import mc.bape.utils.CombatUtil;
import mc.bape.utils.TimerUtil;
import mc.bape.utils.math.MathUtil;
import mc.bape.values.Mode;
import mc.bape.values.Numbers;
import mc.bape.values.Option;
import mc.bape.vapu.Client;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;


public class Killaura extends Module {
    public static float rotationPitch;
    public static ArrayList<EntityLivingBase> targets = new ArrayList();
    public static EntityLivingBase target = null;
    public static Numbers<Double> Turnspeed = new Numbers<Double>("TurnSpeed", "TurnSpeed", 90.0, 1.0, 180.0, 1.0);
    public static Numbers<Double> Switchdelay = new Numbers<Double>("Switchdelay", "switchdelay", 11.0, 0.0, 50.0, 1.0);
    public static Numbers<Double> aps = new Numbers<Double>("CPS", "CPS", 10.0, 1.0, 20.0, 0.5);
    public static long lastMS, lastMS2;
    public static float[] facing;
    public static float sYaw;
    static boolean allowCrits;
    private int ticks;
    private int tpdelay;
    public boolean criticals;
    public ArrayList<EntityLivingBase> attackedTargets = new ArrayList();
    public Mode<Enum> Priority = new Mode("Priority", "Priority", priority.values(), priority.Range);
    public Mode<Enum> mode = new Mode("Mode", "Mode", AuraMode.values(), AuraMode.Switch);
    public Mode<Enum> hand = new Mode("Mode", "Mode", handMode.values(), handMode.Vow);
    public Numbers<Double> crack = new Numbers("CrackSize", "CrackSize", Double.valueOf(1.0D), Double.valueOf(0.0D), Double.valueOf(5.0D), Double.valueOf(1.0D));
    public Numbers<Double> reach = new Numbers<Double>("Reach", "Reach", 4.5, 1.0, 6.0, 0.1);
    public Option<Boolean> blocking = new Option<Boolean>("Autoblock", "Autoblock", true);
    public Option<Boolean> invis = new Option<Boolean>("Invisibles", "Invisibles", false);
    public Option<Boolean> autoaim = new Option<Boolean>("AutoAim", "AutoAim", false);
    public Option<Boolean> raycast = new Option("Raycast", "Raycast", Boolean.valueOf(true));
    public TimerUtil test = new TimerUtil();
    public boolean doBlock = false;
    public boolean unBlock = false;
    public long lastMs;
    public float curYaw = 0.0f;
    public float curPitch = 0.0f;
    public int tick = 0;
    public int index;
    public TimerUtil timer = new TimerUtil();
    public float[] facing0;
    public float[] facing1;
    public float[] facing2;
    public float[] facing3;

    public Killaura() {
        super("Killaura", Keyboard.KEY_NONE, ModuleType.Blatant, "Auto Attack entity near you");
        this.addValues(this.hand,  this.mode, aps,reach, this.blocking, this.raycast);
    }


    public static List<Entity> getEntityList() {
        if(Client.nullCheck())
            return null;
        if(mc.theWorld != null){return mc.theWorld.getLoadedEntityList();} else {return null;}
    }

    public boolean check(EntityLivingBase entity) {
        if(Client.nullCheck())
            return false;
        if (entity instanceof EntityArmorStand) {
            return false;
        }
        if (entity == mc.thePlayer) {
            return false;
        }
        if (entity.isDead) {
            return false;
        }
        if (entity.getHealth() == 0 ) {
            return false;
        }
        if(AntiBot.isServerBot(entity)){
            return false;
        }
        if(entity.getDistanceToEntity(mc.thePlayer) > this.reach.getValue()){
            return false;
        }
        return mc.thePlayer.canEntityBeSeen(entity);
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        Object color = null;
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions can't be null");
        }
        if (colors == null) {
            throw new IllegalArgumentException("Colours can't be null");
        }
        if (fractions.length != colors.length) {
            throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
        }
        int[] indicies = Killaura.getFractionIndicies(fractions, progress);
        float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
        Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};
        float max = range[1] - range[0];
        float value = progress - range[0];
        float weight = value / max;
        return Killaura.blend(colorRange[0], colorRange[1], 1.0f - weight);
    }

    public static int[] getFractionIndicies(float[] fractions, float progress) {
        int startPoint;
        int[] range = new int[2];
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = 1.0f - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        } else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        } else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        } else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color = null;
        try {
            color = new Color(red, green, blue);
        } catch (IllegalArgumentException exp) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
            exp.printStackTrace();
        }
        return color;
    }

    public static double random(double min, double max) {
        Random random = new Random();
        return min + (int) (random.nextDouble() * (max - min));
    }

    public static long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public static boolean hit(long milliseconds) {
        return (getCurrentMS() - lastMS) >= milliseconds;
    }

    public static void revert() {
        lastMS = getCurrentMS();
    }

    public static int randomNumber(double min, double max) {
        Random random = new Random();
        return (int) (min + (random.nextDouble() * (max - min)));
    }

    public static int randomNumber1(double min, double max) {
        Random random = new Random();
        return (int) (min + (random.nextDouble() * (max - min)));
    }

    public static float[] getRotationsNeededBlock(final double n, final double n2, final double n3) {
        final double n4 = n + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
        final double n5 = n3 + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
        return new float[]{Minecraft.getMinecraft().thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float((float) (Math.atan2(n5, n4) * 180.0 / 3.141592653589793) - 90.0f - Minecraft.getMinecraft().thePlayer.rotationYaw), Minecraft.getMinecraft().thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float((float) (-Math.atan2(n2 + 0.5 - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight()), MathHelper.sqrt_double(n4 * n4 + n5 * n5)) * 180.0 / 3.141592653589793) - Minecraft.getMinecraft().thePlayer.rotationPitch)};
    }

    public static float[] getRotationFromPosition(final double n, final double n2, final double n3) {
        final double n4 = n - Minecraft.getMinecraft().thePlayer.posX;
        final double n5 = n2 - Minecraft.getMinecraft().thePlayer.posZ;
        return new float[]{(float) (Math.atan2(n5, n4) * 180.0 / 3.141592653589793) - 90.0f, (float) (-Math.atan2(n3 - Minecraft.getMinecraft().thePlayer.posY - 1.2, MathHelper.sqrt_double(n4 * n4 + n5 * n5)) * 180.0 / 3.141592653589793)};
    }

    public static float[] getRotations(final Entity entity) {
        if(Client.nullCheck()) return null;
        if(entity == null) return null;
        return getRotationFromPosition(entity.posX, entity.posZ, entity.posY + entity.getEyeHeight() / 2.0f);
    }

    public static double getRandomDoubleInRange(double minDouble, double maxDouble) {
        return minDouble >= maxDouble ? minDouble : new Random().nextDouble() * (maxDouble - minDouble) + minDouble;
    }

    public static float[] getRotationToEntity(Entity target) {
        Minecraft.getMinecraft();
        double xDiff = target.posX - mc.thePlayer.posX;
        Minecraft.getMinecraft();
        double yDiff = target.posY - mc.thePlayer.posY;
        Minecraft.getMinecraft();
        double zDiff = target.posZ - mc.thePlayer.posZ;
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        float pitch = (float) (-Math.atan2(target.posY + (double) target.getEyeHeight() / 0.0 - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight()), Math.hypot(xDiff, zDiff)) * 180.0 / 3.141592653589793);
        if (yDiff > -0.2 && yDiff < 0.2) {
            Minecraft.getMinecraft();
            Minecraft.getMinecraft();
            pitch = (float) (-Math.atan2(target.posY + (double) target.getEyeHeight() / HitLocation.CHEST.getOffset() - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight()), Math.hypot(xDiff, zDiff)) * 180.0 / 3.141592653589793);
        } else if (yDiff > -0.2) {
            Minecraft.getMinecraft();
            Minecraft.getMinecraft();
            pitch = (float) (-Math.atan2(target.posY + (double) target.getEyeHeight() / HitLocation.FEET.getOffset() - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight()), Math.hypot(xDiff, zDiff)) * 180.0 / 3.141592653589793);
        } else if (yDiff < 0.3) {
            Minecraft.getMinecraft();
            Minecraft.getMinecraft();
            pitch = (float) (-Math.atan2(target.posY + (double) target.getEyeHeight() / HitLocation.HEAD.getOffset() - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight()), Math.hypot(xDiff, zDiff)) * 180.0 / 3.141592653589793);
        }
        return new float[]{yaw, pitch};
    }

    public static float ROL(float angle1, float angle2) {
        float angle3 = Math.abs(angle1 - angle2) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 0.0f;
        }
        return angle3;
    }

    public static float getYawDifference(float current, float target) {
        float rot = 0;
        return rot + ((rot = (target + 180.0f - current) % 360.0f) > 0.0f ? -180.0f : 180.0f);
    }

    public void color(int color) {
        float f = (float) (color >> 24 & 255) / 255.0f;
        float f2 = (float) (color >> 16 & 255) / 255.0f;
        float f3 = (float) (color >> 8 & 255) / 255.0f;
        float f4 = (float) (color & 255) / 255.0f;
        GL11.glColor4f(f2, f3, f4, f);
    }

    public void drawRect(double x1, double y1, double x2, double y2, int color) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        this.color(color);
        GL11.glBegin(7);
        GL11.glVertex2d(x2, y1);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x1, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        this.drawRect(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawRect(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawRect(x, y, x + width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawRect(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawRect(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public boolean shouldAttack() {
        return this.timer.hasReached(1000 / aps.getValue().intValue());
    }

    public void drawShadow(Entity entity, float partialTicks, float pos, boolean direction) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(7425);
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks - mc.getRenderManager().viewerPosY + pos;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks - mc.getRenderManager().viewerPosZ;
        GL11.glBegin(GL11.GL_QUAD_STRIP);
        for (int i = 0; i <= 180; i++) {
            double c1 = i * Math.PI * 2 / 180;
            double c2 = (i + 1) * Math.PI * 2 / 180;
            GlStateManager.color(1, 1, 1, 0.3f);
            GL11.glVertex3d(x + 0.5 * Math.cos(c1), y, z + 0.5 * Math.sin(c1));
            GL11.glVertex3d(x + 0.5 * Math.cos(c2), y, z + 0.5 * Math.sin(c2));
            GlStateManager.color(1, 1, 1, 0f);

            GL11.glVertex3d(x + 0.5 * Math.cos(c1), y + (direction ? -0.2 : 0.2), z + 0.5 * Math.sin(c1));
            GL11.glVertex3d(x + 0.5 * Math.cos(c2), y + (direction ? -0.2 : 0.2), z + 0.5 * Math.sin(c2));


        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(7424);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public void drawCircle(Entity entity, float partialTicks, float pos) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(7425);
        GL11.glLineWidth(1);
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks - mc.getRenderManager().viewerPosY + pos;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks - mc.getRenderManager().viewerPosZ;
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 180; i++) {
            double c1 = i * Math.PI * 2 / 180;
            GlStateManager.color(2, 1, 1, 1);
            GL11.glVertex3d(x + 0.5 * Math.cos(c1), y, z + 0.5 * Math.sin(c1));


        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(7424);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public boolean canBlock() {
        return mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword;
    }

    @SubscribeEvent
    public void onUpdate(final TickEvent.PlayerTickEvent event) {
        this.status = this.mode.getValue().toString();
        if (mc.thePlayer.ticksExisted % Switchdelay.getValue().intValue() == 0 && targets.size() > 1) {
            ++this.index;
        }
        if (!targets.isEmpty() && this.index >= targets.size()) {
            this.index = 0;
        }
        if (this.autoaim.getValue().booleanValue()) {
            if (target != null) {
                float[] rotations = CombatUtil.getRotations(target);
                mc.thePlayer.rotationYawHead = rotations[0];
                mc.thePlayer.rotationYaw = rotations[0];
            }
        }
        this.doBlock = false;
        this.clear();
        this.findTargets(event);
        this.setCurTarget();
        if (this.hand.getValue() == handMode.Vow) {
            if (target != null) {
                if(Client.nullCheck())
                    return;
                Random rand = new Random();
                this.facing0 = Killaura.getRotationsNeededBlock(Killaura.target.posX, Killaura.target.posY, Killaura.target.posZ);
                this.facing1 = Killaura.getRotationFromPosition(Killaura.target.posX, Killaura.target.posY, Killaura.target.posZ);
                this.facing2 = Killaura.getRotationsNeededBlock(Killaura.target.posX, Killaura.target.posY, Killaura.target.posZ);
                this.facing3 = Killaura.getRotations(target);
                for (int i2 = 0; i2 <= 3; ++i2) {
                    switch (Killaura.randomNumber(0.0, i2)) {
                        case 0: {
                            facing = this.facing0;
                        }
                        case 1: {
                            facing = this.facing1;
                        }
                        case 2: {
                            facing = this.facing2;
                        }
                        case 3: {
                            facing = this.facing3;
                        }
                    }
                }
                if (facing.length >= 0) {
                    event.player.setRotationYawHead(facing[0]);
                    event.player.cameraPitch = facing[1];
                }
                if (target != null) {
                    mc.thePlayer.renderYawOffset = facing[0];
                    mc.thePlayer.rotationYawHead = facing[0];
                }
            } else {
                targets.clear();
                this.attackedTargets.clear();
                this.lastMs = System.currentTimeMillis();
                if (this.unBlock) {
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    mc.thePlayer.setItemInUse(mc.thePlayer.getItemInUse(), 0);
                    this.unBlock = false;
                }
            }
        }
        if (this.hand.getValue() == handMode.Nov) {
            if (Killaura.target != null) {
                final Random rand = new Random();
                this.facing0 = getRotationsNeededBlock(Killaura.target.posX, Killaura.target.posY, Killaura.target.posZ);
                this.facing1 = getRotationFromPosition(Killaura.target.posX, Killaura.target.posY, Killaura.target.posZ);
                this.facing2 = getRotationsNeededBlock(Killaura.target.posX, Killaura.target.posY, Killaura.target.posZ);
                this.facing3 = getRotations(Killaura.target);
                for (int i = 0; i <= 3; ++i) {
                    switch (randomNumber(0.0, i)) {
                        case 0: {
                            Killaura.facing = this.facing0;
                        }
                        case 1: {
                            Killaura.facing = this.facing1;
                        }
                        case 2: {
                            Killaura.facing = this.facing2;
                        }
                        case 3: {
                            Killaura.facing = this.facing3;
                            break;
                        }
                    }
                }
                if (Killaura.facing.length >= 0) {
                    Turnspeed.getValue().intValue();
                    event.player.setRotationYawHead(Killaura.facing[0]);
                    Turnspeed.getValue().intValue();
                    event.player.cameraPitch = Killaura.facing[1];
                }

                if (Killaura.target != null) {
                    final Minecraft mc = Killaura.mc;
                    mc.thePlayer.renderYawOffset = Killaura.facing[0];
                    final Minecraft mc2 = Killaura.mc;
                    mc.thePlayer.rotationYawHead = Killaura.facing[0];
                }
                final int maxAngleStep = Turnspeed.getValue().intValue();
                final int xz = (int) (randomNumber1(maxAngleStep, maxAngleStep) / 100.0);
                Random rand1 = new Random();
                facing0 = getRotationsNeededBlock(target.posX, target.posY, target.posZ);
                facing1 = getRotationFromPosition(target.posX, target.posY, target.posZ);
                facing2 = getRotationsNeededBlock(target.posX, target.posY, target.posZ);
                facing3 = getRotations(target);
                int i;
                for (i = 0; i <= 3; i++) {
                    switch (randomNumber1(0, i)) {
                        case 0:
                            facing = facing0;
                        case 1:
                            facing = facing1;
                        case 2:
                            facing = facing2;
                        case 3:
                            facing = facing3;
                    }
                }

                if (facing.length >= 0) {
                    event.player.setRotationYawHead((facing[0]));
                    event.player.cameraPitch = facing[1];
                }
                if (Killaura.target != null) {
                    mc.thePlayer.renderYawOffset = facing[0];
                    mc.thePlayer.rotationYawHead = facing[0];
                }
            } else {
                targets.clear();
                this.attackedTargets.clear();
                this.lastMs = System.currentTimeMillis();
                if (this.unBlock) {
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    mc.thePlayer.setItemInUse(mc.thePlayer.getItemInUse(), 0);
                    this.unBlock = false;
                }
            }
        }
    }

    public void doAttack() {
        if(Client.nullCheck())
            return;
        int aps = Killaura.aps.getValue().intValue();
        int delayValue = (int) (1000 / Killaura.aps.getValue().intValue() + MathUtil.randomDouble(-1.0, 2.0));

        if ((double) mc.thePlayer.getDistanceToEntity(target) <= this.reach.getValue() + 0.4 && this.tick == 0 && this.test.delay(delayValue - 1)) {
            boolean miss = false;
            this.test.reset();

            if (mc.thePlayer.isBlocking() || mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && this.blocking.getValue().booleanValue()) {
                if (new Random().nextBoolean()) {
                    if (mc.thePlayer.getCurrentEquippedItem() == null) {
                        return;
                    }
                    if (!(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword)) {
                        return;
                    }
//                    if (mc.objectMouseOver.entityHit != null && mc.objectMouseOver.entityHit.isEntityAlive()) {
//                        {
//                            // 格挡
//                            mc.thePlayer.getCurrentEquippedItem().useItemRightClick(mc.theWorld, mc.thePlayer);
//                        }
//                    }
                    if (mc.objectMouseOver.entityHit != null && mc.objectMouseOver.entityHit.isEntityAlive()){
                        if (mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword && timer.delay(100)) {
                            mc.thePlayer.getCurrentEquippedItem().useItemRightClick(mc.theWorld, mc.thePlayer);
                            timer.reset();
                        }
                    }
                }
                this.unBlock = false;
            }
            if (!mc.thePlayer.isBlocking() && !this.blocking.getValue().booleanValue() && mc.thePlayer.getItemInUseCount() > 0) {
                mc.thePlayer.setItemInUse(mc.thePlayer.getItemInUse(), 0);
            }

            this.attack(miss);
            this.doBlock = true;
            if (!miss) {
                for (Object o : mc.theWorld.loadedEntityList) {
                    EntityLivingBase entity;
                    if (!(o instanceof EntityLivingBase) || !this.isValidEntity(entity = (EntityLivingBase) o))
                        continue;
                    this.attackedTargets.add(target);
                }
            }
        }

    }

    public void swap(int slot, int hotbarNum) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2, mc.thePlayer);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if(Client.nullCheck())
            return;
        this.sortList(targets);
        if (target != null) {
            this.doAttack();
            this.newAttack();
        }
        if (target != null && (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && this.blocking.getValue().booleanValue() || mc.thePlayer.isBlocking()) && this.doBlock) {
            mc.thePlayer.setItemInUse(mc.thePlayer.getItemInUse(), mc.thePlayer.getHeldItem().getMaxItemUseDuration());
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0f, 0.0f, 0.0f));
            this.unBlock = true;
        }

        int i2 = 0;
    }

    public void attack(boolean fake) {
        if(Client.nullCheck())
            return;
        // 防止Crash[1]
        mc.thePlayer.swingItem();
        if (!fake) {
            this.doBlock = true;
            if (ModuleManager.getModule("Criticals").getState()) {
                if (mc.thePlayer.onGround && !Criticals.canJump() && target.hurtTime <= 1) {
                    if(Criticals.mode.getValue() == Criticals.CriticalsMode.Jump && Criticals.mode.getValue() == Criticals.CriticalsMode.Legit){
                        mc.thePlayer.jump();
                    } else if (Criticals.mode.getValue() == Criticals.CriticalsMode.Watchdog){
                        Criticals.doWatchdogCirt(); //？
                    }
                }
            }
            if(!Client.nullCheck() && target != null){
                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(Killaura.target, C02PacketUseEntity.Action.ATTACK));
                if (mc.thePlayer.isBlocking() && this.blocking.getValue().booleanValue() && mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                    if (new Random().nextBoolean())
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0f, 0.0f, 0.0f));
                    this.unBlock = true;
                }
                if (!mc.thePlayer.isBlocking() && !this.blocking.getValue().booleanValue() && mc.thePlayer.getItemInUseCount() > 0) {
                    mc.thePlayer.setItemInUse(mc.thePlayer.getItemInUse(), 0);
                }
            }
        }
    }

    public void newAttack() {
        if(Client.nullCheck())
            return;
        if (mc.thePlayer.isBlocking()) {
            for (int i = 0; i <= 2; i++) {
                if (new Random().nextBoolean())
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0f, 0.0f, 0.0f));
            }
        }
        if (mc.thePlayer.isBlocking()) {
            for (int i = 0; i <= 2; i++) {
                if (new Random().nextBoolean())
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0f, 0.0f, 0.0f));
            }
        }
        if (mc.thePlayer.isBlocking() && this.timer.delay(100)) {
            for (int i = 0; i <= 2; i++) {
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
        }
        if (!mc.thePlayer.isBlocking() && !this.blocking.getValue().booleanValue() && mc.thePlayer.getItemInUseCount() > 0) {
            mc.thePlayer.setItemInUse(mc.thePlayer.getItemInUse(), 0);
        }
    }

    public void setCurTarget() {
        if(Client.nullCheck())
            return;
        for (Entity object : getEntityList()) {
            EntityLivingBase entity;
            if (!(object instanceof EntityLivingBase) || !this.check(entity = (EntityLivingBase) object)) continue;
            target = entity;
        }
    }

    public void clear() {
        if(Client.nullCheck())
            return;
        target = null;
        targets.clear();
        for (EntityLivingBase ent : targets) {
            if (this.isValidEntity(ent)) continue;
            targets.remove(ent);
            if (!this.attackedTargets.contains(ent)) continue;
            this.attackedTargets.remove(ent);
        }
    }

    public void findTargets(TickEvent.PlayerTickEvent event) {
        if(Client.nullCheck())
            return;
        int maxSize = this.mode.getValue() == AuraMode.Switch ? 4 : 1;
        for (Entity o3 : mc.theWorld.loadedEntityList) {
            EntityLivingBase curEnt;
            if (o3 instanceof EntityLivingBase && this.isValidEntity(curEnt = (EntityLivingBase) o3) && !targets.contains(curEnt)) {
                targets.add(curEnt);
            }
            if (targets.size() >= maxSize) break;
        }
        try {
            targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(o2) - o2.getDistanceToEntity(o1)));
        } catch(ConcurrentModificationException e){
//            System.out.print("我操不是吧，又是" + e.toString());
        }

    }

    public boolean isValidEntity(EntityLivingBase ent) {
        if(Client.nullCheck())
            return true;
        AntiBot ab = (AntiBot) ModuleManager.getModule("AntiBot");
        return ent != null && (ent != mc.thePlayer && (((((!(ent instanceof EntityMob) && !(ent instanceof EntityVillager) && !(ent instanceof EntityBat)) || true) && (!((double) mc.thePlayer.getDistanceToEntity(ent) > this.reach.getValue() + 0.4) && ((!(ent instanceof EntityPlayer) || !FriendManager.isFriend(ent.getName())) && (!ent.isDead && ent.getHealth() > 0.0F && ((!ent.isInvisible() || this.invis.getValue()) && !ab.isServerBot(ent) && (!this.mc.thePlayer.isDead && (!(ent instanceof EntityPlayer) || !Teams.isOnSameTeam(ent)))))))))));
    }

    @Override
    public void enable() {
        index = 0;
        this.curYaw = mc.thePlayer.rotationYaw;
        this.curPitch = mc.thePlayer.rotationPitch;
    }

    @Override
    public void disable() {
        targets.clear();
        this.attackedTargets.clear();
        target = null;
        mc.thePlayer.setItemInUse(mc.thePlayer.getItemInUse(), 0);
        allowCrits = true;
        mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw;
        rotationPitch = 0.0f;
        this.curYaw = mc.thePlayer.rotationYaw;
        this.curPitch = mc.thePlayer.rotationPitch;
    }

    public void sortList(List<EntityLivingBase> weed) {
        if (this.Priority.getValue() == priority.Range) {
            try {
                weed.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) - o2.getDistanceToEntity(mc.thePlayer)));
            } catch (ConcurrentModificationException exception) {
//            System.out.print("我操不是吧，又是" + e.toString());
            }
        }
    }

    public float getYawDifference(float yaw, EntityLivingBase target) {
        return Killaura.getYawDifference(yaw, Killaura.getRotationToEntity(target)[0]);
    }

    enum HitLocation {
        AUTO(0.0), HEAD(1.0), CHEST(1.5), FEET(3.5);

        public double offset;

        HitLocation(double offset) {
            this.offset = offset;
        }

        public double getOffset() {
            return this.offset;
        }
    }

    enum priority {
        Range
    }

    enum AuraMode {
        Switch, Single, Multi
    }

    enum handMode {
        Vow, Nov
    }



    @SubscribeEvent
    public void doMultiAttack(TickEvent.PlayerTickEvent event) {
        if(Client.nullCheck())
            return;
        if(this.mode.getValue() == AuraMode.Multi) {
            try{
                ++this.ticks;
                ++this.tpdelay;
                if (this.ticks >= 20 - this.speed()) {
                    this.ticks = 0;
                    for (Object object : this.mc.theWorld.loadedEntityList) {
                        EntityLivingBase entity;
                        if (!(object instanceof EntityLivingBase) || (entity = (EntityLivingBase) object) instanceof EntityPlayerSP || this.mc.thePlayer.getDistanceToEntity(entity) > 10.0f || !entity.isEntityAlive())
                            continue;
                        if (this.tpdelay >= 4) {
                            this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(entity.posX, entity.posY, entity.posZ, false));
                        }
                        if (this.mc.thePlayer.getDistanceToEntity(entity) >= 10.0f) continue;
                        this.attack(entity);
                    }
                }
            } catch (ConcurrentModificationException exception){
//                Helper.sendMessage("脑残");
            }
        }
    }

    public void attack(EntityLivingBase entity) {
        if(Client.nullCheck())
            return;
        this.attack(entity, false);
    }

    public void attack(EntityLivingBase entity, boolean crit) {
        if(Client.nullCheck())
            return;
        try{
            this.mc.thePlayer.swingItem();
            float sharpLevel = EnchantmentHelper.getModifierForCreature(this.mc.thePlayer.getHeldItem(), entity.getCreatureAttribute());
            boolean vanillaCrit = this.mc.thePlayer.fallDistance > 0.0f && !this.mc.thePlayer.onGround && !this.mc.thePlayer.isOnLadder() && !this.mc.thePlayer.isInWater() && !this.mc.thePlayer.isPotionActive(Potion.blindness) && this.mc.thePlayer.ridingEntity == null;
            this.mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity((Entity)entity, C02PacketUseEntity.Action.ATTACK));
            if (crit || vanillaCrit) {
                this.mc.thePlayer.onCriticalHit(entity);
            }
            if (sharpLevel > 0.0f) {
                this.mc.thePlayer.onEnchantmentCritical(entity);
            }
        } catch (ConcurrentModificationException exception){
//            Helper.sendMessage("我日");
        }
    }

    private int speed() {
        return 8;
    }
}
