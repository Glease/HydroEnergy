package com.sinthoras.hydroenergy;

public class HEUtil {
	
	public static int bucketInt16(int value) {
		return value < 0 ? -((-value - 1) >> 4) - 1 : value >> 4;
	}
	
	public static int debucketInt16(int value) {
		return value < 0 ? -((-value) << 4) : value << 4;
	}
	
	public static long chunkXZ2Int(int x, int z) {
		return (((long)x) << 32) | (z & 0xffffffffL);
	}
}
