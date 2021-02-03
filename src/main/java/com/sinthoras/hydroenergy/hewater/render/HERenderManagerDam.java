package com.sinthoras.hydroenergy.hewater.render;

import java.util.HashMap;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.proxy.HECommonProxy;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;

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
		public void triggerRenderUpdate(float waterHeight)
		{
			for(int i=0;i<renderChunks.length;i++)
			{
				if(renderChunks[i])
				{
					//Minecraft.getMinecraft().theWorld.func_147451_t(x, i*16, z);
					Chunk chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(x >> 4, z >> 4);
					for(int _x = 0;_x<16;_x++)
						for(int _y=0;_y<16;_y++)
							for(int _z=0;_z<16;_z++)
								// works, but f-ing slow and must be split into parts
								Minecraft.getMinecraft().theWorld.func_147451_t(x+_x, i*16+_y, z+_z);
								
								/*float delta = i*16+_y - waterHeight;
								int lightVal = 15 + (int)(delta * 3);
								lightVal = Math.min(lightVal, 15);
								lightVal = Math.max(lightVal, 0);
								
								//Block block = chunk.getBlock(x+_x, i*16+_y, z+_z);
								//if(block == HECommonProxy.blockWaterStill)
								//{
									chunk.setLightValue(EnumSkyBlock.Block, lightVal, x+_x, i*16+_y, z+_z);
								//}*/
					
					Minecraft.getMinecraft().renderGlobal.markBlockForRenderUpdate(x, i * 16, z);
					
					
					
					//Minecraft.getMinecraft().theWorld.theProfiler.startSection("checkLight");
					//Minecraft.getMinecraft().theWorld.func_147451_t(x, i*16, z);
					//Minecraft.getMinecraft().theWorld.theProfiler.endSection();
				}
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
	
	public void triggerRenderUpdate(float waterHeight)
	{
		for(HERenderChunk chunk : chunks.values())
		{
			chunk.triggerRenderUpdate(waterHeight);
		}
	}
}























