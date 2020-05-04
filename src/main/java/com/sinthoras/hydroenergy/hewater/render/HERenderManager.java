package com.sinthoras.hydroenergy.hewater.render;


import com.sinthoras.hydroenergy.HE;

import net.minecraftforge.event.world.ChunkEvent;

public class HERenderManager
{
	public static HERenderManager instance;

	private HERenderManagerDam[] dams;
	
	public HERenderManager()
	{
		dams = new HERenderManagerDam[HE.maxController];
		for(int i=0;i<dams.length;i++)
		{
			dams[i] = new HERenderManagerDam();
		}
	}
	
	
	public void addBlock(int x, int y, int z, int controllerId)
	{
		dams[controllerId].addBlock(x, y, z);
	}
	
	public void onChunkUnload(ChunkEvent.Unload event)
	{
		for(HERenderManagerDam dam : dams)
		{
			dam.onChunkUnload(event.getChunk().xPosition, event.getChunk().zPosition);
		}
	}
	
	public void triggerRenderUpdate(int controllerId)
	{
		dams[controllerId].triggerRenderUpdate();
	}
	
	public void triggerRenderUpdate()
	{
		for(HERenderManagerDam dam : dams)
		{
			dam.triggerRenderUpdate();
		}
	}
}
