package com.sinthoras.hydroenergy.hewater.render;


import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.controller.HEDamsClient;

import net.minecraftforge.event.world.ChunkEvent;

public class HERenderManager
{
	public static HERenderManager instance;

	private HERenderManagerDam[] renderDams;
	private HELightManagerDam[] lightDams;
	
	public HERenderManager()
	{
		renderDams = new HERenderManagerDam[HE.maxController];
		for(int i=0;i<renderDams.length;i++)
		{
			renderDams[i] = new HERenderManagerDam();
		}
		lightDams = new HELightManagerDam[HE.maxController];
		for(int i=0;i<lightDams.length;i++)
		{
			lightDams[i] = new HELightManagerDam();
		}
	}
	
	
	public void addBlock(int x, int y, int z, int controllerId, boolean lightNeedsPatching) {
		renderDams[controllerId].addBlock(x, y, z);
		if(lightNeedsPatching)
			lightDams[controllerId].addBlock(x, y, z);
	}
	
	public void onChunkUnload(ChunkEvent.Unload event) {
		int chunkX = event.getChunk().xPosition;
		int chunkZ = event.getChunk().zPosition;
		for(HERenderManagerDam dam : renderDams)
			dam.onChunkUnload(chunkX, chunkZ);
		for(HELightManagerDam dam : lightDams)
			dam.onChunkUnload(chunkX, chunkZ);
	}
	
	public void onChunkLoad(ChunkEvent.Load event) {
		int chunkX = event.getChunk().xPosition;
		int chunkZ = event.getChunk().zPosition;
		for(HELightManagerDam dam : lightDams)
			dam.onChunkLoad(chunkX, chunkZ);
	}
	
	public void triggerRenderUpdate(int controllerId)
	{
		float waterLevel = HEDamsClient.instance.getRenderedWaterLevel(controllerId);
		long time1 = java.lang.System.nanoTime();
		renderDams[controllerId].triggerRenderUpdate(waterLevel);
		time1 = java.lang.System.nanoTime() - time1;
		long time2 = java.lang.System.nanoTime();
		lightDams[controllerId].triggerLightPatch(waterLevel);
		time2 = java.lang.System.nanoTime() - time2;
		HE.LOG.info("render: " + time1);
		HE.LOG.info("light: " + time2);
	}
	
	public void triggerRenderUpdate()
	{
		for(int i=0;i<renderDams.length;i++)
		{
			triggerRenderUpdate(i);
		}
	}
}
