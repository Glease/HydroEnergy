package com.sinthoras.hydroenergy.controller;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.network.HEPacketSynchronize;

import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class HEDams extends WorldSavedData {
	
	private HEController[] controllers;
	
	public boolean renderDebugMode = false;
	
	public HEDams() {
		super(Tags.hydroenergy);
		controllers = new HEController[HE.maxController];
		for(int i=0;i<controllers.length;i++)
		{
			controllers[i] = new HEController();
		}
	}
	
	public HEDams(String name) {
		super(name);
		controllers = new HEController[HE.maxController];
		for(int i=0;i<controllers.length;i++)
		{
			controllers[i] = new HEController();
		}
	}
	
	
	public static HEDams instance;
	
	public static HEDams load(World world) {
		HEDams instance = (HEDams) world.mapStorage.loadData(HEDams.class, Tags.hydroenergy);
		if (instance == null) {
			instance = new HEDams(Tags.hydroenergy);
			 world.mapStorage.setData(Tags.hydroenergy, instance);
		}
		return instance;
	}
	
	public class Tags
	{
		public static final String hydroenergy = "hydroenergy";
		public static final String instance = "inst";
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		controllers = new HEController[HE.maxController];
		for(int i=0;i<HE.maxController;i++)
		{
			controllers[i] = new HEController();
			controllers[i].readFromNBTFull(compound.getCompoundTag(Tags.instance + i));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		for(int i=0;i<controllers.length;i++)
		{
			NBTTagCompound subcompound = new NBTTagCompound();
			controllers[i].writeToNBTFull(subcompound);
			compound.setTag(Tags.instance + i, subcompound);
		}
	}

	
	public boolean canControllerBePlaced()
	{
		for(HEController controller : controllers)
			if(!controller.isPlaced())
				return true;
		return false;
	}
	
	public void onBreakController(int id)
	{
		controllers[id].breakController();
		markDirty();
	}
	
	public int reserveControllerId(int yCoord)
	{
		for(int i=0;i<controllers.length;i++)
			if(!controllers[i].isPlaced())
			{
				controllers[i].placeController(yCoord);
				markDirty();
				return i;
			}
		return -1;
	}
	
	public double getWaterLevel(int id)
	{
		return controllers[id].getWaterLevel();
	}
	
	public float getRenderedWaterLevel(int id)
	{
		return controllers[id].getRenderedWaterLevel();
	}
	
	public void updateWaterLevel(int id, double waterLevel)
	{
		controllers[id].updateWaterLevel(waterLevel);
		markDirty();
	}

	public void updateDebugState(int id, boolean debugState) {
		controllers[id].updateDebugState(debugState);
	}

	public int getWaterLimitUp(int id) {
		return controllers[id].getWaterLimitUp();
	}
	
	public int getWaterLimitDown(int id) {
		return controllers[id].getWaterLimitDown();
	}

	public void synchronizeClient(PlayerLoggedInEvent event) {
		HEPacketSynchronize message = new HEPacketSynchronize(controllers.length);
		for(int i=0;i<controllers.length;i++)
		{
			message.renderedWaterLevel[i] = controllers[i].getRenderedWaterLevel();
		}
		HE.network.sendTo(message, (EntityPlayerMP) event.player);
	}
}
