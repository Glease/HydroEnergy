package com.sinthoras.hydroenergy.hewater.render;

import java.util.HashMap;

import com.sinthoras.hydroenergy.HE;

import net.minecraft.client.Minecraft;

public class HERenderManagerDam {
	
	private class HERenderChunk
	{
		private boolean[] renderChunks = new boolean[HE.worldHeight / 16];
		private int x;
		private int z;
		
		// x, z in block coords
		public HERenderChunk(int x, int z)
		{
			this.x = x;
			this.z = z;
		}
		
		public void addBlock(int y)
		{
			renderChunks[y/16] = true;
		}
		
		// x, z in block coords
		public void triggerRenderUpdate()
		{
			for(int i=0;i<renderChunks.length;i++)
			{
				if(renderChunks[i])
					Minecraft.getMinecraft().renderGlobal.markBlockForRenderUpdate(x, i * 16, z);
			}
		}
	}
	
	private HashMap<Long, HERenderChunk> chunks = new HashMap<Long, HERenderChunk>();
	
	private long getKey(int x, int z)
	{
		return (long)x << 32 | z & 0xFFFFFFFFL;
	}
	
	// x, y, z in block coords
	public void addBlock(int x, int y, int z)
	{
		HERenderChunk chunk;
		long key = getKey(x, z);
		if(chunks.containsKey(key))
			chunk = chunks.get(key);
		else
		{
			chunk = new HERenderChunk(x, z);
			chunks.put(key, chunk);
		}
		chunk.addBlock(y);
	}
	
	// x, y, z in Chunk coords (/16)
	public void onChunkUnload(int x, int z)
	{
		chunks.remove(getKey(x, z));
	}
	
	public void triggerRenderUpdate()
	{
		for(HERenderChunk chunk : chunks.values())
		{
			chunk.triggerRenderUpdate();
		}
	}
}























