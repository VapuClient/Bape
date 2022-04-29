package mc.bape.vapu.irc.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import mc.bape.vapu.irc.IRCUser;
import mc.bape.vapu.irc.NetHandler;

public class Packet03SetUserList extends Packet {
	private IRCUser[] users = new IRCUser[0];
	public Packet03SetUserList() {}

	@Override
	public void readPacket(DataInputStream input) throws IOException {
		byte[] bitmap = new byte[input.readUnsignedByte()];
		ArrayList<IRCUser> users = new ArrayList<>(bitmap.length);
		input.readFully(bitmap);
		for (int i = 0; i < bitmap.length; i++) {
			int b = bitmap[i] & 0xFF;
			for(int j = 0; j < 8; j++) {
				if(input.available() < 1)
					break;
				boolean anonymous = (b & (int)Math.pow(2, 7 - j)) != 0;
				IRCUser user;
				if(anonymous) {
					user = new IRCUser(readShortString(input));
				} else {
					user = new IRCUser(readShortString(input), readShortString(input), readShortString(input));
				}
				users.add(user);
			}
		}
		this.users = users.toArray(this.users);
	}

	@Override
	public void writePacket(DataOutputStream output) throws IOException {}

	@Override
	public void handlePacket(NetHandler handler) {
		handler.handleSetUser(this);
	}

	@Override
	public int getPacketId() {
		return 0x03;
	}

	public IRCUser[] getUsers() {
		return users;
	}
	
	private String readShortString(DataInputStream in) throws IOException {
		byte[] buf = new byte[in.readUnsignedByte()];
		in.readFully(buf);
		return new String(buf, StandardCharsets.UTF_8);
	}

}
