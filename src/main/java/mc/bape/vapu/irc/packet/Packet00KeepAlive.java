package mc.bape.vapu.irc.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import mc.bape.vapu.irc.NetHandler;

public class Packet00KeepAlive extends Packet {

	public Packet00KeepAlive() {}

	@Override
	public void readPacket(DataInputStream input) {
		
	}

	@Override
	public void writePacket(DataOutputStream output) {
		
	}

	@Override
	public void handlePacket(NetHandler handler) {
		handler.handleKeepAlive(this);
	}

	@Override
	public int getPacketId() {
		return 0x00;
	}

	
}
