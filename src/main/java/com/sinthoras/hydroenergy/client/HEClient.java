package com.sinthoras.hydroenergy.client;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.client.light.HELightManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HEClient {

	private static HEDam[] dams = new HEDam[HE.maxControllers];
	static {
		for(int waterId=0;waterId<dams.length;waterId++) {
			dams[waterId] = new HEDam(waterId);
		}
	}

	
	public static void onWaterUpdate(int waterId, float waterLevel) {
		if(waterId < 0 || waterId >= HE.maxControllers) {
			HE.LOG.error(HE.ERROR_serverIdsOutOfBounds);
			return;
		}
		dams[waterId].onWaterUpdate(waterLevel);
		HELightManager.onUpdateWaterLevels();
	}

	public static void onConfigUpdate(int waterId, int blockX, int blockY, int blockZ, HE.DamMode mode, int limitWest, int limitDown, int limitNorth, int limitEast, int limitUp, int limitSouth) {
		if(waterId < 0 || waterId >= HE.maxControllers) {
			HE.LOG.error(HE.ERROR_serverIdsOutOfBounds);
			return;
		}
		dams[waterId].onConfigUpdate(blockX, blockY, blockZ, mode,
				limitWest, limitDown, limitNorth, limitEast, limitUp, limitSouth);
	}

	public static float[] getDebugStatesAsFactors() {
		float[] debugFactors = new float[dams.length];
		for(int waterId = 0; waterId< debugFactors.length; waterId++) {
			debugFactors[waterId] = dams[waterId].renderAsDebug() ? 1.0f : 0.0f;
		}
		return debugFactors;
	}

	public static float[] getAllWaterLevelsForRendering() {
		float[] waterLevels = new float[HEClient.dams.length];
		for(int waterId = 0; waterId< waterLevels.length; waterId++) {
			waterLevels[waterId] = dams[waterId].getWaterLevelForRendering();
		}
		return waterLevels;
	}

	public static void onSynchronize(int[] blocksX, int[] blocksY, int[] blocksZ, float[] waterLevels, HE.DamMode[] modes, int[] limitsWest, int[] limitsDown, int[] limitsNorth, int[] limitsEast, int[] limitsUp, int[] limitsSouth) {
		if(dams.length != waterLevels.length) {
			HE.maxControllers = waterLevels.length;
			dams = new HEDam[waterLevels.length];
			for(int waterId=0;waterId<dams.length;waterId++) {
				dams[waterId] = new HEDam(waterId);
			}
		}
		for(int waterId=0;waterId<dams.length;waterId++) {
			dams[waterId].onConfigUpdate(blocksX[waterId], blocksY[waterId], blocksZ[waterId], modes[waterId],
					limitsWest[waterId], limitsDown[waterId], limitsNorth[waterId], limitsEast[waterId], limitsUp[waterId], limitsSouth[waterId]);
			dams[waterId].onWaterUpdate(waterLevels[waterId]);
		}
	}

	public static int getWaterId(int blockX, int blockY, int blockZ) {
		for(int waterId=0;waterId<HE.maxControllers;waterId++) {
			if(dams[waterId].belongsToController(blockX, blockY, blockZ)) {
				return waterId;
			}
		}
		return -1;
	}

	public static HEDam getDam(int waterId) {
		return dams[waterId];
	}

	public static void onDisconnect() {
		dams = new HEDam[HE.maxControllers];
		for(int waterId=0;waterId<dams.length;waterId++) {
			dams[waterId] = new HEDam(waterId);
		}
	}
}
