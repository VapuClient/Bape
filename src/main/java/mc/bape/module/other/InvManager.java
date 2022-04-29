package mc.bape.module.other;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import mc.bape.manager.ModuleManager;
import mc.bape.module.Module;
import mc.bape.module.ModuleType;
import mc.bape.module.blatant.AutoArmor;
import mc.bape.utils.TimerUtil;
import mc.bape.values.Mode;
import mc.bape.values.Numbers;
import mc.bape.values.Option;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvManager extends Module {

    static final List<Block> blacklisted = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.ender_chest, Blocks.yellow_flower, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.crafting_table, Blocks.snow_layer, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.cactus, Blocks.lever, Blocks.activator_rail, Blocks.rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.furnace, Blocks.ladder, Blocks.oak_fence, Blocks.redstone_torch, Blocks.iron_trapdoor, Blocks.trapdoor, Blocks.tripwire_hook, Blocks.hopper, Blocks.acacia_fence_gate, Blocks.birch_fence_gate, Blocks.dark_oak_fence_gate, Blocks.jungle_fence_gate, Blocks.spruce_fence_gate, Blocks.oak_fence_gate, Blocks.dispenser, Blocks.sapling, Blocks.tallgrass, Blocks.deadbush, Blocks.web, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.nether_brick_fence, Blocks.vine, Blocks.double_plant, Blocks.flower_pot, Blocks.beacon, Blocks.pumpkin, Blocks.lit_pumpkin);
    TimerUtil timer = new TimerUtil();
    Minecraft mc = Minecraft.getMinecraft();
    private Numbers<Double> BlockCap = new Numbers<Double>("BlockCap", "BlockCap", 128.0, 0.0, 256.0, 8.0);
    private Numbers<Double> Delay = new Numbers<Double>("Delay", "Delay", 1.0, 0.0, 10.0, 1.0);
    private Option<Boolean> Food = new Option<Boolean>("Food", "Food", true);
    private Option<Boolean> sort = new Option<Boolean>("sort", "sort", true);
    private Option<Boolean> Archery = new Option<Boolean>("Archery", "Archery", true);
    private Option<Boolean> Sword = new Option<Boolean>("Sword", "Sword", true);
    private mc.bape.values.Mode<Enum> Mode = new Mode("Mode", "Mode", (Enum[])EMode.values(), (Enum)EMode.Basic);
    private Option<Boolean> UHC = new Option<Boolean>("UHC", "UHC", false);
    public static int weaponSlot = 36;
    public static int pickaxeSlot = 37;
    public static int axeSlot = 38;
    public static int shovelSlot = 39;

    public InvManager() {
        super("InvManager", Keyboard.KEY_NONE, ModuleType.Blatant,"Auto Manage your items when openinv");
        this.addValues(this.BlockCap, this.Delay, this.Food, this.Archery, this.Sword, this.Mode, this.sort, this.UHC);
        Chinese="背包管理";
    }

    @Override
    public void enable() {
        super.enable();
    }

    @SubscribeEvent
    public void onEvent(TickEvent.PlayerTickEvent event) {
        ItemStack is;
        if (this.isMoving()) {
            return;
        }
        if (mc.thePlayer.openContainer instanceof ContainerChest && mc.currentScreen instanceof GuiContainer) {
            return;
        }
        InvManager i3 = (InvManager) ModuleManager.getModule("InvManager");
        long delay = ((Double)this.Delay.getValue()).longValue() * 50L;
        long Adelay = ((Double)AutoArmor.DELAY.getValue()).longValue() * 50L;
        if (this.timer.hasReached(Adelay) && i3.getState() && (i3.Mode.getValue() != AutoArmor.EMode.OpenInv || mc.currentScreen instanceof GuiInventory) && (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat)) {
            this.getBestArmor();
        }
        if (i3.getState()) {
            int type = 1;
            while (type < 5) {
                if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                    is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                    if (!AutoArmor.isBestArmor(is, type)) {
                        return;
                    }
                } else if (this.invContainsType(type - 1)) {
                    return;
                }
                ++type;
            }
        }
        if (this.Mode.getValue() == EMode.OpenInv && !(mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) {
            if (this.timer.hasReached(delay) && weaponSlot >= 36) {
                if (!mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getHasStack()) {
                    this.getBestWeapon(weaponSlot);
                } else if (!this.isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getStack())) {
                    this.getBestWeapon(weaponSlot);
                }
            }
            if (((Boolean)this.sort.getValue()).booleanValue()) {
                if (this.timer.hasReached(delay) && pickaxeSlot >= 36) {
                    this.getBestPickaxe(pickaxeSlot);
                }
                if (this.timer.hasReached(delay) && shovelSlot >= 36) {
                    this.getBestShovel(shovelSlot);
                }
                if (this.timer.hasReached(delay) && axeSlot >= 36) {
                    this.getBestAxe(axeSlot);
                }
            }
            if (this.timer.hasReached(delay)) {
                int i = 9;
                while (i < 45) {
                    if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                        is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                        if (this.shouldDrop(is, i)) {
                            this.drop(i);
                            this.timer.reset();
                            if (delay > 0L) break;
                        }
                    }
                    ++i;
                }
            }
        }
    }

    public boolean shouldDrop(ItemStack stack, int slot) {
        block88: {
            block87: {
                block84: {
                    block86: {
                        block85: {
                            block83: {
                                if (stack.getDisplayName().contains("\u70b9\u51fb")) {
                                    return false;
                                }
                                if (stack.getDisplayName().contains("\u53f3\u952e")) {
                                    return false;
                                }
                                if (stack.getDisplayName().toLowerCase().contains("(right click)")) {
                                    return false;
                                }
                                if (stack.getItem() instanceof ItemSkull) {
                                    return false;
                                }
                                if (((Boolean)this.UHC.getValue()).booleanValue()) {
                                    if (stack.getDisplayName().toLowerCase().contains("\u5934")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("apple")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("head")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("gold")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("crafting table")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("stick")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("and") && stack.getDisplayName().toLowerCase().contains("ril")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("axe of perun")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("barbarian")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("bloodlust")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("dragonchest")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("dragon sword")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("dragon armor")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("excalibur")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("exodus")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("fusion armor")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("hermes boots")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("hide of leviathan")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("scythe")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("seven-league boots")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("shoes of vidar")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("apprentice")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("master")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("vorpal")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("enchanted")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("spiked")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("tarnhelm")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("philosopher")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("anvil")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("panacea")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("fusion")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("excalibur")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u5b66\u5f92")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u5927\u5e08\u7f57\u76d8")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u65a9\u9996\u4e4b\u5251")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u9644\u9b54")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u5de8\u9f99\u4e4b\u5251")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u5de8\u9f99\u4e4b\u7532")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u5203\u7532")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u4e03\u56fd\u6218\u9774")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u51b0\u6597\u6e56")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u54f2\u4eba")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u94c1\u7827")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u82f9\u679c")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u91d1")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u6c38\u751f\u4e4b\u9152")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u4e18\u6bd4\u7279\u4e4b\u5f13")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u953b\u7089")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("backpack")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u805a\u53d8\u4e4b\u7532")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u80cc\u5305")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u6708\u795e")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u6c38\u751f")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u6f6e\u6c50")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u96f7\u65a7")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u738b\u8005\u4e4b\u5251")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u5b89\u90fd\u745e\u5c14")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u6b7b\u795e\u9570\u5200")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u4e30\u9976\u4e4b\u89d2")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u7ef4\u8fbe\u6218\u9774")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u593a\u9b42\u4e4b\u5203")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u86ee\u4eba\u4e4b\u7532")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("\u7a83\u8d3c\u4e4b\u9774")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("hermes")) {
                                        return false;
                                    }
                                    if (stack.getDisplayName().toLowerCase().contains("barbarian")) {
                                        return false;
                                    }
                                }
                                if (slot != weaponSlot) break block83;
                                if (this.isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getStack())) break block84;
                            }
                            if (slot != pickaxeSlot) break block85;
                            if (this.isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack()) && pickaxeSlot >= 0) break block84;
                        }
                        if (slot != axeSlot) break block86;
                        if (this.isBestAxe(mc.thePlayer.inventoryContainer.getSlot(axeSlot).getStack()) && axeSlot >= 0) break block84;
                    }
                    if (slot != shovelSlot) break block87;
                    if (!this.isBestShovel(mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getStack()) || shovelSlot < 0) break block87;
                }
                return false;
            }
            if (!(stack.getItem() instanceof ItemArmor)) break block88;
            int type = 1;
            while (type < 5) {
                block90: {
                    block89: {
                        if (!mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) break block89;
                        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                        if (AutoArmor.isBestArmor(is, type)) break block90;
                    }
                    if (AutoArmor.isBestArmor(stack, type)) {
                        return false;
                    }
                }
                ++type;
            }
        }
        if (((Double)this.BlockCap.getValue()).intValue() != 0 && stack.getItem() instanceof ItemBlock && (this.getBlockCount() > ((Double)this.BlockCap.getValue()).intValue() || this.blacklisted.contains(((ItemBlock)stack.getItem()).getBlock()))) {
            return true;
        }
        if (stack.getItem() instanceof ItemPotion && this.isBadPotion(stack)) {
            return true;
        }
        if (stack.getItem() instanceof ItemFood && (Boolean) this.Food.getValue() && !(stack.getItem() instanceof ItemAppleGold)) {
            return true;
        }
        if (stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemArmor) {
            return true;
        }
        if ((stack.getItem() instanceof ItemBow || stack.getItem().getUnlocalizedName().contains("arrow")) && ((Boolean)this.Archery.getValue()).booleanValue()) {
            return true;
        }
        return stack.getItem().getUnlocalizedName().contains("tnt") || stack.getItem().getUnlocalizedName().contains("stick") || stack.getItem().getUnlocalizedName().contains("egg") || stack.getItem().getUnlocalizedName().contains("string") || stack.getItem().getUnlocalizedName().contains("cake") || stack.getItem().getUnlocalizedName().contains("mushroom") || stack.getItem().getUnlocalizedName().contains("flint") || stack.getItem().getUnlocalizedName().contains("compass") || stack.getItem().getUnlocalizedName().contains("dyePowder") || stack.getItem().getUnlocalizedName().contains("feather") || stack.getItem().getUnlocalizedName().contains("bucket") || stack.getItem().getUnlocalizedName().contains("chest") && !stack.getDisplayName().toLowerCase().contains("collect") || stack.getItem().getUnlocalizedName().contains("snow") || stack.getItem().getUnlocalizedName().contains("fish") || stack.getItem().getUnlocalizedName().contains("enchant") || stack.getItem().getUnlocalizedName().contains("exp") || stack.getItem().getUnlocalizedName().contains("shears") || stack.getItem().getUnlocalizedName().contains("anvil") || stack.getItem().getUnlocalizedName().contains("torch") || stack.getItem().getUnlocalizedName().contains("seeds") || stack.getItem().getUnlocalizedName().contains("leather") || stack.getItem().getUnlocalizedName().contains("reeds") || stack.getItem().getUnlocalizedName().contains("skull") || stack.getItem().getUnlocalizedName().contains("record") || stack.getItem().getUnlocalizedName().contains("snowball") || stack.getItem() instanceof ItemGlassBottle || stack.getItem().getUnlocalizedName().contains("piston");
    }

    private boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion)stack.getItem();
            if (potion.getEffects(stack) == null) {
                return true;
            }
            for (PotionEffect o : potion.getEffects(stack)) {
                PotionEffect effect = o;
                if (effect.getPotionID() != Potion.poison.getId() && effect.getPotionID() != Potion.harm.getId() && effect.getPotionID() != Potion.moveSlowdown.getId() && effect.getPotionID() != Potion.weakness.getId()) continue;
                return true;
            }
        }
        return false;
    }

    private int getBlockCount() {
        int blockCount = 0;
        int i = 0;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (is.getItem() instanceof ItemBlock && !blacklisted.contains(((ItemBlock)item).getBlock())) {
                    blockCount += is.stackSize;
                }
            }
            ++i;
        }
        return blockCount;
    }

    private void getBestAxe(int slot) {
        int i = 9;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (this.isBestAxe(is) && axeSlot != i && !this.isBestWeapon(is)) {
                    if (!mc.thePlayer.inventoryContainer.getSlot(axeSlot).getHasStack()) {
                        this.swap(i, axeSlot - 36);
                        this.timer.reset();
                        if (((Double)this.Delay.getValue()).longValue() > 0L) {
                            return;
                        }
                    } else if (!this.isBestAxe(mc.thePlayer.inventoryContainer.getSlot(axeSlot).getStack())) {
                        this.swap(i, axeSlot - 36);
                        this.timer.reset();
                        if (((Double)this.Delay.getValue()).longValue() > 0L) {
                            return;
                        }
                    }
                }
            }
            ++i;
        }
    }

    private boolean isBestAxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemAxe)) {
            return false;
        }
        float value = this.getToolEffect(stack);
        int i = 9;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (this.getToolEffect(is) > value && is.getItem() instanceof ItemAxe && !this.isBestWeapon(stack)) {
                    return false;
                }
            }
            ++i;
        }
        return true;
    }

    private void getBestShovel(int slot) {
        int i = 9;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (this.isBestShovel(is) && shovelSlot != i && !this.isBestWeapon(is)) {
                    if (!mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getHasStack()) {
                        this.swap(i, shovelSlot - 36);
                        this.timer.reset();
                        if (((Double)this.Delay.getValue()).longValue() > 0L) {
                            return;
                        }
                    } else if (!this.isBestShovel(mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getStack())) {
                        this.swap(i, shovelSlot - 36);
                        this.timer.reset();
                        if (((Double)this.Delay.getValue()).longValue() > 0L) {
                            return;
                        }
                    }
                }
            }
            ++i;
        }
    }

    private boolean isBestShovel(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemSpade)) {
            return false;
        }
        float value = this.getToolEffect(stack);
        int i = 9;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (this.getToolEffect(is) > value && is.getItem() instanceof ItemSpade) {
                    return false;
                }
            }
            ++i;
        }
        return true;
    }

    public boolean isMoving() {
        return (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F);
    }

    public void drop(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    }

    private void getBestPickaxe(int slot) {
        int i = 9;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (this.isBestPickaxe(is) && pickaxeSlot != i && !this.isBestWeapon(is)) {
                    if (!mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getHasStack()) {
                        this.swap(i, pickaxeSlot - 36);
                        this.timer.reset();
                        if (((Double)this.Delay.getValue()).longValue() > 0L) {
                            return;
                        }
                    } else if (!this.isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack())) {
                        this.swap(i, pickaxeSlot - 36);
                        this.timer.reset();
                        if (((Double)this.Delay.getValue()).longValue() > 0L) {
                            return;
                        }
                    }
                }
            }
            ++i;
        }
    }

    private boolean isBestPickaxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemPickaxe)) {
            return false;
        }
        float value = this.getToolEffect(stack);
        int i = 9;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (this.getToolEffect(is) > value && is.getItem() instanceof ItemPickaxe) {
                    return false;
                }
            }
            ++i;
        }
        return true;
    }

    private float getToolEffect(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemTool)) {
            return 0.0f;
        }
        String name = item.getUnlocalizedName();
        ItemTool tool = (ItemTool)item;
        float value = 1.0f;
        if (item instanceof ItemPickaxe) {
            value = tool.getStrVsBlock(stack, Blocks.stone);
            if (name.toLowerCase().contains("gold")) {
                value -= 5.0f;
            }
        } else if (item instanceof ItemSpade) {
            value = tool.getStrVsBlock(stack, Blocks.dirt);
            if (name.toLowerCase().contains("gold")) {
                value -= 5.0f;
            }
        } else if (item instanceof ItemAxe) {
            value = tool.getStrVsBlock(stack, Blocks.log);
            if (name.toLowerCase().contains("gold")) {
                value -= 5.0f;
            }
        } else {
            return 1.0f;
        }
        value = (float)((double)value + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075);
        value = (float)((double)value + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100.0);
        return value;
    }

    public void getBestWeapon(int slot) {
        int i = 9;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (this.isBestWeapon(is) && this.getDamage(is) > 0.0f && (is.getItem() instanceof ItemSword || !((Boolean)this.Sword.getValue()).booleanValue())) {
                    this.swap(i, slot - 36);
                    this.timer.reset();
                    break;
                }
            }
            ++i;
        }
    }

    public void swap(int slot1, int hotbarSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
    }

    public boolean isBestWeapon(ItemStack stack) {
        float damage = this.getDamage(stack);
        int i = 9;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (this.getDamage(is) > damage && (is.getItem() instanceof ItemSword || !(Boolean) this.Sword.getValue())) {
                    return false;
                }
            }
            ++i;
        }
        return stack.getItem() instanceof ItemSword || (Boolean)this.Sword.getValue() == false;
    }

    private float getDamage(ItemStack stack) {
        float damage = 0.0f;
        Item item = stack.getItem();
        if (item instanceof ItemTool) {
            ItemTool tool = (ItemTool)item;
            damage += (float)tool.getMaxDamage();
        }
        if (item instanceof ItemSword) {
            ItemSword sword = (ItemSword)item;
            damage += sword.getDamageVsEntity();
        }
        return damage += (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01f;
    }

    boolean invContainsType(int type) {
        int i = 9;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (item instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor)item;
                    if (type == armor.armorType) {
                        return true;
                    }
                }
            }
            ++i;
        }
        return false;
    }

    public void getBestArmor() {
        int type = 1;
        while (type < 5) {
            block9: {
                block8: {
                    if (!mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) break block8;
                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                    if (AutoArmor.isBestArmor(is, type)) break block9;
                    this.drop(4 + type);
                }
                int i = 9;
                while (i < 45) {
                    if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                        if (AutoArmor.isBestArmor(is, type) && AutoArmor.getProtection(is) > 0.0f) {
                            this.shiftClick(i);
                            this.timer.reset();
                            if (((Double)this.Delay.getValue()).longValue() > 0L) {
                                return;
                            }
                        }
                    }
                    ++i;
                }
            }
            ++type;
        }
    }

    public void shiftClick(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
    }

    static enum EMode {
        Basic,
        OpenInv;
    }
}