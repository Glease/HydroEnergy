package com.sinthoras.hydroenergy.controller;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.hewater.render.HERenderManager;

public class HEDamsClient {

	public static HEDamsClient instance;
	
	public float[] renderedWaterLevel;
	public boolean debugMode;
	
	public HEDamsClient()
	{
		renderedWaterLevel = new float[HE.maxController];
	}
	
	public void onClientUpdate(int id, float renderedWaterLevel)
	{
		HE.LOG.info("UPDATE RECEIVED:   " + id + "  " + renderedWaterLevel);
		this.renderedWaterLevel[id] = renderedWaterLevel;
		HERenderManager.instance.triggerRenderUpdate(id);
	}
	
	public void onSetDebugMode(boolean value)
	{
		if(debugMode != value)
			HERenderManager.instance.triggerRenderUpdate();
		debugMode = value;
	}
	
	public float getRenderedWaterLevel(int id)
	{
		return renderedWaterLevel[id];
	}

	public void onClientSynchronize(float[] renderedWaterLevel) {
		this.renderedWaterLevel = renderedWaterLevel;
	}
}
