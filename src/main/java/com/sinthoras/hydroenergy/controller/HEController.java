package com.sinthoras.hydroenergy.controller;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.network.HEPacketUpdate;

import net.minecraft.nbt.NBTTagCompound;

public class HEController {
	
	public class Tags {
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
	private double waterLevel;
	private boolean renderDebug;
	private float renderedWaterLevel;
	private boolean placed;
	private int yLimitUp;
	private int yLimitDown;
	private boolean keepWater;
	private boolean stopSpreading;

	
	public void readFromNBTFull(NBTTagCompound compound) {
		id = compound.getInteger(Tags.id);
		waterLevel = compound.getDouble(Tags.waterLevel);
		renderDebug = false;
		renderedWaterLevel = compound.getFloat(Tags.renderedWaterLevel);
		placed = compound.getBoolean(Tags.placed);
		yLimitUp = 72;//compound.getInteger(Tags.yLimitUp);
		yLimitDown = compound.getInteger(Tags.yLimitDown);
		keepWater = compound.getBoolean(Tags.keepWater);
		stopSpreading = compound.getBoolean(Tags.stopSpreading);
	}
	
	public void writeToNBTFull(NBTTagCompound compound)	{
		compound.setInteger(Tags.id, id);
		compound.setDouble(Tags.waterLevel, waterLevel);
		compound.setFloat(Tags.renderedWaterLevel, renderedWaterLevel);
		compound.setBoolean(Tags.placed, placed);
		compound.setInteger(Tags.yLimitUp, yLimitUp);
		compound.setInteger(Tags.yLimitDown, yLimitDown);
		compound.setBoolean(Tags.keepWater, keepWater);
		compound.setBoolean(Tags.stopSpreading, stopSpreading);
	}
	
	public void updateWaterLevel(double level) {
		if(Math.abs(level - renderedWaterLevel) >= HE.waterRenderResolution) {
			renderedWaterLevel = (float)level;
			sendUpdate();
		}
		waterLevel = level;
	}

	public void updateDebugState(boolean debugState) {
		if(debugState != renderDebug) {
			renderDebug = debugState;
			sendUpdate();
		}
	}
	
	public void sendUpdate() {
		HEPacketUpdate message = new HEPacketUpdate(id, renderedWaterLevel, renderDebug);
		HE.network.sendToAll(message);
	}
	
	public void breakController() {
		placed = false;
		waterLevel = 0.0;
		renderedWaterLevel = -1.0f;
		sendUpdate();
	}
	
	public void placeController(int blockY) {
		placed = true;
		yLimitDown = blockY;
		yLimitUp = blockY+1;
		
		waterLevel = blockY + 0.5;
		renderedWaterLevel = blockY + 0.5f;
		sendUpdate();
	}

	public int getWaterLimitUp() {
		return yLimitUp;
	}
	
	public int getWaterLimitDown() {
		return yLimitDown;
	}
	
	public double getWaterLevel() {
		return waterLevel;
	}
	
	public float getRenderedWaterLevel() {
		if(renderDebug) {
			return 0.0f;
		}
		else {
			return renderedWaterLevel;
		}
	}
	
	public boolean isPlaced() {
		return placed;
	}
}
