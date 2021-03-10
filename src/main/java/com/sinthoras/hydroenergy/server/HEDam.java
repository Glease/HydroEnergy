package com.sinthoras.hydroenergy.server;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.network.HEPacketUpdate;

import net.minecraft.nbt.NBTTagCompound;

public class HEDam {
	
	public class Tags {
		public static final String waterLevel = "walv";
		public static final String removeWater = "remW";
		public static final String isPlaced = "isPl";
		public static final String limitUp = "limU";
		public static final String limitDown = "limD";
		public static final String limitEast = "limE";
		public static final String limitWest = "limW";
		public static final String limitSouth = "limS";
		public static final String limitNorth = "limN";
		public static final String blocksPerY = "BlPY";
	}

	// NBT variables
	private float waterLevel;
	public boolean removeWater;
	private boolean isPlaced;
	public int limitUp;
	public int limitDown;
	public int limitEast;
	public int limitWest;
	public int limitSouth;
	public int limitNorth;
	private int[] blocksPerY = new int[256];

	private int waterId;
	private boolean isDebugMode = false;
	private long timestampLastUpdate = 0;


	public HEDam(int waterId) {
		this.waterId = waterId;
	}

	public void readFromNBTFull(NBTTagCompound compound) {
		waterLevel = compound.getFloat(Tags.waterLevel);
		removeWater = compound.getBoolean(Tags.removeWater);
		isPlaced = compound.getBoolean(Tags.isPlaced);
		limitUp = compound.getInteger(Tags.limitUp);
		limitDown = compound.getInteger(Tags.limitDown);
		limitEast = compound.getInteger(Tags.limitEast);
		limitWest = compound.getInteger(Tags.limitWest);
		limitSouth = compound.getInteger(Tags.limitSouth);
		limitNorth = compound.getInteger(Tags.limitNorth);
		blocksPerY = compound.getIntArray(Tags.blocksPerY);
	}
	
	public void writeToNBTFull(NBTTagCompound compound)	{
		compound.setFloat(Tags.waterLevel, waterLevel);
		compound.setBoolean(Tags.removeWater, removeWater);
		compound.setBoolean(Tags.isPlaced, isPlaced);
		compound.setInteger(Tags.limitUp, limitUp);
		compound.setInteger(Tags.limitDown, limitDown);
		compound.setInteger(Tags.limitEast, limitEast);
		compound.setInteger(Tags.limitWest, limitWest);
		compound.setInteger(Tags.limitSouth, limitSouth);
		compound.setInteger(Tags.limitNorth, limitNorth);
		compound.setIntArray(Tags.blocksPerY, blocksPerY);
	}

	public void setDebugMode(boolean isDebug) {
		if(isDebug != isDebugMode) {
			isDebugMode = isDebug;
			sendUpdate();
		}
	}

	public boolean getDebugMode() {
		return isDebugMode || !isPlaced;
	}
	
	public boolean setWaterLevel(float level) {
		waterLevel = level;
		long timestamp = System.currentTimeMillis();
		if(timestamp - timestampLastUpdate >= HE.minimalUpdateInterval * 1000) {
			sendUpdate();
			return true;
		}
		return false;
	}
	
	public void sendUpdate() {
		HEPacketUpdate message = new HEPacketUpdate(waterId, waterLevel, !isPlaced || isDebugMode);
		HE.network.sendToAll(message);
	}
	
	public void breakController() {
		isPlaced = false;
		sendUpdate();
	}
	
	public void placeController(int blockX, int blockY, int blockZ) {
		removeWater = false;
		isPlaced = true;
		isDebugMode = true;
		limitEast = blockX + 200;
		limitWest = blockX - 200;
		limitUp = blockY+32;
		limitDown = blockY;
		limitSouth = blockZ + 200;
		limitNorth = blockZ - 200;
		waterLevel = blockY;
		blocksPerY = new int[256];

		sendUpdate();
	}
	
	public float getWaterLevel() {
		return waterLevel;
	}
	
	public boolean isPlaced() {
		return isPlaced;
	}

	public int getBlocksOnY(int blockY) {
		return blocksPerY[blockY];
	}

	public void onWaterPlaced(int blockY) {
		blocksPerY[blockY]++;
	}
}
