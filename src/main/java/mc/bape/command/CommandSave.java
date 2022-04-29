package mc.bape.command;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldSettings;
import mc.bape.manager.ConfigManager;
import mc.bape.manager.ModuleManager;
import mc.bape.module.Module;
import mc.bape.utils.Helper;
import mc.bape.values.Value;
import mc.bape.vapu.Client;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.*;

import static mc.bape.manager.ConfigManager.Configs;

public class CommandSave implements ICommand {

	private Minecraft mc = Minecraft.getMinecraft();

	public Client BapeClient =null;

	public CommandSave(Client vapuClient) {
		this.BapeClient = vapuClient;
	}
	
	
	@Override
	public String getCommandName() {
		return "save";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/save configname";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (!ModuleManager.getModule("NoCommand").getState()) {
			try {
				Configs.clear();
				File file = new File(System.getenv("APPDATA"), Client.CLIENT_NAME);
				if (file.isDirectory()) {
					File[] files = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						if (files[i].isDirectory() && !Configs.contains(files[i].getName())) {
							Configs.add(files[i].getName());
						}
					}
				}
				if(Configs.size() >= 6){
					Helper.sendMessage("Config is full.");
					return;
				}
				String names = args[0];
				ConfigManager.ConfigFile = "/" + names + "/";
				Client.CLIENT_CONFIG = names;
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
			}
			catch(Exception e) {
			}
		}
		else {
			Helper.sendMessage("Use /config configname");
		}
	}
	

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender.getCommandSenderEntity() instanceof EntityPlayer;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		//this.exampleMod.log(MinecraftServer.getServer().getAllUsernames().toString());
//		for(String s : MinecraftServer.getServer().getAllUsernames()) {
//			this.exampleMod.log(s);
//		}
		//return Collections.<String>emptyList();
		List<String> list = new ArrayList<String>();
		try {
			//this.exampleMod.log(MinecraftServer.getServer().getConfigurationManager().toString());
			
			Ordering<NetworkPlayerInfo> field_175252_a = Ordering.from(new Comparator<NetworkPlayerInfo>()
	        {
	            private void PlayerComparator()
	            {
	            }

	            public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_)
	            {
	                ScorePlayerTeam scoreplayerteam = p_compare_1_.getPlayerTeam();
	                ScorePlayerTeam scoreplayerteam1 = p_compare_2_.getPlayerTeam();
	                return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != WorldSettings.GameType.SPECTATOR, p_compare_2_.getGameType() != WorldSettings.GameType.SPECTATOR).compare(scoreplayerteam != null ? scoreplayerteam.getRegisteredName() : "", scoreplayerteam1 != null ? scoreplayerteam1.getRegisteredName() : "").compare(p_compare_1_.getGameProfile().getName(), p_compare_2_.getGameProfile().getName()).result();
	            }
	        });
			String last_s = "";
			for (NetworkPlayerInfo networkPlayerInfoIn : field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap())) {
				String s = networkPlayerInfoIn.getDisplayName() != null && false ? networkPlayerInfoIn.getDisplayName().getFormattedText() : networkPlayerInfoIn.getGameProfile().getName();
				if (!s.equals(last_s))
					list.add(s);
				last_s = s;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
        String[] astring = new String[list.size()];

        for (int i = 0; i < list.size(); ++i)
        {
            astring[i] = list.get(i);
        }

		return CommandBase.getListOfStringsMatchingLastWord(args, astring);
		//return MinecraftServer.getTabCompletions();
	}

	@Override
	public int compareTo(ICommand arg0) {
		// TODO Auto-generated method stub
		return this.getCommandName().compareTo(arg0.getCommandName());
	}

	@Override
	public List<String> getCommandAliases() {
		// TODO Auto-generated method stub
		return Collections.<String>emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}
}