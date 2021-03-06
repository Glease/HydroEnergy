package com.sinthoras.hydroenergy.controller;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.hewater.render.HERenderManager;

public class HEDamsClient {

	public static HEDamsClient instance;
	
	public float[] renderedWaterLevel;
	public boolean[] renderDebug;
	
	public HEDamsClient()
	{
		renderedWaterLevel = new float[HE.maxController];
		renderDebug = new boolean[HE.maxController];
	}
	
	public void onClientUpdate(int id, float renderedWaterLevel, boolean renderDebug)
	{
		HE.LOG.info("UPDATE RECEIVED:   " + id + "  " + renderedWaterLevel);
		this.renderedWaterLevel[id] = renderedWaterLevel;
		this.renderDebug[id] = renderDebug;
		//HERenderManager.instance.triggerRenderUpdate(id);
	}
	
	public void onSetDebugMode(int id, boolean value)
	{
		renderDebug[id] = value;
	}

	public float[] getDebugModes() {
		float[] copy = new float[renderDebug.length];
		for(int i=0;i<renderDebug.length;i++)
			copy[i] = renderDebug[i] ? 1.0f : 0.0f;
		return copy;
	}
	
	public float getRenderedWaterLevel(int id)
	{
		if(renderDebug[id])
			return 0.0f;
		else
			return renderedWaterLevel[id];
	}

	public float[] getAllWaterLevels() {
		float[] copy = new float[renderedWaterLevel.length];
		for(int i=0;i<renderedWaterLevel.length;i++)
			if(renderDebug[i])
				copy[i] = 255.0f;
			else
				copy[i] = renderedWaterLevel[i];
		return copy;
	}

	public void onClientSynchronize(float[] renderedWaterLevel) {
		this.renderedWaterLevel = renderedWaterLevel;
	}
}
