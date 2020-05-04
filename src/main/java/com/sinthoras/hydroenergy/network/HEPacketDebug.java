package com.sinthoras.hydroenergy.network;

import com.sinthoras.hydroenergy.controller.HEDamsClient;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class HEPacketDebug implements IMessage{
	
	public boolean debug;
	
	public HEPacketDebug(boolean value)
	{
		debug = value;
	}
	
	public HEPacketDebug()
	{
		
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		debug = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(debug);
	}
	
	public static class Handler implements IMessageHandler<HEPacketDebug, IMessage> {

		@Override
		public IMessage onMessage(HEPacketDebug message, MessageContext ctx) {
			HEDamsClient.instance.onSetDebugMode(message.debug);
			return null;
		}
		
	}
}
