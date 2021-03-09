package com.sinthoras.hydroenergy.hewater;

import com.sinthoras.hydroenergy.controller.HEDams;
import com.sinthoras.hydroenergy.hewater.render.HEWaterRenderer;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class HEWaterStatic extends HEWater {

	public HEWaterStatic(int id) {
		super(id);
		setHardness(100.0F);
		setLightOpacity(3);
		setBlockName("water");
		setBlockTextureName("minecraft:water_still");
		setTickRandomly(false);
		setCreativeTab(CreativeTabs.tabBlock);
	}
	
	@Override
    public int getRenderType() {
        return HEWaterRenderer.instance.getRenderId();
    }
	
	@Override
    public boolean shouldSideBeRendered(IBlockAccess world, int blockX, int blockY, int blockZ, int side) {
		Block block = world.getBlock(blockX, blockY, blockZ);
        if (block != this) {
            return !block.isOpaqueCube();
        }
        return false;
    }
	
	@Override
	public void onNeighborBlockChange(World world, int blockX, int blockY, int blockZ, Block block) {
		if(HEDams.instance.getRenderedWaterLevel(getId()) >= 0.0f) {
			spread(world, blockX, blockY, blockZ);
		}
		else {
			// cleanup of spread water here
		}
    }

	@Override
	public void onBlockAdded(World world, int blockX, int blockY, int blockZ) {
		spread(world, blockX, blockY, blockZ);
	}
	
	private void spread(World world, int blockX, int blockY, int blockZ) {
		if(blockY < HEDams.instance.getWaterLimitUp(getId())) {
			if(canFlowInto(world, blockX, blockY+1, blockZ)) {
				HEBlockQueue.addBlock(blockX, blockY+1, blockZ, getId());
			}
		}
		
		if(canFlowInto(world, blockX, blockY-1, blockZ)) {
			HEBlockQueue.addBlock(blockX, blockY-1, blockZ, getId());
		}
		
		if(canFlowInto(world, blockX+1, blockY, blockZ)) {
			HEBlockQueue.addBlock(blockX+1, blockY, blockZ, getId());
		}
		
		if(canFlowInto(world, blockX-1, blockY, blockZ)) {
			HEBlockQueue.addBlock(blockX-1, blockY, blockZ, getId());
		}
		
		if(canFlowInto(world, blockX, blockY, blockZ+1)) {
			HEBlockQueue.addBlock(blockX, blockY, blockZ+1, getId());
		}
		
		if(canFlowInto(world, blockX, blockY, blockZ-1)) {
			HEBlockQueue.addBlock(blockX, blockY, blockZ-1, getId());
		}
	}
}
