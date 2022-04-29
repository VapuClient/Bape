package mc.bape.vapu.irc.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import mc.bape.vapu.irc.IRCUser;
import mc.bape.vapu.irc.NetHandler;

public class Packet04ChangeUserList extends Packet {

	private boolean add;
	private IRCUser user;
	
	public Packet04ChangeUserList() {}

	@Override
	public void readPacket(DataInputStream input) throws IOException {
		int flag = input.readUnsignedByte();
		this.add = (flag & 0x80) == 0;
		boolean anonymous = (flag & 0x01) != 0;
		if(anonymous) {
			user = new IRCUser(readShortString(input));
		} else {
			user = new IRCUser(readShortString(input), readShortString(input), readShortString(input));
		}
	}

	@Override
	public void writePacket(DataOutputStream output) throws IOException {}

	@Override
	public void handlePacket(NetHandler handler) {
		handler.handleUserListChanged(this);
	}

	@Override
	public int getPacketId() {
		return 0x04;
	}
	
	private String readShortString(DataInputStream in) throws IOException {
		byte[] buf = new byte[in.readUnsignedByte()];
		in.readFully(buf);
		return new String(buf, StandardCharsets.UTF_8);
	}

	public boolean isAdd() {
		return add;
	}

	public IRCUser getUser() {
		return user;
	}

}
