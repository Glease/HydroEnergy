package com.sinthoras.hydroenergy.server;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.network.HEPacketConfigUpdate;
import com.sinthoras.hydroenergy.network.HEPacketWaterUpdate;

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
	private boolean debugState = false;
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

	public void setDebugState(boolean debugState) {
		if(debugState != this.debugState) {
			this.debugState = debugState;
			sendConfigUpdate();
		}
	}

	public boolean getDebugState() {
		return debugState || !isPlaced;
	}
	
	public boolean setWaterLevel(float waterLevel) {
		this.waterLevel = waterLevel;
		long timestamp = System.currentTimeMillis();
		if(timestamp - timestampLastUpdate >= HE.minimalUpdateInterval * 1000) {
			sendWaterUpdate();
			return true;
		}
		return false;
	}

	public boolean setLimitWest(int limitWest) {
		if(limitWest != this.limitWest) {
			this.limitWest = limitWest;
			sendConfigUpdate();
			return true;
		}
		return false;
	}

	public int getLimitWest() {
		return limitWest;
	}

	public boolean setLimitDown(int limitDown) {
		if(limitDown != this.limitDown) {
			this.limitDown = limitDown;
			sendConfigUpdate();
			return true;
		}
		return false;
	}

	public int getLimitDown() {
		return limitDown;
	}

	public boolean setLimitNorth(int limitNorth) {
		if(limitNorth != this.limitNorth) {
			this.limitNorth = limitNorth;
			sendConfigUpdate();
			return true;
		}
		return false;
	}

	public int getLimitNorth() {
		return limitNorth;
	}

	public boolean setLimitEast(int limitEast) {
		if(limitEast != this.limitEast) {
			this.limitEast = limitEast;
			sendConfigUpdate();
			return true;
		}
		return false;
	}

	public int getLimitEast() {
		return limitEast;
	}

	public boolean setLimitUp(int limitUp) {
		if(limitUp != this.limitUp) {
			this.limitUp = limitUp;
			sendConfigUpdate();
			return true;
		}
		return false;
	}

	public int getLimitUp() {
		return limitUp;
	}

	public boolean setLimitSouth(int limitSouth) {
		if(limitSouth != this.limitSouth) {
			this.limitSouth = limitSouth;
			sendConfigUpdate();
			return true;
		}
		return false;
	}

	public int getLimitSouth() {
		return limitSouth;
	}
	
	public void sendWaterUpdate() {
		HEPacketWaterUpdate message = new HEPacketWaterUpdate(waterId, waterLevel);
		HE.network.sendToAll(message);
	}

	public void sendConfigUpdate() {
		HEPacketConfigUpdate message = new HEPacketConfigUpdate(waterId, !isPlaced || debugState, limitWest, limitDown, limitNorth, limitEast, limitUp, limitSouth);
		HE.network.sendToAll(message);
	}
	
	public void breakController() {
		isPlaced = false;
		sendConfigUpdate();
	}
	
	public void placeController(int blockX, int blockY, int blockZ) {
		removeWater = false;
		isPlaced = true;
		debugState = true;
		limitEast = blockX + 200;
		limitWest = blockX - 200;
		limitUp = blockY+32;
		limitDown = blockY;
		limitSouth = blockZ + 200;
		limitNorth = blockZ - 200;
		waterLevel = blockY;
		blocksPerY = new int[256];

		sendConfigUpdate();
	}

	public void onBlockRemoved(int blockY) {
		blocksPerY[blockY]--;
	}

	public void onBlockPlaced(int blockY) {
		blocksPerY[blockY]++;
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
