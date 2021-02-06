package com.sinthoras.hydroenergy.hewater.render;


import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.controller.HEDamsClient;

import net.minecraftforge.event.world.ChunkEvent;

public class HERenderManager
{
	public static HERenderManager instance;

	private HERenderManagerDam[] renderDams;
	
	public HERenderManager()
	{
		renderDams = new HERenderManagerDam[HE.maxController];
		for(int i=0;i<renderDams.length;i++) {
			renderDams[i] = new HERenderManagerDam();
		}
	}
	
	public void addBlock(int x, int y, int z, int controllerId, boolean lightNeedsPatching) {
		renderDams[controllerId].addBlock(x, y, z, lightNeedsPatching);
	}
	
	public void onChunkUnload(ChunkEvent.Unload event) {
		int chunkX = event.getChunk().xPosition;
		int chunkZ = event.getChunk().zPosition;
		for(HERenderManagerDam dam : renderDams)
			dam.onChunkUnload(chunkX, chunkZ);
	}
	
	public void onChunkLoad(ChunkEvent.Load event) {
		int chunkX = event.getChunk().xPosition;
		int chunkZ = event.getChunk().zPosition;
		for(HERenderManagerDam dam : renderDams)
			dam.onChunkLoad(chunkX, chunkZ);
	}
	
	public void triggerRenderUpdate(int controllerId)
	{
		float waterLevel = HEDamsClient.instance.getRenderedWaterLevel(controllerId);
		renderDams[controllerId].triggerRenderUpdate(waterLevel);
	}
	
	public void triggerRenderUpdate()
	{
		for(int i=0;i<renderDams.length;i++)
		{
			triggerRenderUpdate(i);
		}
	}
	
	public void onRenderTick() {
		for(HERenderManagerDam dam : renderDams)
			dam.onRenderTick();
	}
}
