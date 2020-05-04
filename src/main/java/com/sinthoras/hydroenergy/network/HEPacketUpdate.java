package com.sinthoras.hydroenergy.network;

import com.sinthoras.hydroenergy.controller.HEDamsClient;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class HEPacketUpdate implements IMessage{
	
	public int id;
	public float renderedWaterLevel;
	
	public HEPacketUpdate(int id, float renderedWaterLevel)
	{
		this.id = id;
		this.renderedWaterLevel = renderedWaterLevel;
	}
	
	public HEPacketUpdate()
	{
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		this.renderedWaterLevel = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.id);
		buf.writeFloat(this.renderedWaterLevel);
	}
	
	
	public static class Handler implements IMessageHandler<HEPacketUpdate, IMessage> {

		@Override
		public IMessage onMessage(HEPacketUpdate message, MessageContext ctx) {
			HEDamsClient.instance.onClientUpdate(message.id, message.renderedWaterLevel);
			return null;
		}
		
	}
}
