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

	public HEServer(String name) {
		super(name);
		dams = new HEDam[HE.maxController];
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
		dams = new HEDam[HE.maxController];
		for(int waterId=0;waterId<HE.maxController;waterId++) {
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

	public float getWaterLevel(int waterId) {
		return dams[waterId].getWaterLevel();
	}
	
	public void setWaterLevel(int waterId, float waterLevel) {
		if(dams[waterId].setWaterLevel(waterLevel)) {
			markDirty();
		}
	}

	public void setDebugState(int waterId, boolean debugState) {
		dams[waterId].setDebugMode(debugState);
	}

	public int getWaterLimitUp(int waterId) {
		return dams[waterId].limitUp;
	}
	
	public int getWaterLimitDown(int waterId) {
		return dams[waterId].limitDown;
	}

	public int getWaterLimitEast(int waterId) {
		return dams[waterId].limitEast;
	}

	public int getWaterLimitWest(int waterId) {
		return dams[waterId].limitWest;
	}

	public int getWaterLimitSouth(int waterId) {
		return dams[waterId].limitSouth;
	}

	public int getWaterLimitNorth(int waterId) {
		return dams[waterId].limitNorth;
	}

	public void setWaterLimitUp(int waterId, int limitUp) {
		if(dams[waterId].limitUp != limitUp) {
			dams[waterId].limitUp = limitUp;
			markDirty();
		}
	}

	public void setWaterLimitDown(int waterId, int limitDown) {
		if(dams[waterId].limitDown != limitDown) {
			dams[waterId].limitDown = limitDown;
			markDirty();
		}
	}

	public void setWaterLimitEast(int waterId, int limitEast) {
		if(dams[waterId].limitEast != limitEast) {
			dams[waterId].limitEast = limitEast;
			markDirty();
		}
	}

	public void setWaterLimitWest(int waterId, int limitWest) {
		if(dams[waterId].limitWest != limitWest) {
			dams[waterId].limitWest = limitWest;
			markDirty();
		}
	}

	public void setWaterLimitSouth(int waterId, int limitSouth) {
		if(dams[waterId].limitSouth != limitSouth) {
			dams[waterId].limitSouth = limitSouth;
			markDirty();
		}
	}

	public void setWaterLimitNorth(int waterId, int limitNorth) {
		if(dams[waterId].limitNorth != limitNorth) {
			dams[waterId].limitNorth = limitNorth;
			markDirty();
		}
	}

	public void spreadWater(int waterId) {
		if(dams[waterId].removeWater) {
			dams[waterId].removeWater = false;
			markDirty();
		}
	}

	public void removeWater(int waterId) {
		if(!dams[waterId].removeWater) {
			dams[waterId].removeWater = true;
			markDirty();
		}
	}

	public boolean canSpread(int waterId) {
		return dams[waterId].isPlaced() && !dams[waterId].removeWater;
	}

	public void synchronizeClient(PlayerLoggedInEvent event) {
		HEPacketSynchronize message = new HEPacketSynchronize(dams.length);
		for(int i = 0; i< dams.length; i++) {
			message.renderedWaterLevel[i] = dams[i].getWaterLevel();
			message.renderDebug[i] = dams[i].getDebugMode();
		}
		HE.network.sendTo(message, (EntityPlayerMP) event.player);
	}
}
