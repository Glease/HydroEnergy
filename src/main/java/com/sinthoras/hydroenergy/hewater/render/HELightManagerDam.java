package com.sinthoras.hydroenergy.hewater.render;

import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.HashMap;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.proxy.HECommonProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class HELightManagerDam {
	
	private HashMap<Long, HERelightChunk> relightChunks = new HashMap<Long, HERelightChunk>();
	
	public float waterLevel = 0.0f;
	
	// x, y, z in block coords
	public void addBlock(int x, int y, int z) {
		int chunkX = HEUtil.bucketInt16(x);
		int chunkZ = HEUtil.bucketInt16(z);
		long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
		
		if(!relightChunks.containsKey(key)) {
			HERelightChunk chunk = new HERelightChunk();
			chunk.addBlock(x, y, z);
			relightChunks.put(key, chunk);
		} else {
			relightChunks.get(key).addBlock(x, y, z);
		}
	}
	
	// x, y, z in Chunk coords (/16)
	public void onChunkUnload(int chunkX, int chunkZ) {
		long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
		if(relightChunks.containsKey(key))
			relightChunks.remove(key);
	}
	
	public void onChunkLoad(int chunkX, int chunkZ) {
		/*
		long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
		if(relightChunks.containsKey(key))
			relightChunks.get(key).applyLightPatch(chunkX, chunkZ);
		*/
	}
	
	public void triggerLightPatch(long key, WorldClient world) {
		//long t = System.nanoTime();
		if(relightChunks.containsKey(key)) {
			int chunkX = (int)(key >> 32);
			int chunkZ = (int)key;
			relightChunks.get(key).applyLightPatch(chunkX, chunkZ, waterLevel, world);
		}
		//t = System.nanoTime() - t;
		//HE.LOG.info("deltaT: " + t + "ns");
	}
}
