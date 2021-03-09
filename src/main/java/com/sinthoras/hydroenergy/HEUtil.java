package com.sinthoras.hydroenergy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;

public class HEUtil {

	private static Field locationLightMap;
	private static Field frustrumX;
	private static Field frustrumY;
	private static Field frustrumZ;
	static {
		try {
			locationLightMap = EntityRenderer.class.getDeclaredField("locationLightMap");
			locationLightMap.setAccessible(true);
			frustrumX = Frustrum.class.getDeclaredField("xPosition");
			frustrumX.setAccessible(true);
			frustrumY = Frustrum.class.getDeclaredField("yPosition");
			frustrumY.setAccessible(true);
			frustrumZ = Frustrum.class.getDeclaredField("zPosition");
			frustrumZ.setAccessible(true);
		}
		catch(Exception e) {}
	}

	public static ResourceLocation getLightMapLocation() {
		try {
			return (ResourceLocation) locationLightMap.get(Minecraft.getMinecraft().entityRenderer);
		}
		catch(Exception e) {
			return null;
		}
	}

	public static float getCameraBlockX(ICamera camera) {
		try {
			return (float)frustrumX.getDouble(camera);
		}
		catch(Exception e) {
			return 0.0f;
		}
	}

	public static float getCameraBlockY(ICamera camera) {
		try {
			return (float)frustrumY.getDouble(camera);
		}
		catch(Exception e) {
			return 0.0f;
		}
	}

	public static float getCameraBlockZ(ICamera camera) {
		try {
			return (float)frustrumZ.getDouble(camera);
		}
		catch(Exception e) {
			return 0.0f;
		}
	}
	
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
}
