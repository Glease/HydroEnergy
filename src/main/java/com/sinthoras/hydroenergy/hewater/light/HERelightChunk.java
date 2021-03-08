package com.sinthoras.hydroenergy.hewater.light;

import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.List;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.proxy.HECommonProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.chunk.Chunk;

public class HERelightChunk {
	
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
	
	public void applyLightPatch(int chunkX, int chunkZ, float waterLevel, WorldClient world) {
		Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
		for(int chunkY=0;chunkY<16;chunkY++)
			if(subChunks[chunkY] != null) {
				for (int linearCoord = subChunks[chunkY].nextSetBit(0); linearCoord != -1; linearCoord = subChunks[chunkY].nextSetBit(linearCoord + 1))
					patchBlockLight(chunkX, chunkY, chunkZ, linearCoord >> 8, (linearCoord >> 4) & 15, linearCoord & 15, waterLevel, world, chunk);
			}
	}
	
	private void patchBlockLight(int chunkX, int chunkY, int chunkZ, int x, int y, int z, float waterLevel, WorldClient world, Chunk chunk) {
		float diff = Math.min((chunkY << 4) - waterLevel + y, 0);
		int lightVal = (int)(15 + diff * HECommonProxy.blockWaterStill.getLightOpacity());
		lightVal = Math.max(lightVal, 0);
		world.setLightValue(EnumSkyBlock.Sky, chunkX*16+x, chunkY*16+y, chunkZ*16+z, lightVal);
	}
}
