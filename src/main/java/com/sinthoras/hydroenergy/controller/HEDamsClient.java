package com.sinthoras.hydroenergy.controller;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.hewater.light.HELightManager;

public class HEDamsClient {
	
	public static float[] renderedWaterLevel = new float[HE.maxController];
	public static boolean[] renderDebug = new boolean[HE.maxController];

	
	public static void onClientUpdate(int id, float newWaterLevel, boolean isDebug) {
		HE.LOG.info("UPDATE RECEIVED:   " + id + "  " + newWaterLevel);
		renderedWaterLevel[id] = newWaterLevel;
		renderDebug[id] = isDebug;
		HELightManager.onUpdateWaterLevels();
	}
	
	public static void onSetDebugMode(int id, boolean value) {
		renderDebug[id] = value;
	}

	public static float[] getDebugModes() {
		float[] copy = new float[renderDebug.length];
		for(int i=0;i<renderDebug.length;i++) {
			copy[i] = renderDebug[i] ? 1.0f : 0.0f;
		}
		return copy;
	}
	
	public static float getRenderedWaterLevel(int id) {
		if(renderDebug[id]) {
			return 0.0f;
		}
		else {
			return renderedWaterLevel[id];
		}
	}

	public static float[] getAllWaterLevels() {
		float[] copy = new float[renderedWaterLevel.length];
		for(int i=0;i<renderedWaterLevel.length;i++) {
			copy[i] = renderDebug[i] ? 255.0f : renderedWaterLevel[i];
		}
		return copy;
	}

	public static void onClientSynchronize(float[] newWaterLevel) {
		renderedWaterLevel = newWaterLevel;
	}
}
