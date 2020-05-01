package com.sinthoras.hydroenergy.hewater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.sinthoras.hydroenergy.HE;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.world.ChunkEvent;

public class HERenderManager
{
	private HashMap<Long, HERenderChunk[]> chunks = new HashMap<Long, HERenderChunk[]>();
	
	public static HERenderManager instance;

	private class HERenderChunk
	{
		public int x;
		public int y;
		public int z;
		public long controllerIds;
		
		public HERenderChunk(int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.controllerIds = 0;
		}
		
		public void addControllerId(int controllerId)
		{
			controllerIds |= 1L << controllerId;
		}
	}
	
	public long getKey(int x, int z)
	{
		return (long)x << 32 | z & 0xFFFFFFFFL;
	}
	
	public void addBlock(int x, int y, int z, int controllerId)
	{
		int chunkX = MathHelper.bucketInt(x, 16);
		int chunkY = MathHelper.bucketInt(y, 16);
		int chunkZ = MathHelper.bucketInt(z, 16);
		
		HERenderChunk[] chunk;
		long key = getKey(chunkX, chunkZ);
		if(chunks.containsKey(key))
			chunk = chunks.get(key);
		else
		{
			chunk = new HERenderChunk[HE.worldHeight / 16];
			chunks.put(key, chunk);
		}
		
		if(chunk[chunkY] == null)
			chunk[chunkY] = new HERenderChunk(x, y, z);
		chunk[chunkY].addControllerId(controllerId);
	}
	
	public void onChunkUnload(ChunkEvent.Unload event)
	{
		int x = MathHelper.bucketInt(event.getChunk().xPosition, 16);
		int z = MathHelper.bucketInt(event.getChunk().zPosition, 16);
		
		chunks.remove(getKey(x, z));
	}
	
	public void triggerRenderUpdate(long flags)
	{
		for(HERenderChunk[] chunk : chunks.values())
		{
			for(int i=0;i<HE.worldHeight / 16;i++)
				if(chunk[i] != null)
					if((chunk[i].controllerIds & flags) > 0)
						Minecraft.getMinecraft().renderGlobal.markBlockForRenderUpdate(chunk[i].x, chunk[i].y, chunk[i].z);
		}
	}
}
