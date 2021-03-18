package com.sinthoras.hydroenergy.network;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.client.HEClient;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class HEPacketSynchronize implements IMessage {

	public int[] blocksX = new int[HE.maxControllers];
	public int[] blocksY = new int[HE.maxControllers];
	public int[] blocksZ = new int[HE.maxControllers];
	public float[] waterLevels = new float[HE.maxControllers];
	public HE.DamMode[] modes = new HE.DamMode[HE.maxControllers];
	public int[] limitsWest = new int[HE.maxControllers];
	public int[] limitsDown = new int[HE.maxControllers];
	public int[] limitsNorth = new int[HE.maxControllers];
	public int[] limitsEast = new int[HE.maxControllers];
	public int[] limitsUp = new int[HE.maxControllers];
	public int[] limitsSouth = new int[HE.maxControllers];

	@Override
	public void fromBytes(ByteBuf buf) {
		int length = buf.readInt();
		blocksX = new int[length];
		blocksY = new int[length];
		blocksZ = new int[length];
		waterLevels = new float[length];
		modes = new HE.DamMode[length];
		limitsWest = new int[length];
		limitsDown = new int[length];
		limitsNorth = new int[length];
		limitsEast = new int[length];
		limitsUp = new int[length];
		limitsSouth = new int[length];
		for(int waterId=0;waterId<length;waterId++) {
			blocksX[waterId] = buf.readInt();
			blocksY[waterId] = buf.readInt();
			blocksZ[waterId] = buf.readInt();
			waterLevels[waterId] = buf.readFloat();
			modes[waterId] = HE.DamMode.getMode(buf.readInt());
			limitsWest[waterId] = buf.readInt();
			limitsDown[waterId] = buf.readInt();
			limitsNorth[waterId] = buf.readInt();
			limitsEast[waterId] = buf.readInt();
			limitsUp[waterId] = buf.readInt();
			limitsSouth[waterId] = buf.readInt();
		}
		HE.clippingOffset = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(HE.maxControllers);
		for(int waterId = 0; waterId< HE.maxControllers; waterId++) {
			buf.writeInt(blocksX[waterId]);
			buf.writeInt(blocksY[waterId]);
			buf.writeInt(blocksZ[waterId]);
			buf.writeFloat(waterLevels[waterId]);
			buf.writeInt(modes[waterId].getValue());
			buf.writeInt(limitsWest[waterId]);
			buf.writeInt(limitsDown[waterId]);
			buf.writeInt(limitsNorth[waterId]);
			buf.writeInt(limitsEast[waterId]);
			buf.writeInt(limitsUp[waterId]);
			buf.writeInt(limitsSouth[waterId]);
		}
		buf.writeFloat(HE.clippingOffset);
	}

	public static class Handler implements IMessageHandler<HEPacketSynchronize, IMessage> {

		@Override
		public IMessage onMessage(HEPacketSynchronize message, MessageContext ctx) {
			HEClient.onSynchronize(message.blocksX,
					message.blocksY,
					message.blocksZ,
					message.waterLevels,
					message.modes,
					message.limitsWest,
					message.limitsDown,
					message.limitsNorth,
					message.limitsEast,
					message.limitsUp,
					message.limitsSouth);
			return null;
		}
	}
}
