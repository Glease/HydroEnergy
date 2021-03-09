package com.sinthoras.hydroenergy.network;

import com.sinthoras.hydroenergy.controller.HEDamsClient;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class HEPacketSynchronize implements IMessage {
	
	public float[] renderedWaterLevel;
	
	public HEPacketSynchronize() {
		
	}
	
	public HEPacketSynchronize(int size) {
		renderedWaterLevel = new float[size];
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int length = buf.readInt();
		renderedWaterLevel = new float[length];
		for(int i=0;i<renderedWaterLevel.length;i++) {
			renderedWaterLevel[i] = buf.readFloat();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(renderedWaterLevel.length);
		for(int i=0;i<renderedWaterLevel.length;i++) {
			buf.writeFloat(renderedWaterLevel[i]);
		}
	}

	public static class Handler implements IMessageHandler<HEPacketSynchronize, IMessage> {

		@Override
		public IMessage onMessage(HEPacketSynchronize message, MessageContext ctx) {
			HEDamsClient.onClientSynchronize(message.renderedWaterLevel);
			return null;
		}
	}
}
