package com.sinthoras.hydroenergy.server;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.config.HEConfig;
import com.sinthoras.hydroenergy.network.packet.HEPacketConfigUpdate;
import com.sinthoras.hydroenergy.network.packet.HEPacketWaterUpdate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class HEDam {
	
	private class Tags {
		public static final String waterLevel = "walv";
		public static final String drainState = "drai";
		public static final String isPlaced = "isPl";
		public static final String limitUp = "limU";
		public static final String limitDown = "limD";
		public static final String limitEast = "limE";
		public static final String limitWest = "limW";
		public static final String limitSouth = "limS";
		public static final String limitNorth = "limN";
		public static final String blocksPerY = "BlPY";
		public static final String blockX = "bloX";
		public static final String blockY = "bloY";
		public static final String blockZ = "bloZ";
		public static final String dimensionId = "dimI";
	}

	// NBT variables
	private float waterLevel;
	private boolean isPlaced;
	private HE.DamMode mode = HE.DamMode.DRAIN;
	public int limitUp;
	public int limitDown;
	public int limitEast;
	public int limitWest;
	public int limitSouth;
	public int limitNorth;
	private int[] blocksPerY = new int[256];
	private int blockX;
	private int blockY;
	private int blockZ;
	private int dimensionId;

	private int waterId;
	private long timestampLastUpdate = 0;


	public HEDam(int waterId) {
		this.waterId = waterId;
	}

	public void readFromNBTFull(NBTTagCompound compound) {
		waterLevel = compound.getFloat(Tags.waterLevel);
		boolean drainState = compound.getBoolean(Tags.drainState);
		isPlaced = compound.getBoolean(Tags.isPlaced);
		limitUp = compound.getInteger(Tags.limitUp);
		limitDown = compound.getInteger(Tags.limitDown);
		limitEast = compound.getInteger(Tags.limitEast);
		limitWest = compound.getInteger(Tags.limitWest);
		limitSouth = compound.getInteger(Tags.limitSouth);
		limitNorth = compound.getInteger(Tags.limitNorth);
		blocksPerY = compound.getIntArray(Tags.blocksPerY);
		blockX = compound.getInteger(Tags.blockX);
		blockY = compound.getInteger(Tags.blockY);
		blockZ = compound.getInteger(Tags.blockZ);
		dimensionId = compound.getInteger(Tags.dimensionId);

		if(!isPlaced || drainState) {
			mode = HE.DamMode.DRAIN;
		}
		else {
			mode = HE.DamMode.SPREAD;
		}
	}
	
	public void writeToNBTFull(NBTTagCompound compound)	{
		compound.setFloat(Tags.waterLevel, waterLevel);
		compound.setBoolean(Tags.drainState, mode == HE.DamMode.DRAIN);
		compound.setBoolean(Tags.isPlaced, isPlaced);
		compound.setInteger(Tags.limitUp, limitUp);
		compound.setInteger(Tags.limitDown, limitDown);
		compound.setInteger(Tags.limitEast, limitEast);
		compound.setInteger(Tags.limitWest, limitWest);
		compound.setInteger(Tags.limitSouth, limitSouth);
		compound.setInteger(Tags.limitNorth, limitNorth);
		compound.setIntArray(Tags.blocksPerY, blocksPerY);
		compound.setInteger(Tags.blockX, blockX);
		compound.setInteger(Tags.blockY, blockY);
		compound.setInteger(Tags.blockZ, blockZ);
		compound.setInteger(Tags.dimensionId, dimensionId);
	}

	public void setMode(HE.DamMode mode) {
		if(mode != this.mode) {
			this.mode = mode;
			sendConfigUpdate();
		}
	}

	public HE.DamMode getMode() {
		return mode;
	}
	
	public boolean setWaterLevel(float waterLevel) {
		this.waterLevel = waterLevel;
		long timestamp = System.currentTimeMillis();
		if(timestamp - timestampLastUpdate >= HEConfig.minimalWaterUpdateInterval) {
			timestampLastUpdate = timestamp;
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
		HEPacketConfigUpdate message = new HEPacketConfigUpdate(waterId, blockX, blockY, blockZ,
				mode, limitWest, limitDown, limitNorth,
				limitEast, limitUp, limitSouth);
		HE.network.sendToAll(message);
	}
	
	public void breakController() {
		isPlaced = false;
		sendConfigUpdate();
	}
	
	public void placeController(int dimensionId, int blockX, int blockY, int blockZ) {
		isPlaced = true;
		mode = HE.DamMode.DRAIN;
		limitEast = blockX + 20;
		limitWest = blockX - 20;
		limitUp = blockY+10;
		limitDown = blockY;
		limitSouth = blockZ + 20;
		limitNorth = blockZ - 20;
		waterLevel = blockY;
		blocksPerY = new int[256];
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		this.dimensionId = dimensionId;

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

	public int getBlockX() {
		return blockX;
	}

	public int getBlockY() {
		return blockY;
	}

	public int getBlockZ() {
		return blockZ;
	}

	public boolean onConfigRequest(HE.DamMode mode, int limitWest, int limitDown, int limitNorth, int limitEast, int limitUp, int limitSouth) {
		// Clap change requests to server limits before processing
		limitWest = blockX - HEUtil.clamp(blockX - limitWest, 0, HEConfig.maxWaterSpreadWest);
		limitDown = blockY - HEUtil.clamp(blockY - limitDown, 0, HEConfig.maxWaterSpreadDown);
		limitNorth = blockZ - HEUtil.clamp(blockZ - limitNorth, 0, HEConfig.maxWaterSpreadNorth);
		limitEast = blockX + HEUtil.clamp(limitEast - blockX, 0, HEConfig.maxWaterSpreadEast);
		limitUp = blockY + HEUtil.clamp(limitUp - blockY, 0, HEConfig.maxWaterSpreadUp);
		limitSouth = blockZ + HEUtil.clamp(limitSouth - blockZ, 0, HEConfig.maxWaterSpreadSouth);

		if(this.mode != mode || this.limitWest != limitWest || this.limitDown != limitDown || this.limitNorth != limitNorth
				|| this.limitEast != limitEast || this.limitUp != limitUp || this.limitSouth != limitSouth) {
			this.mode = mode;
			this.limitWest = limitWest;
			this.limitDown = limitDown;
			this.limitNorth = limitNorth;
			this.limitEast = limitEast;
			this.limitUp = limitUp;
			this.limitSouth = limitSouth;
			sendConfigUpdate();
			HEBlockQueue.enqueueBlock(MinecraftServer.getServer().worldServerForDimension(dimensionId).provider.worldObj, blockX, blockY, blockZ, waterId);
			return true;
		}
		return false;
	}

	public boolean canSpread() {
		return mode != HE.DamMode.DRAIN && isPlaced;
	}

	public String getShortDescription() {
		return "HEController @(" + blockX + ", " + blockY + ", " + blockZ + ")";
	}

	public long getEnergyCapacity() {
		long energyCapacity = 0;
		for(int blockY=this.blockY;blockY<HE.numChunksY*HE.chunkHeight;blockY++) {
			long heightCoefficient = blockY - this.blockY + 1;
			energyCapacity += heightCoefficient * blocksPerY[blockY] * HEConfig.energyPerWaterBlock;
		}
		return energyCapacity;
	}

	public long getWaterCapacity() {
		long energyCapacity = 0;
		for(int blockY=this.blockY;blockY<HE.numChunksY*HE.chunkHeight;blockY++) {
			energyCapacity += blocksPerY[blockY];
		}
		return energyCapacity;
	}

	public int getRainedOnBlocks() {
		for(int blockY=255;blockY>=0;blockY--) {
			if(blocksPerY[blockY] > 0) {
				return blocksPerY[blockY];
			}
		}
		return 0;
	}
}
