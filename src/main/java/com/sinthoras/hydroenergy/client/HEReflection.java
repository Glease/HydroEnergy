package com.sinthoras.hydroenergy.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;

@SideOnly(Side.CLIENT)
public class HEReflection {

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
}
