package mc.bape.vapu.irc.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import mc.bape.vapu.irc.NetHandler;

public class PacketFFDisconnect extends Packet {
	private String reason;
	public PacketFFDisconnect() {}
	public PacketFFDisconnect(String reason) {
		this.reason = reason;
	}

	@Override
	public void readPacket(DataInputStream input) throws IOException {
		byte[] buf = new byte[input.available()];
		input.readFully(buf);
		reason = new String(buf, StandardCharsets.UTF_8);
	}

	@Override
	public void writePacket(DataOutputStream output) throws IOException {
		if(reason != null)
			output.write(reason.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public void handlePacket(NetHandler handler) {
		handler.handleDisconnect(this);
	}

	@Override
	public int getPacketId() {
		return 0xFF;
	}

	public String getReason() {
		return reason;
	}

}
