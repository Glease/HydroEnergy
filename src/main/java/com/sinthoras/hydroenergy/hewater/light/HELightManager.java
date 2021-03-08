package com.sinthoras.hydroenergy.hewater.light;

import com.sinthoras.hydroenergy.hewater.HEWater;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

@SideOnly(Side.CLIENT)
public class HELightManager {

    public static void onChunkUnload(int chunkX, int chunkZ) {

    }

    public static void onChunkDataLoad(Chunk chunk, int subChunkHasDataFlags) {
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
