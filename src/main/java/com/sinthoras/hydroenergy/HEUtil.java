package com.sinthoras.hydroenergy;

public class HEUtil {
	
	public static int coordBlockToChunk(int value) {
		return value < 0 ? -((-value - 1) >> 4) - 1 : value >> 4;
	}
	
	public static int coordChunkToBlock(int value) {
		return value < 0 ? -((-value) << 4) : value << 4;
	}
	
	public static long chunkCoordsToKey(int x, int z) {
		return (((long)x) << 32) | (z & 0xffffffffL);
	}

	public static float clamp(float value, float lowerLimit, float upperLimit) {
		return Math.min(Math.max(value, lowerLimit), upperLimit);
	}
}
