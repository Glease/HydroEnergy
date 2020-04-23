package com.sinthoras.hydroenergy.network;

import com.sinthoras.hydroenergy.controller.HEController;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class HEWaterUpdate implements IMessage{
	
	public float[] waterLevel = new float[16];

	@Override
	public void fromBytes(ByteBuf buf) {
		for(int i=0;i<16;i++)
			waterLevel[i] = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		for(int i=0;i<16;i++)
			buf.writeFloat(waterLevel[i]);
	}
	
	
	public static class Handler implements IMessageHandler<HEWaterUpdate, IMessage> {

		@Override
		public IMessage onMessage(HEWaterUpdate message, MessageContext ctx) {
			HEController.onUpdateWaterLevels(message.waterLevel);
			return null;
		}
		
	}
}
