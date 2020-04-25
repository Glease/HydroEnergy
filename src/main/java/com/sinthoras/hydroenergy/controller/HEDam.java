package com.sinthoras.hydroenergy.controller;

import com.sinthoras.hydroenergy.HE;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class HEDam extends WorldSavedData {
	
	private static final String prefix = HE.MODID + ":";
	private static final String tag = prefix + "data_storage";
	private static HEDam instance;
	
	private HEController[] controllers;
	
	public HEDam() {
		super(HE.MODID + ":data_storage");
	}
	
	public HEDam(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		int max_controller = compound.getInteger(prefix + HE.saveTags.max_controller);
		if(max_controller == 0)
		{
			// Create all new
		}
		else
		{
			// read from NBT
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setInteger(HE.MODID + ":" + HE.saveTags.max_controller, HEController.max_controller);
		
		markDirty();
	}

	public static HEDam get(World world) {
		if(instance == null)
		{
			MapStorage storage = world.mapStorage;
			HEDam result = (HEDam)storage.loadData(HEDam.class, tag);
			if (result == null) {
				result = new HEDam(tag);
				storage.setData(tag, result);
			}
			instance = result;
		}
		return instance;
	}
}
