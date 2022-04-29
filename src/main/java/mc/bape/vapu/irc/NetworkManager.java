package mc.bape.vapu.irc;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mc.bape.vapu.irc.packet.*;

public class NetworkManager {
	
	private static final int MAX_PROCESS_COUNT = 100,
			 				 MAX_WRITE_COUNT = 100;
	
	private static final Map<Integer, Class<? extends Packet>> MAPPING = new HashMap<>();
	
	private final List<Packet> incoming = Collections.synchronizedList(new ArrayList<>()),
							   outgoing = Collections.synchronizedList(new ArrayList<>());
	
	private final Socket socket;
	private final IRCClient ircClient;
	private final DataInputStream input;
	private final DataOutputStream output;
	private boolean terminated;

	private NetHandler netHandler;
	public NetworkManager(Socket socket, IRCClient ircClient, NetHandler netHandler) throws IOException {
		this.socket = socket;
		this.ircClient = ircClient;
		this.input = new DataInputStream(socket.getInputStream());
		this.output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		this.netHandler = netHandler;
		netHandler.netMgr = this;
		new Thread("Network read thread") {
			public void run() {
				try {
					while(!terminated)
						readPacket();
				} catch(IOException e) {
					if(terminated)
						return;
					e.printStackTrace();
					terminate();
				}
			}
		}.start();
	}
	
	protected void sendPacket(Packet packet) {
		try {
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			packet.writePacket(new DataOutputStream(bOut));
			output.writeByte(packet.getPacketId());
			output.writeShort(bOut.size());
			output.write(bOut.toByteArray());
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
			terminate();
		}
	}
	
	public void readPacket() throws IOException {
		int packetid = input.readUnsignedByte();
		if(!MAPPING.containsKey(packetid))
			throw new IOException("Unknown packet id: "+packetid);
		int packetlength = input.readUnsignedShort();
		Packet packet;
		try {
			packet = MAPPING.get(packetid).getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
		byte[] buf = new byte[packetlength];
		input.readFully(buf);
		packet.readPacket(new DataInputStream(new ByteArrayInputStream(buf)));
		incoming.add(packet);
	}
	
	public boolean flush() {
		if(terminated)
			return false;
		for(int i=0;i<MAX_WRITE_COUNT&&!outgoing.isEmpty();i++)
			sendPacket(outgoing.remove(0));
		return !outgoing.isEmpty();
	}
	public boolean processReadPacket() {
		for(int i=0;i<MAX_PROCESS_COUNT&&!incoming.isEmpty();i++)
			incoming.remove(0).handlePacket(netHandler);
		return !incoming.isEmpty();
	}
	
	public boolean addToSendQueue(Packet packet) {
		if(terminated)
			return false;
		return outgoing.add(packet);
	}
	
	public boolean isTerminated() {return terminated;}
	public void terminate() {
		terminated = true;
		try {
			socket.shutdownInput();
			socket.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void shutdown(String message) {
		sendPacket(new PacketFFDisconnect(message));
		terminate();
	}
	public IRCClient getClient() {
		return ircClient;
	}

	private static void addMapping(int id, Class<? extends Packet> clazz) {
		if(!MAPPING.containsKey(id))
			MAPPING.put(id, clazz);
	}
	
	static {
		addMapping(0x00, Packet00KeepAlive.class);
		addMapping(0x01, Packet01Login.class);
		addMapping(0x02, Packet02Chat.class);
		addMapping(0x03, Packet03SetUserList.class);
		addMapping(0x04, Packet04ChangeUserList.class);
		addMapping(0xFF, PacketFFDisconnect.class);
	}
}
