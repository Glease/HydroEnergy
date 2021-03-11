package com.sinthoras.hydroenergy;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.demo.DemoWorldServer;

public class HEUtil {
	
	public static int coordBlockToChunk(int blockCoord) {
		return blockCoord < 0 ? -((-blockCoord - 1) >> 4) - 1 : blockCoord >> 4;
	}
	
	public static int coordChunkToBlock(int chunkCoord) {
		return chunkCoord < 0 ? -((-chunkCoord) << 4) : chunkCoord << 4;
	}
	
	public static long chunkCoordsToKey(int chunkX, int chunkZ) {
		return (((long)chunkX) << 32) | (chunkZ & 0xffffffffL);
	}

	public static float clamp(float value, float lowerLimit, float upperLimit) {
		return Math.min(Math.max(value, lowerLimit), upperLimit);
	}

	public static boolean isServerWorld(World world) {
		return world instanceof WorldServer
				|| world instanceof WorldServerMulti
				|| world instanceof DemoWorldServer;
	}
}
