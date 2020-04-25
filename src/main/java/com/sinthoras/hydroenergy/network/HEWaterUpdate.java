package com.sinthoras.hydroenergy.network;

import com.sinthoras.hydroenergy.controller.HEController;
import com.sinthoras.hydroenergy.controller.HEDams;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class HEWaterUpdate implements IMessage{
	
	public NBTTagCompound compound;
	
	public HEWaterUpdate()
	{
		compound = new NBTTagCompound();
	}
	
	public HEWaterUpdate(NBTTagCompound compound)
	{
		this.compound = compound;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int num_controller = buf.readInt();
		compound.setInteger(HEDams.tags.max_controller, num_controller);
		for(int i=0;i<num_controller;i++)
		{
			NBTTagCompound subcompound = new NBTTagCompound();
			float waterLevel = buf.readFloat();
			subcompound.setFloat(HEController.tags.waterLevel, waterLevel);
			boolean placed = buf.readBoolean();
			subcompound.setBoolean(HEController.tags.placed, placed);
			compound.setTag(HEDams.tags.instance + i, subcompound);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		final int num_controller = compound.getInteger(HEDams.tags.max_controller);
		buf.writeInt(num_controller);
		for(int i=0;i<num_controller;i++)
		{
			NBTTagCompound subcompound = compound.getCompoundTag(HEDams.tags.instance + i);
			buf.writeFloat(subcompound.getFloat(HEController.tags.waterLevel));
			buf.writeBoolean(subcompound.getBoolean(HEController.tags.placed));
		}
	}
	
	
	public static class Handler implements IMessageHandler<HEWaterUpdate, IMessage> {

		@Override
		public IMessage onMessage(HEWaterUpdate message, MessageContext ctx) {
			HEDams.instance.onClientUpdate(message.compound);
			return null;
		}
		
	}
}
