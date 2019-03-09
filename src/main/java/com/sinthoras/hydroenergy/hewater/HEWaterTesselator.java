package com.sinthoras.hydroenergy.hewater;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class HEWaterTesselator {
	
	private static int x=0, y=0, z=0;

    public static void renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
    	if(!(block instanceof HEWater))
    		throw new IllegalArgumentException("This renderer can only handle HEWater blocks");
    	HEWaterTesselator.x = x;
    	HEWaterTesselator.y = y;
    	HEWaterTesselator.z = z;
    }
}
