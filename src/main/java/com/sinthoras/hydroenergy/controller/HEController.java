package com.sinthoras.hydroenergy.controller;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.network.HEWaterUpdate;

import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class HEController {

	private static float[] waterLevels = new float[16];
	private static int[][] coordinates = new int[16][3];
	private static boolean[] placed = new boolean[16];
	private static boolean changed = false;
	
	public static void updateWaterLevel(int id, float level)
	{
		if(Math.abs(waterLevels[id] - level) >= 0.1)
			changed = true;
		waterLevels[id] = level;
	}
	
	public static void setWaterLevel(int id, float level)
	{
		changed = true;
		waterLevels[id] = level;
	}
	
	public static float getWaterLevel(int id)
	{
		return waterLevels[id];
	}
	
	public static void onUpdateWaterLevels(float[] levels)
	{
		waterLevels = levels;
	}
	
	// Every 10 seconds to check if update is necessary
	public static void on10sTick(ServerTickEvent event)
	{
		if (changed) {
			HEWaterUpdate message = new HEWaterUpdate();
			message.waterLevel = waterLevels;
			HE.network.sendToAll(message);
			changed = false;
		}
	}
	
	public static void onShutdown()
	{
		
	}
	
	public static void onStartup()
	{
		
	}
}
