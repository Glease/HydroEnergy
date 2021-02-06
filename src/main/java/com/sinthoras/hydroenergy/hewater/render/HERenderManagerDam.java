package com.sinthoras.hydroenergy.hewater.render;

import java.util.HashMap;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.proxy.HECommonProxy;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;

public class HERenderManagerDam {
	
	private HashMap<Long, Short> chunks = new HashMap<Long, Short>();
	
	// x, y, z in block coords
	public void addBlock(int x, int y, int z)
	{
		int chunkX = HEUtil.bucketInt16(x);
		int chunkY = HEUtil.bucketInt16(y);
		int chunkZ = HEUtil.bucketInt16(z);
		long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
		short verticalField = chunks.containsKey(key) ? chunks.get(key) : 0;
		verticalField |= 1 << chunkY;
		chunks.put(key, verticalField);
	}
	
	// x, y, z in Chunk coords (/16)
	public void onChunkUnload(int chunkX, int chunkZ)
	{
		long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
		if(chunks.containsKey(key))
			chunks.remove(key);
	}
	
	public void triggerRenderUpdate(float waterHeight)
	{
		HE.LOG.info("Updated " + chunks.size() + " chunks");
		int counter = 0;
		for(long key : chunks.keySet())
		{
			int chunkX = (int)(key >> 32);
			int chunkZ = (int)key;
			int x = HEUtil.debucketInt16(chunkX);
			int z = HEUtil.debucketInt16(chunkZ);
			short verticalField = chunks.get(key);
			for(int y=0;y<16;y++)
				if(((verticalField >> y) & 1) == 1) {
					for(int i=0;i<5;i++)
					Minecraft.getMinecraft().renderGlobal.markBlocksForUpdate(x, y << 4, z, x, y << 4, z);
					
					// temporary light calculation
					Chunk chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(x >> 4, z >> 4);
					
					for(int _x = 0;_x<16;_x++)
						for(int _y=0;_y<16;_y++)
							for(int _z=0;_z<16;_z++)
								// works, but f-ing slow and must be split into parts
								Minecraft.getMinecraft().theWorld.func_147451_t(x+_x, (y<<4)+_y, z+_z);
					counter++;
				}
		}
		HE.LOG.info("Updated " + counter + " subchunks");
	}
}























