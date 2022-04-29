package mc.bape.command;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import mc.bape.utils.Helper;
import mc.bape.vapu.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;

public class CommandEsu implements ICommand {

	private Minecraft mc = Minecraft.getMinecraft();

	public Client BapeClient;

	public CommandEsu(Client vapuClient) {
		this.BapeClient = vapuClient;
	}
	
	
	@Override
	public String getCommandName() {
		return "esu";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/esu <QQ Number>";
	}


	public static String getJsonData(String jsonString, String targetString) {
		JsonParser parser = new JsonParser();
		JsonElement jsonNode = parser.parse(jsonString);
		if (jsonNode.isJsonObject()) {
			JsonObject jsonObject = jsonNode.getAsJsonObject();
			JsonElement jsonElementId = jsonObject.get(targetString);
			return jsonElementId.toString();
		}
		return null;
	}

	public static String deleteCharInString(String str, char delChar) {
		StringBuilder delStr = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) != delChar) {
				delStr.append(str.charAt(i));
			}
		}
		return delStr.toString();
	}

	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;

		try {
			URL realUrl = new URL(url);
			URLConnection connection = realUrl.openConnection();
			connection.setDoOutput(true);
			connection.setReadTimeout(99781);
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.connect();
			Map<String, List<String>> map = connection.getHeaderFields();
			for (String str : map.keySet()) ;
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result = String.valueOf(result) + line;
			}
		} catch (Exception exception) {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception ignored) {
			}
		} finally {
			try {
				if (in != null) in.close();
			} catch (Exception ignored) {
			}
		}
		return result;
	}


	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		try {
			if (args.length == 0) {
				Helper.sendMessage("Use /esu <QQ Number>");
			}
			String jsonData = sendGet("https://zy.xywlapi.cc/qqcx?qq=" + args[0], null);
			jsonData = deleteCharInString(jsonData, '\n');
			jsonData = deleteCharInString(jsonData, '\r');
			jsonData = deleteCharInString(jsonData, ' ');
			try {
				String weibo = getJsonData(jsonData, "wb");
				String diqu = getJsonData(jsonData, "phonediqu");
				String mobile = getJsonData(jsonData, "phone");
				String Check = "QQ:"+args[0]+", 微博:" + weibo + ", 电话:" + mobile + ", 电话地区："+diqu;
				Check = deleteCharInString(Check, '\"');
				Helper.sendMessage(Check);
			} catch (Exception e) {
				Helper.sendMessage("未查询到记录!");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender.getCommandSenderEntity() instanceof EntityPlayer;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		List<String> list = new ArrayList<String>();
		try {
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