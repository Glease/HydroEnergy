package com.sinthoras.hydroenergy.client.renderer;

import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.blocks.HEWater;
import com.sinthoras.hydroenergy.blocks.HEWaterStill;

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
	public float getFluidHeightForRender(IBlockAccess world, int blockX, int blockY, int blockZ, BlockFluidBase block) {
		HEWaterStill water = (HEWaterStill) block;
		float val = water.getWaterLevel() - blockY;
		return HEUtil.clamp(val, 0.0f, 1.0f);
    }
	
	@Override
    public int getRenderId() {
        return renderID;
    }

    private static final Block[] neighbors = new Block[6];
	private static final boolean[] shouldSidesBeRendered = new boolean[6];
	
	@Override
    public boolean renderWorldBlock(IBlockAccess world, int blockX, int blockY, int blockZ, Block block, int modelId, RenderBlocks renderer) {
        if (!(block instanceof HEWater)) {
            return false;
        }

        neighbors[0] = world.getBlock(blockX - 1, blockY, blockZ);
        neighbors[1] = world.getBlock(blockX + 1, blockY, blockZ);
        neighbors[2] = world.getBlock(blockX, blockY - 1, blockZ);
        neighbors[3] = world.getBlock(blockX, blockY + 1, blockZ);
        neighbors[4] = world.getBlock(blockX, blockY, blockZ - 1);
        neighbors[5] = world.getBlock(blockX, blockY, blockZ + 1);

        shouldSidesBeRendered[0] = !neighbors[0].isOpaqueCube() && neighbors[0] != block;
        shouldSidesBeRendered[1] = !neighbors[1].isOpaqueCube() && neighbors[1] != block;
        shouldSidesBeRendered[2] = neighbors[2] != block;
        shouldSidesBeRendered[3] = !neighbors[3].isOpaqueCube() && neighbors[3] != block;
        shouldSidesBeRendered[4] = !neighbors[4].isOpaqueCube() && neighbors[4] != block;
        shouldSidesBeRendered[5] = !neighbors[5].isOpaqueCube() && neighbors[5] != block;

        int worldColorModifier = block.colorMultiplier(world, blockX, blockY, blockZ);
        HETessalator.addBlock(blockX, blockY, blockZ, ((HEWater)block).getWaterId(), worldColorModifier, shouldSidesBeRendered);

        return false;
    }
}
