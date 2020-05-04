package com.sinthoras.hydroenergy.controller;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.network.HEPacketUpdate;

import net.minecraft.nbt.NBTTagCompound;

public class HEController {
	
	public class Tags
	{
		public static final String id = "id";
		public static final String waterLevel = "walv";
		public static final String renderedWaterLevel = "rwlv";
		public static final String placed = "plac";
		public static final String yLimitUp = "yliu";
		public static final String yLimitDown = "ylid";
		public static final String keepWater = "keep";
		public static final String energyStored = "engy";
		public static final String stopSpreading = "stps";
	}

	// NBT variables
	private int id;
	private float waterLevel;
	private float renderedWaterLevel;
	private boolean placed;
	private int yLimitUp;
	private int yLimitDown;
	private boolean keepWater;
	private boolean stopSpreading;

	
	public void readFromNBTFull(NBTTagCompound compound)
	{
		id = compound.getInteger(Tags.id);
		waterLevel = compound.getFloat(Tags.waterLevel);
		renderedWaterLevel = compound.getFloat(Tags.renderedWaterLevel);
		placed = compound.getBoolean(Tags.placed);
		yLimitUp = compound.getInteger(Tags.yLimitUp);
		yLimitDown = compound.getInteger(Tags.yLimitDown);
		keepWater = compound.getBoolean(Tags.keepWater);
		stopSpreading = compound.getBoolean(Tags.stopSpreading);
	}
	
	public void writeToNBTFull(NBTTagCompound compound)
	{
		compound.setInteger(Tags.id, id);
		compound.setFloat(Tags.waterLevel, waterLevel);
		compound.setFloat(Tags.renderedWaterLevel, renderedWaterLevel);
		compound.setBoolean(Tags.placed, placed);
		compound.setInteger(Tags.yLimitUp, yLimitUp);
		compound.setInteger(Tags.yLimitDown, yLimitDown);
		compound.setBoolean(Tags.keepWater, keepWater);
		compound.setBoolean(Tags.stopSpreading, stopSpreading);
	}
	
	public void updateWaterLevel(float level)
	{
		float waterLevelToRender = Math.round(level * HE.waterRenderResolution) / HE.waterRenderResolution;
		if(Math.abs(waterLevelToRender - renderedWaterLevel) >= 1.0f / waterLevelToRender / 1000.0f)
		{
			renderedWaterLevel = waterLevelToRender;
			sendUpdate();
		}
		waterLevel = level;
	}
	
	public void sendUpdate()
	{
		HEPacketUpdate message = new HEPacketUpdate(id, renderedWaterLevel);
		HE.network.sendToAll(message);
	}
	
	public void breakController()
	{
		placed = false;
		waterLevel = 0.0f;
		renderedWaterLevel = 0.0f;
		sendUpdate();
	}
	
	public void placeController(int y)
	{
		placed = true;
		yLimitDown = y;
		yLimitUp = y;
		
		// y or y-1?
		waterLevel = y;
		renderedWaterLevel = y;
		sendUpdate();
	}

	public int getWaterLimitUp()
	{
		return yLimitUp;
	}
	
	public int getWaterLimitDown()
	{
		return yLimitDown;
	}
	
	public float getWaterLevel()
	{
		return waterLevel;
	}
	
	public float getRenderedWaterLevel()
	{
		return renderedWaterLevel;
	}
	
	public boolean isPlaced()
	{
		return placed;
	}
}
