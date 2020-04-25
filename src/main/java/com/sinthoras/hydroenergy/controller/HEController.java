package com.sinthoras.hydroenergy.controller;

import net.minecraft.nbt.NBTTagCompound;

public class HEController {
	
	public class tags
	{
		public static final String waterLevel = "wlev";
		public static final String xCoord = "x";
		public static final String yCoord = "y";
		public static final String zCoord = "z";
		public static final String placed = "plac";
	}

	// NBT variables
	private float waterLevel;
	private int xCoord;
	private int yCoord;
	private int zCoord;
	private boolean placed = false;
	
	// Flag object to be synchronized to clients
	private boolean requiresUpdate = false;

	
	public static int max_controller = 16;
	
	public void readFromNBTFull(NBTTagCompound compound)
	{
		waterLevel = compound.getFloat(tags.waterLevel);
		xCoord = compound.getInteger(tags.xCoord);
		yCoord = compound.getInteger(tags.yCoord);
		zCoord = compound.getInteger(tags.zCoord);
		placed = compound.getBoolean(tags.placed);
	}
	
	public void writeToNBTFull(NBTTagCompound compound)
	{
		compound.setFloat(tags.waterLevel, waterLevel);
		compound.setInteger(tags.xCoord, xCoord);
		compound.setInteger(tags.yCoord, yCoord);
		compound.setInteger(tags.zCoord, zCoord);
		compound.setBoolean(tags.placed, placed);
	}
	
	// Trimmed version for what the client requires for rendering
	public void readFromNBTNetwork(NBTTagCompound compound)
	{
		waterLevel = compound.getFloat(tags.waterLevel);
		placed = compound.getBoolean(tags.placed);
	}
	
	// Trimmed version for what the client requires for rendering
	public void writeToNBTNetwork(NBTTagCompound compound)
	{
		compound.setFloat(tags.waterLevel, waterLevel);
		compound.setBoolean(tags.placed, placed);
	}
	
	public boolean transmitUpdate()
	{
		return requiresUpdate;
	}
	
	public void updateWaterLevel(float level)
	{
		if(Math.abs(waterLevel - level) >= 1.0f/32.0f)  // TODO: Send constant to config
		{
			requiresUpdate = true;
		}
		waterLevel = level;
	}
	
	public float getWaterLevel()
	{
		return waterLevel;
	}
	
	public boolean isPlaced()
	{
		return placed;
	}
	
	public void onBreakController()
	{
		placed = false;
		requiresUpdate = true;
	}
	
	public void updateSent()
	{
		requiresUpdate = false;
	}
	
	public void placeController()
	{
		placed = true;
		requiresUpdate = true;
	}
}
