package com.sinthoras.hydroenergy.network;

import com.sinthoras.hydroenergy.client.HEClient;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class HEPacketSynchronize implements IMessage {
	
	public float[] renderedWaterLevel;
	public boolean[] renderDebug;
	
	public HEPacketSynchronize() {
		
	}
	
	public HEPacketSynchronize(int size) {
		renderedWaterLevel = new float[size];
		renderDebug = new boolean[size];
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int length = buf.readInt();
		renderedWaterLevel = new float[length];
		for(int waterId=0;waterId<length;waterId++) {
			renderedWaterLevel[waterId] = buf.readFloat();
		}
		renderDebug = new boolean[length];
		for(int waterId=0;waterId<length;waterId++) {
			renderDebug[waterId] = buf.readBoolean();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(renderedWaterLevel.length);
		for(int waterId=0;waterId<renderedWaterLevel.length;waterId++) {
			buf.writeFloat(renderedWaterLevel[waterId]);
		}
		for(int waterId=0;waterId<renderDebug.length;waterId++) {
			buf.writeBoolean(renderDebug[waterId]);
		}
	}

	public static class Handler implements IMessageHandler<HEPacketSynchronize, IMessage> {

		@Override
		public IMessage onMessage(HEPacketSynchronize message, MessageContext ctx) {
			HEClient.onClientSynchronize(message.renderedWaterLevel);
			return null;
		}
	}
}
