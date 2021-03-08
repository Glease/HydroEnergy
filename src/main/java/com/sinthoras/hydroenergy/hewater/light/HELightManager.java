package com.sinthoras.hydroenergy.hewater.light;

import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.hewater.HEWater;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Stack;

@SideOnly(Side.CLIENT)
public class HELightManager {

    private static final HashMap<Long, HELightChunk> chunks = new HashMap<Long, HELightChunk>();
    private static final Stack<HELightChunk> availableBuffers = new Stack<HELightChunk>();

    public static void onChunkUnload(int chunkX, int chunkZ) {
        long key = HEUtil.chunkCoordsToKey(chunkX, chunkZ);
        HELightChunk lightChunk = chunks.get(key);
        availableBuffers.push(lightChunk);
        chunks.remove(key);
    }

    public static void onChunkDataLoad(Chunk chunk, int subChunkHasDataFlags) {
        HELightChunk lightChunk = null;
        if(availableBuffers.empty()) {
            lightChunk = new HELightChunk();
        }
        else
            lightChunk = availableBuffers.pop();

        int chunkX = chunk.xPosition;
        int chunkZ = chunk.zPosition;
        long key = HEUtil.chunkCoordsToKey(chunkX, chunkZ);
        chunks.put(key, lightChunk);

        // iterate through block and note down water blocks
        // also apply light patch
    }

    public static void onSetBlock(int x, int y, int z, Block block, int metadata, Block oldBlock) {
        if(oldBlock instanceof HEWater) {

            // remove flag
        }
        if(block instanceof  HEWater) {

            // add flag
            // apply light patch? Probably needs to be later
        }
    }

    public static void onPreRender(World world, int blockX, int bockY, int blockZ) {
        //apply light patch every time? Some times? benchmark for decision
    }


    /*
    Light update stuff
        x = x & 15;
        y = y & 15;
        z = z & 15;
        lightUpdateFlags.set((x << 8) | (y << 4) | z);
     */
}


class HELightChunk {
    public BitSet[] lightFlags;
    public int[][] waterIds;

    public HELightChunk() {
        lightFlags = new BitSet[16];
        for(int i=0;i<lightFlags.length;i++)
            lightFlags[i] = new BitSet(16*16*16);

        waterIds = new int[16][16];
    }
}