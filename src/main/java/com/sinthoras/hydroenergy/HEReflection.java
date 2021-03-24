package com.sinthoras.hydroenergy;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.lang.reflect.Field;

public class HEReflection {

    private static Field blockRefCount;
    private static Field tickRefCount;

    static {
        try {
            boolean isDeobfuscated = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
            blockRefCount = ExtendedBlockStorage.class.getDeclaredField(isDeobfuscated ? "blockRefCount" : "field_76682_b");
            blockRefCount.setAccessible(true);
            tickRefCount = ExtendedBlockStorage.class.getDeclaredField(isDeobfuscated ? "tickRefCount" : "field_76683_c");
            tickRefCount.setAccessible(true);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static int getTickRefCount(ExtendedBlockStorage extendedBlockStorage) {
        try {
            return tickRefCount.getInt(extendedBlockStorage);
        }
        catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void setTickRefCount(ExtendedBlockStorage extendedBlockStorage, int value) {
        try {
            tickRefCount.setInt(extendedBlockStorage, value);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static int getBlockRefCount(ExtendedBlockStorage extendedBlockStorage) {
        try {
            return blockRefCount.getInt(extendedBlockStorage);
        }
        catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void setBlockRefCount(ExtendedBlockStorage extendedBlockStorage, int value) {
        try {
            blockRefCount.setInt(extendedBlockStorage, value);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
