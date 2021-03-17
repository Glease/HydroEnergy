package com.sinthoras.hydroenergy;

import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.lang.reflect.Field;

public class HEReflection {

    private static Field blockRefCount;
    private static Field tickRefCount;

    static {
        try {
            blockRefCount = ExtendedBlockStorage.class.getDeclaredField("blockRefCount");
            blockRefCount.setAccessible(true);
            tickRefCount = ExtendedBlockStorage.class.getDeclaredField("tickRefCount");
            tickRefCount.setAccessible(true);
        }
        catch(Exception e) {}
    }

    public static int getTickRefCount(ExtendedBlockStorage extendedBlockStorage) {
        try {
            return tickRefCount.getInt(extendedBlockStorage);
        }
        catch(Exception e) {
            return 0;
        }
    }

    public static void setTickRefCount(ExtendedBlockStorage extendedBlockStorage, int value) {
        try {
            tickRefCount.setInt(extendedBlockStorage, value);
        }
        catch(Exception e) {}
    }

    public static int getBlockRefCount(ExtendedBlockStorage extendedBlockStorage) {
        try {
            return blockRefCount.getInt(extendedBlockStorage);
        }
        catch(Exception e) {
            return 0;
        }
    }

    public static void setBlockRefCount(ExtendedBlockStorage extendedBlockStorage, int value) {
        try {
            blockRefCount.setInt(extendedBlockStorage, value);
        }
        catch(Exception e) {}
    }
}
