package com.sinthoras.hydroenergy.hewater.render;

import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.hewater.HEWater;
import com.sinthoras.hydroenergy.hewater.HEWaterStatic;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.RenderBlockFluid;

public class HEWaterRenderer extends RenderBlockFluid {
	
	public static HEWaterRenderer instance = new HEWaterRenderer();
	private final int renderID = RenderingRegistry.getNextAvailableRenderId();

	
	@Override
	public float getFluidHeightForRender(IBlockAccess world, int x, int y, int z, BlockFluidBase block)
    {
		HEWaterStatic water = (HEWaterStatic) block;
		float val = water.getRenderedWaterLevel(world, x, y, z) - y;
		return HEUtil.clamp(val, 0.0f, 1.0f);
    }
	
	@Override
    public int getRenderId()
    {
        return renderID;
    }
	
	@Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        if (!(block instanceof HEWater))
        {
            return false;
        }

        Block[] neighbors = new Block[] {
                world.getBlock(x - 1, y, z),
                world.getBlock(x + 1, y, z),
                world.getBlock(x, y - 1, z),
                world.getBlock(x, y + 1, z),
                world.getBlock(x, y, z - 1),
                world.getBlock(x, y, z + 1)
        };

        boolean[] shouldSidesBeRendered = new boolean[]
        {
                !neighbors[0].isOpaqueCube() && neighbors[0] != block,
                !neighbors[1].isOpaqueCube() && neighbors[1] != block,
                !neighbors[2].isOpaqueCube() && neighbors[2] != block,
                !neighbors[3].isOpaqueCube() && neighbors[3] != block,
                !neighbors[4].isOpaqueCube() && neighbors[4] != block,
                !neighbors[5].isOpaqueCube() && neighbors[5] != block
        };

        int worldColorModifier = block.colorMultiplier(world, x, y, z);
        HETessalator.addBlock(x, y, z, ((HEWater)block).getId(), worldColorModifier, shouldSidesBeRendered);
        
        HEWaterStatic water = (HEWaterStatic) block;

        // legacy for lights:
        //HERenderManager.instance.addBlock(x, y, z, water.getId(), true);//, lightNeedsPatching);

        /* TODO: add color multiplier into shaders
        int color = block.colorMultiplier(world, x, y, z);
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        final float LIGHT_Y_NEG = 0.5F;
        final float LIGHT_Y_POS = 1.0F;
        final float LIGHT_XZ_NEG = 0.8F;
        final float LIGHT_XZ_POS = 0.6F;
        final double RENDER_OFFSET = 0.0010000000474974513D;*/
        return false;
    }
}
