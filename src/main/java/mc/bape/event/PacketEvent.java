package mc.bape.event;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PacketEvent extends Event {
	private final Packet<?> packet;
	private final Side side;

	
	public PacketEvent(Packet<?> packet, Side side) {
		this.packet = packet;
		this.side = side;
	}

	public Side getSide() {
		return side;
	}

	public Packet<?> getPacket() {
		return packet;
	}
	
	public enum Side {
		CLIENT,
		SERVER;
	}
}
