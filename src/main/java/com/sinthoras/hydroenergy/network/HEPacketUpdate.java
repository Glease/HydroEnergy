package com.sinthoras.hydroenergy.network;

import com.sinthoras.hydroenergy.controller.HEDamsClient;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class HEPacketUpdate implements IMessage{
	
	public int id;
	public float renderedWaterLevel;
	public boolean renderDebug;
	
	public HEPacketUpdate(int id, float renderedWaterLevel, boolean renderDebug)
	{
		this.id = id;
		this.renderedWaterLevel = renderedWaterLevel;
		this.renderDebug = renderDebug;
	}
	
	public HEPacketUpdate()
	{
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		this.renderedWaterLevel = buf.readFloat();
		this.renderDebug = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.id);
		buf.writeFloat(this.renderedWaterLevel);
		buf.writeBoolean(this.renderDebug);
	}
	
	
	public static class Handler implements IMessageHandler<HEPacketUpdate, IMessage> {

		@Override
		public IMessage onMessage(HEPacketUpdate message, MessageContext ctx) {
			HEDamsClient.onClientUpdate(message.id, message.renderedWaterLevel, message.renderDebug);
			return null;
		}
		
	}
}
