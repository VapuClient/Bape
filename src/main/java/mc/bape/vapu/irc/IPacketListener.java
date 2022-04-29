package mc.bape.vapu.irc;

import mc.bape.vapu.irc.packet.Packet;

public interface IPacketListener {
	void handlePacket(Packet packet);
}
