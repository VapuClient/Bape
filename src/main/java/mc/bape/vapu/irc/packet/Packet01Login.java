package mc.bape.vapu.irc.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import mc.bape.vapu.irc.NetHandler;

public class Packet01Login extends Packet {

	private boolean anonymous;
	private String username, password, ingameName;
	public Packet01Login() {}
	public Packet01Login(String ingameName) {
		this.anonymous = true;
		this.ingameName = ingameName;
	}
	public Packet01Login(String ingameName, String username, String password) {
		this.anonymous = false;
		this.ingameName = ingameName;
		this.username = username;
		this.password = password;
	}

	@Override
	public void readPacket(DataInputStream input) {} // TODO

	@Override
	public void writePacket(DataOutputStream output) throws IOException {
		output.writeInt(0x00000002);
		output.writeBoolean(anonymous);
		writeShortString(ingameName, output);
		if(!anonymous) {
			writeShortString(username, output);
			writeShortString(password, output);
		}
	}

	@Override
	public void handlePacket(NetHandler handler) {
		handler.handleLogin(this);
	}

	@Override
	public int getPacketId() {
		return 0x01;
	}
	private void writeShortString(String str, DataOutputStream output) throws IOException {
		byte[] buf = str.getBytes(StandardCharsets.UTF_8);
		if(buf.length >= 256) {
			throw new RuntimeException("String wide more than 1 byte!");
		}
		output.writeByte(buf.length);
		output.write(buf);
	}
}
