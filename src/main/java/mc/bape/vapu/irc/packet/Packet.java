package mc.bape.vapu.irc.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mc.bape.vapu.irc.NetHandler;

public abstract class Packet {
	public abstract void readPacket(DataInputStream input) throws IOException;
	public abstract void writePacket(DataOutputStream output) throws IOException;
	public abstract void handlePacket(NetHandler handler);
	public abstract int getPacketId();
}
