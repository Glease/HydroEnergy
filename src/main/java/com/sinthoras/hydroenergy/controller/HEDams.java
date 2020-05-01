package com.sinthoras.hydroenergy.controller;

import java.math.BigInteger;
import java.util.ArrayList;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.hewater.HERenderManager;
import com.sinthoras.hydroenergy.network.HEWaterUpdate;

import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class HEDams extends WorldSavedData {
	
	private HEController[] controllers;
	
	public HEDams() {
		super(tags.dam);
	}
	
	public HEDams(String name) {
		super(name);
	}
	
	
	public static HEDams instance;
	
	public static HEDams get(World world) {
		if(instance == null)
		{
			MapStorage storage = world.mapStorage;
			HEDams result = (HEDams)storage.loadData(HEDams.class, tags.dam);
			if (result == null) {
				result = new HEDams(tags.dam);
				storage.setData(tags.dam, result);
			}
			instance = result;
		}
		return instance;
	}
	
	// Client only!
	public static HEDams init() {
		instance = new HEDams();
		instance.controllers = new HEController[16];  // TODO: move to config
		for(int i=0;i<instance.controllers.length;i++)
		{
			instance.controllers[i] = new HEController();
		}
		return instance;
	}
	
	
	public class tags
	{
		public static final String dam = "hydro_energy_dam";
		public static final String instance = "inst";
		public static final String max_controller = "max_c";
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		int max_controller = compound.getInteger(tags.max_controller);
		if(max_controller == 0)
		{
			max_controller = 16; // TODO: move to config
		}
		controllers = new HEController[max_controller];
		for(int i=0;i<max_controller;i++)
		{
			controllers[i] = new HEController();
			controllers[i].readFromNBTFull(compound.getCompoundTag(tags.instance + i));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setInteger(tags.max_controller, controllers.length);
		for(int i=0;i<controllers.length;i++)
		{
			NBTTagCompound subcompound = new NBTTagCompound();
			controllers[i].writeToNBTFull(subcompound);
			compound.setTag(tags.instance + i, subcompound);
		}
	}

	
	public void onTick(ServerTickEvent event)
	{
		boolean flag = false;
		for(HEController controller : controllers)
			if(controller.transmitUpdate())
				flag = true;
		if(flag)
		{
			HEWaterUpdate message = new HEWaterUpdate(getClientUpdate());
			HE.network.sendToAll(message);
			for(HEController controller : controllers)
				controller.updateSent();
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
		controllers[id].onBreakController();
	}
	
	public void onClientUpdate(NBTTagCompound compound)
	{
		long flags = 0L;
		for(int i=0;i<controllers.length;i++)
		{
			controllers[i].readFromNBTNetwork(compound.getCompoundTag(tags.instance + i));
			if(controllers[i].renderUpdate())
			{
				flags |= 1L << i;
				controllers[i].updateRendered();
			}
		}
		if(flags > 0)
			HERenderManager.instance.triggerRenderUpdate(flags);
	}
	
	private NBTTagCompound getClientUpdate() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger(tags.max_controller, controllers.length);
		for(int i=0;i<controllers.length;i++)
		{
			NBTTagCompound subcompound = new NBTTagCompound();
			controllers[i].writeToNBTNetwork(subcompound);
			compound.setTag(tags.instance + i, subcompound);
		}
		return compound;
	}
	
	public int reserveControllerId()
	{
		for(int i=0;i<controllers.length;i++)
			if(!controllers[i].isPlaced())
			{
				controllers[i].placeController();
				return i;
			}
		return -1;
	}
	
	public float getWaterLevel(int id)
	{
		return controllers[id].getWaterLevel();
	}
	
	public float getRenderedWaterLevel(int id)
	{
		return controllers[id].getRenderedWaterLevel();
	}
	
	public void updateWaterLevel(int id, float waterLevel)
	{
		controllers[id].updateWaterLevel(waterLevel);
	}
}
