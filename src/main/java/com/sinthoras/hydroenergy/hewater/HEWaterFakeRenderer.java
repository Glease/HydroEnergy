package com.sinthoras.hydroenergy.hewater;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.RegistrySimple;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.RenderBlockFluid;

public class HEWaterFakeRenderer extends RenderBlockFluid {
	
	public static HEWaterFakeRenderer instance = new HEWaterFakeRenderer();
	private int renderID = -1;
	
	public HEWaterFakeRenderer()
	{
		if(renderID == -1)
			renderID = RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
		HEWaterTesselator.renderWorldBlock(world, x, y, z, block, modelId, renderer);
		return false;
    }
	
	@Override
    public int getRenderId()
    {
        return renderID;
    }
}
