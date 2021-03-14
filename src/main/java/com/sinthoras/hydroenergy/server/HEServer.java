package com.sinthoras.hydroenergy.server;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.network.HEPacketSynchronize;

import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class HEServer extends WorldSavedData {
	
	private HEDam[] dams;

	public HEServer() {
		super(Tags.hydroenergy);
	}

	public HEServer(String name) {
		super(name);
		dams = new HEDam[HE.maxControllers];
		for(int waterId = 0; waterId< dams.length; waterId++) {
			dams[waterId] = new HEDam(waterId);
		}
	}

	public static HEServer instance;
	
	public static HEServer load(World world) {
		HEServer instance = (HEServer) world.mapStorage.loadData(HEServer.class, Tags.hydroenergy);
		if (instance == null) {
			instance = new HEServer(Tags.hydroenergy);
			 world.mapStorage.setData(Tags.hydroenergy, instance);
		}
		return instance;
	}
	
	public class Tags {
		public static final String hydroenergy = "hydroenergy";
		public static final String dam = "dam";
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		dams = new HEDam[HE.maxControllers];
		for(int waterId = 0; waterId<HE.maxControllers; waterId++) {
			dams[waterId] = new HEDam(waterId);
			dams[waterId].readFromNBTFull(compound.getCompoundTag(Tags.dam + waterId));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		for(int waterId = 0; waterId< dams.length; waterId++) {
			NBTTagCompound damCompound = new NBTTagCompound();
			dams[waterId].writeToNBTFull(damCompound);
			compound.setTag(Tags.dam + waterId, damCompound);
		}
	}

	
	public boolean canControllerBePlaced() {
		for(HEDam dam : dams) {
			if (!dam.isPlaced()) {
				return true;
			}
		}
		return false;
	}
	
	public void onBreakController(int waterId) {
		dams[waterId].breakController();
		markDirty();
	}
	
	public int onPlacecontroller(int blockX, int blockY, int blockZ) {
		for(int waterId = 0; waterId< dams.length; waterId++) {
			if (!dams[waterId].isPlaced()) {
				dams[waterId].placeController(blockX, blockY, blockZ);
				markDirty();
				return waterId;
			}
		}
		return -1;
	}

	public void onBlockRemoved(int waterId, int blockY) {
		dams[waterId].onBlockRemoved(blockY);
	}

	public void onBlockPlaced(int waterId, int blockY) {
		dams[waterId].onBlockPlaced(blockY);
	}

	public float getWaterLevel(int waterId) {
		return dams[waterId].getWaterLevel();
	}
	
	public void setWaterLevel(int waterId, float waterLevel) {
		if(dams[waterId].setWaterLevel(waterLevel)) {
			markDirty();
		}
	}

	public void setDebugState(int waterId, boolean debugState) {
		dams[waterId].setDebugState(debugState);
	}

	public int getWaterLimitWest(int waterId) {
		return dams[waterId].getLimitWest();
	}
	
	public int getWaterLimitDown(int waterId) {
		return dams[waterId].getLimitDown();
	}

	public int getWaterLimitNorth(int waterId) {
		return dams[waterId].getLimitNorth();
	}

	public int getWaterLimitEast(int waterId) {
		return dams[waterId].getLimitEast();
	}

	public int getWaterLimitUp(int waterId) {
		return dams[waterId].getLimitUp();
	}

	public int getWaterLimitSouth(int waterId) {
		return dams[waterId].getLimitSouth();
	}

	public boolean isBlockOutOfBounds(int waterId, int blockX, int blockY, int blockZ) {
		HEDam dam = dams[waterId];
		return blockX > dam.limitEast
				|| blockX < dam.limitWest
				|| blockY > dam.limitUp
				|| blockY < dam.limitDown
				|| blockZ > dam.limitSouth
				|| blockZ < dam.limitNorth;
	}

	public void setWaterLimitWest(int waterId, int limitWest) {
		if(dams[waterId].setLimitWest(limitWest)) {
			markDirty();
		}
	}

	public void setWaterLimitDown(int waterId, int limitDown) {
		if(dams[waterId].setLimitDown(limitDown)) {
			markDirty();
		}
	}

	public void setWaterLimitNorth(int waterId, int limitNorth) {
		if(dams[waterId].setLimitNorth(limitNorth)) {
			markDirty();
		}
	}

	public void setWaterLimitEast(int waterId, int limitEast) {
		if(dams[waterId].setLimitEast(limitEast)) {
			markDirty();
		}
	}

	public void setWaterLimitUp(int waterId, int limitUp) {
		if(dams[waterId].setLimitUp(limitUp)) {
			markDirty();
		}
	}

	public void setWaterLimitSouth(int waterId, int limitSouth) {
		if(dams[waterId].setLimitSouth(limitSouth)) {
			markDirty();
		}
	}

	public void spreadWater(int waterId) {
		if(dams[waterId].drainState) {
			dams[waterId].drainState = false;
			markDirty();
		}
	}

	public void drainWater(int waterId) {
		if(!dams[waterId].drainState) {
			dams[waterId].drainState = true;
			markDirty();
		}
	}

	public boolean canSpread(int waterId) {
		return dams[waterId].isPlaced() && !dams[waterId].drainState;
	}

	public void synchronizeClient(PlayerLoggedInEvent event) {
		HEPacketSynchronize message = new HEPacketSynchronize();
		for(int waterId = 0; waterId< dams.length; waterId++) {
			message.waterLevels[waterId] = dams[waterId].getWaterLevel();
			message.debugStates[waterId] = dams[waterId].getDebugState();
			message.drainStates[waterId] = dams[waterId].drainState;
			message.limitsWest[waterId] = dams[waterId].limitWest;
			message.limitsDown[waterId] = dams[waterId].limitDown;
			message.limitsNorth[waterId] = dams[waterId].limitNorth;
			message.limitsEast[waterId] = dams[waterId].limitEast;
			message.limitsUp[waterId] = dams[waterId].limitUp;
			message.limitsSouth[waterId] = dams[waterId].limitSouth;
		}
		HE.network.sendTo(message, (EntityPlayerMP) event.player);
	}

	public void onConfigRequest(int waterId, boolean debugState, boolean drainState, int limitWest, int limitDown, int limitNorth, int limitEast, int limitUp, int limitSouth) {
		if(waterId < 0 || waterId >= HE.maxControllers) {
			return;
		}
		dams[waterId].onConfigRequest(debugState, drainState, limitWest, limitDown, limitNorth, limitEast, limitUp, limitSouth);
	}
}
