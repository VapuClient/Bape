package mc.bape.vapu.irc.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import mc.bape.vapu.irc.NetHandler;

public class Packet02Chat extends Packet {
	private String message;
	public Packet02Chat() {}
	public Packet02Chat(String message) {
		if(message.getBytes(StandardCharsets.UTF_8).length >= 256) {
			throw new RuntimeException("String wide more than 1 byte!");
		}
		this.message = message;
	}

	@Override
	public void readPacket(DataInputStream input) throws IOException {
		byte[] buf = new byte[input.readUnsignedShort()];
		input.readFully(buf);
		message = new String(buf, StandardCharsets.UTF_8);
	}

	@Override
	public void writePacket(DataOutputStream output) throws IOException {
		byte[] buf = message.getBytes(StandardCharsets.UTF_8);
		output.writeByte(buf.length);
		output.write(buf);
	}

	@Override
	public void handlePacket(NetHandler handler) {
		handler.handleChat(this);
	}

	@Override
	public int getPacketId() {
		return 0x02;
	}

	public String getMessage() {return message;}

}
