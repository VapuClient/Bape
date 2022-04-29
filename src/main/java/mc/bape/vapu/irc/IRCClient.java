package mc.bape.vapu.irc;

import mc.bape.vapu.irc.packet.Packet;
import mc.bape.vapu.irc.packet.Packet01Login;
import mc.bape.vapu.irc.packet.Packet02Chat;
import mc.bape.vapu.irc.packet.PacketFFDisconnect;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class IRCClient implements Runnable {
	private final SocketAddress address;
	private Socket socket;
	private NetworkManager networkManager;
	private final ArrayList<IPacketListener> listener = new ArrayList<>();
	private final ArrayList<IRCUser> onlineUsers = new ArrayList<>();
	public IRCClient(SocketAddress address) {
		this.address = address;
	}
	
	public void connect() throws IOException {
		this.socket = new Socket();
		socket.connect(address);
		this.networkManager = new NetworkManager(socket, this, new NetHandler() {
			protected void unhandlePacket(Packet packet) {
				for (IPacketListener listener : listener) {
					listener.handlePacket(packet);
				}
			}
		});
		new Thread(this, "IRC Thread").start();
	}
	public void disconnect() {
		this.networkManager.terminate();
	}
	
	public boolean isConnected() {
		return networkManager != null && !networkManager.isTerminated();
	}
	
	public NetworkManager getNetworkManager() {
		return networkManager;
	}
	public boolean addListener(IPacketListener l) {
		if(listener.contains(l))
			return false;
		return listener.add(l);
	}
	public boolean removeListener(IPacketListener l) {
		return listener.remove(l);
	}
	@Override
	public void run() {
		while(!networkManager.isTerminated()) {
			runTick();
			try {
				Thread.sleep(50L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private void runTick() {
		networkManager.processReadPacket();
		networkManager.flush();
	}
	
	public static void main(String[] args) throws Throwable {
		IRCClient client = new IRCClient(new InetSocketAddress("127.0.0.1", 54188));
		client.connect();
		client.getNetworkManager().addToSendQueue(new Packet01Login("TestUser1"));
		client.addListener(new IPacketListener() {

			@Override
			public void handlePacket(Packet packet) {
				if(packet instanceof Packet02Chat) {
					System.out.println(((Packet02Chat) packet).getMessage());
				}
				if(packet instanceof PacketFFDisconnect) {
					System.out.println("§eDisconnected: "+((PacketFFDisconnect) packet).getReason());
				}
			}
			
		});
	}

	public void setOnlineUsers(IRCUser[] users) {
		onlineUsers.clear();
		onlineUsers.ensureCapacity(users.length);
		for (int i = 0; i < users.length; i++) {
			onlineUsers.add(users[i]);
		}
	}
	
	public IRCUser[] getOnlineUsers() {
		return onlineUsers.toArray(new IRCUser[0]);
	}
	
	public boolean addOnlineUser(IRCUser user) {
		return onlineUsers.add(user);
	}
	public boolean removeOnlineUser(IRCUser user) {
		return onlineUsers.remove(user);
	}
}
