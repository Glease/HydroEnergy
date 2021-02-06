package com.sinthoras.hydroenergy.hewater.render;

import java.lang.reflect.Field;
import java.util.BitSet;

import com.sinthoras.hydroenergy.proxy.HECommonProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class HERelightChunk {
	
	private static Field storageArraysMapping;
	static {
		try {
			storageArraysMapping = Chunk.class.getDeclaredField("storageArrays");
			storageArraysMapping.setAccessible(true);
		} catch(Exception e) {}
	}
	
	public BitSet[] subChunks = new BitSet[16];
	
	public void addBlock(int x, int y, int z) {
		int chunkY = y >> 4;
		x = x & 15;
		y = y & 15;
		z = z & 15;
		
		if(subChunks[chunkY] == null)
			subChunks[chunkY] = new BitSet(16*16*16);
		
		subChunks[chunkY].set((x << 8) | (y << 4) | z);
	}
	
	public void applyLightPatch(int chunkX, int chunkZ, float waterLevel) {
		for(int chunkY=0;chunkY<16;chunkY++)
			if(subChunks[chunkY] != null)
				for (int linearCoord = subChunks[chunkY].nextSetBit(0); linearCoord != -1; linearCoord = subChunks[chunkY].nextSetBit(linearCoord + 1)) {
					patchBlockLight(chunkX, chunkY, chunkZ, linearCoord >> 8, (linearCoord >> 4) & 15, linearCoord & 15, waterLevel);
				}
	}
	
	private void patchBlockLight(int chunkX, int chunkY, int chunkZ, int x, int y, int z, float waterLevel) {
		try {
			Chunk chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(chunkX, chunkZ);
			
			ExtendedBlockStorage[] storageArrays = (ExtendedBlockStorage[]) storageArraysMapping.get(chunk);
			ExtendedBlockStorage extendedblockstorage = storageArrays[chunkY];
			if(extendedblockstorage == null)
				return;
			float diff = Math.min((chunkY << 4) - waterLevel + y, 0);
			int lightVal = (int)(15 + diff * HECommonProxy.blockWaterStill.getLightOpacity());
			lightVal = Math.max(lightVal, 0);
			extendedblockstorage.setExtSkylightValue(x, y, z, lightVal);//(int)(15 - diff * HECommonProxy.blockWaterStill.getLightOpacity()));
		} catch(Exception e) {}
	}
	
	/*
	private void setLightWithoutUpdate(long key, int lightVal) {
		try {
			int x = (int)(key >> 32);
			int z = (int)key;
			int y = 64; // random dev const
			Chunk chunk = Minecraft.getMinecraft().theWorld.getChunkFromBlockCoords(x, z);
			
			ExtendedBlockStorage[] storageArrays = (ExtendedBlockStorage[]) storageArraysMapping.get(chunk);
			ExtendedBlockStorage extendedblockstorage = storageArrays[y >> 4];
			if(extendedblockstorage == null)
				return; // continue whatever
			
			// if block has sky connection
			extendedblockstorage.setExtSkylightValue(x, y & 15, z, lightVal);
			// else
			extendedblockstorage.setExtBlocklightValue(x, y & 15, z, lightVal);
		} catch(Exception e) {}
	}
	
	private void getLight(long key) {
		try {
			int x = (int)(key >> 32);
			int z = (int)key;
			int y = 64; // random dev const
			Chunk chunk = Minecraft.getMinecraft().theWorld.getChunkFromBlockCoords(x, z);
		
		
			ExtendedBlockStorage[] storageArrays = (ExtendedBlockStorage[]) storageArraysMapping.get(chunk);
			ExtendedBlockStorage extendedblockstorage = storageArrays[y >> 4];
			if(extendedblockstorage == null)
				return; // continue whatever
			
			// if block has sky connection
			extendedblockstorage.getExtSkylightValue(x, y & 15, z);
			//else
			extendedblockstorage.getExtBlocklightValue(x, y & 15, z);
		} catch(Exception e) {}
	}
	*/
}
