package mc.bape.vapu.irc;

import mc.bape.vapu.irc.packet.Packet;
import mc.bape.vapu.irc.packet.Packet00KeepAlive;
import mc.bape.vapu.irc.packet.Packet01Login;
import mc.bape.vapu.irc.packet.Packet02Chat;
import mc.bape.vapu.irc.packet.Packet03SetUserList;
import mc.bape.vapu.irc.packet.Packet04ChangeUserList;
import mc.bape.vapu.irc.packet.PacketFFDisconnect;

public class NetHandler {
	protected NetworkManager netMgr;
	
	protected void unhandlePacket(Packet packet) {
		
	}
	public void handleKeepAlive(Packet00KeepAlive packet) {
		netMgr.addToSendQueue(packet);
	}
	public void handleLogin(Packet01Login packet) {
		
	}
	public void handleChat(Packet02Chat packet) {
		unhandlePacket(packet);
	}
	public void handleSetUser(Packet03SetUserList packet) {
		netMgr.getClient().setOnlineUsers(packet.getUsers());
	}
	public void handleUserListChanged(Packet04ChangeUserList packet) {
		IRCClient client = netMgr.getClient();
		if(packet.isAdd())
			client.addOnlineUser(packet.getUser());
		else
			client.removeOnlineUser(packet.getUser());
	}
	public void handleDisconnect(PacketFFDisconnect packet) {
		unhandlePacket(packet);
		netMgr.terminate();
	}
	public void addToSendQueue(Packet packet) {
		netMgr.addToSendQueue(packet);
	}

}
