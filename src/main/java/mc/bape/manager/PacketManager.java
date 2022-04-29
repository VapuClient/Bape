package mc.bape.manager;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import mc.bape.utils.ReflectionUtil;
import mc.bape.event.PacketEvent;
import mc.bape.event.PacketEvent.Side;

public class PacketManager {
	public static final PacketManager INSTANCE = new PacketManager();
	private PacketManager() {}
	
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
	}
	
	public void uninject() {
		MinecraftForge.EVENT_BUS.unregister(this);
		Minecraft mc = Minecraft.getMinecraft();
		NetworkManager netMgr = (NetworkManager) ReflectionUtil.getFieldValue(mc, "myNetworkManager", "field_71453_ak");
		if(netMgr != null)
			netMgr.channel().pipeline().remove("Bape");
	}
	
	@SubscribeEvent
	public void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {
		Minecraft mc = Minecraft.getMinecraft();
		NetworkManager netMgr = null;
		if(e == null)
			netMgr = (NetworkManager) ReflectionUtil.getFieldValue(mc, "myNetworkManager", "field_71453_ak");
		else
			netMgr = e.manager;
		if(netMgr != null)
			netMgr.channel().pipeline().addBefore("packet_handler", "Bape", new PacketListener());
	}
}
class PacketListener extends ChannelDuplexHandler {
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!MinecraftForge.EVENT_BUS.post(new PacketEvent((Packet<?>) msg, Side.SERVER)))
        	super.channelRead(ctx, msg);
    }
	@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(!MinecraftForge.EVENT_BUS.post(new PacketEvent((Packet<?>) msg, Side.CLIENT)))
			super.write(ctx, msg, promise);
    }
}
