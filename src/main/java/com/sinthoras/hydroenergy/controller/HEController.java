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
	private float renderedWaterLevel;
	private int xCoord;
	private int yCoord;
	private int zCoord;
	private boolean placed = false;
	
	// Flag object to be synchronized to clients
	private boolean requiresNetworkUpdate = false;
	private boolean requiresRenderUpdate = false;

	
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
		updateWaterLevel(compound.getFloat(tags.waterLevel));
		if(!placed && compound.getBoolean(tags.placed))
		{
			requiresNetworkUpdate = true;
			requiresRenderUpdate = true;
		}
		placed = compound.getBoolean(tags.placed);
	}
	
	// Trimmed version for what the client requires for rendering
	public void writeToNBTNetwork(NBTTagCompound compound)
	{
		compound.setFloat(tags.waterLevel, waterLevel);
		compound.setBoolean(tags.placed, placed);
	}
	
	// server: Send update to client
	public boolean transmitUpdate()
	{
		return requiresNetworkUpdate;
	}
	
	// client: Update display
	public boolean renderUpdate()
	{
		return requiresRenderUpdate;
	}
	
	public void updateWaterLevel(float level)
	{
		final float stepResolution = 16.0f;  // Config?
		float waterLevelToRender = Math.round(level * stepResolution) / stepResolution;
		if(Math.abs(waterLevelToRender - renderedWaterLevel) >= 1.0f / waterLevelToRender / 1000.0f)
		{
			requiresNetworkUpdate = true;
			requiresRenderUpdate = true;
		}
		waterLevel = level;
		renderedWaterLevel = waterLevelToRender;
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
	
	public void onBreakController()
	{
		placed = false;
		requiresNetworkUpdate = true;
		requiresRenderUpdate = true;
	}
	
	public void updateSent()
	{
		requiresNetworkUpdate = false;
	}
	
	public void updateRendered()
	{
		requiresRenderUpdate = false;
	}
	
	public void placeController()
	{
		placed = true;
		requiresNetworkUpdate = true;
		requiresRenderUpdate = true;
	}
}
