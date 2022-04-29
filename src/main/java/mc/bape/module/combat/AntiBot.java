package mc.bape.module.combat;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import mc.bape.module.ModuleType;
import mc.bape.module.Module;
import mc.bape.manager.ModuleManager;
import mc.bape.values.Mode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

public class AntiBot extends Module {
    private static Mode<Enum> mode = new Mode("Mode", "mode", (Enum[]) AntiBot.antibotmode.values(), (Enum) AntiBot.antibotmode.Hypixel);
        public AntiBot() {
            super("AntiBot", Keyboard.KEY_NONE, ModuleType.Combat,"Make target exclude the bots");
            this.addValues(this.mode);
            Chinese="反机器人";
        }

    static enum antibotmode {
        Hypixel,
        Mineplex,
        Syuu,
        Vanilla
    }

    @SubscribeEvent
    public void onTick(TickEvent e){
        this.status = this.mode.getValue().toString();
    }

    public static double getEntitySpeed(Entity entity) {
        double xDif = entity.posX - entity.prevPosX;
        double zDif = entity.posZ - entity.prevPosZ;
        return (Math.sqrt(xDif * xDif + zDif * zDif) * 20.0);
    }

        public static boolean isServerBot(Entity entity) {
            if (Objects.requireNonNull(ModuleManager.getModule("AntiBot")).getState()) {
                if(AntiBot.mode.getValue() == antibotmode.Hypixel){
                    for (Entity entitys : mc.theWorld.loadedEntityList) {
                        if(!entity.getDisplayName().getFormattedText().startsWith("\u00a7") ||
                                entity.isInvisible() ||
                                entity.getDisplayName().getFormattedText().toLowerCase().contains("npc")){
                            return true;
                        }
                    }
                } else if(AntiBot.mode.getValue() == antibotmode.Mineplex){
                    for (Object object : mc.theWorld.playerEntities) {
                        EntityPlayer entityPlayer = (EntityPlayer)object;
                        if (entityPlayer == null || entityPlayer == mc.thePlayer || !entityPlayer.getName().startsWith("Body #") && entityPlayer.getMaxHealth() != 20.0f) continue;
                        return true;
                    }
                } else if(AntiBot.mode.getValue() == antibotmode.Syuu){
                    for (Entity entitys : mc.theWorld.loadedEntityList) {
                        if (entity == mc.thePlayer) continue;
                        if (entity instanceof EntityPlayer) {
                            final EntityPlayer entityPlayer = (EntityPlayer)entity;
                            if (entityPlayer.isInvisible() && entityPlayer.getHealth() > 1000.0f && getEntitySpeed(entityPlayer) > 20) {
                                return true;
                            }
                        }
                    }
                } else if(AntiBot.mode.getValue() == antibotmode.Vanilla){
                    if(!entity.getDisplayName().getFormattedText().startsWith("\u00a7") ||
                            entity.isInvisible() ||
                            entity.getDisplayName().getFormattedText().toLowerCase().contains("npc")){
                        return true;
                    }

                }
            }
            return false;
        }
    }

