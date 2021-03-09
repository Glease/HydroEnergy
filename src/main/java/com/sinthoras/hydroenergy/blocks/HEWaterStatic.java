package com.sinthoras.hydroenergy.blocks;

import com.sinthoras.hydroenergy.server.HEBlockQueue;
import com.sinthoras.hydroenergy.server.HEServer;
import com.sinthoras.hydroenergy.client.renderer.HEWaterRenderer;

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
		spread(world, blockX, blockY, blockZ);
    }

	@Override
	public void onBlockAdded(World world, int blockX, int blockY, int blockZ) {
		spread(world, blockX, blockY, blockZ);
	}
	
	private void spread(World world, int blockX, int blockY, int blockZ) {
		final int waterId = getId();
		if(HEServer.instance.canSpread(waterId)) {
			if (blockY < HEServer.instance.getWaterLimitUp(waterId)) {
				if (canFlowInto(world, blockX, blockY + 1, blockZ)) {
					HEBlockQueue.enqueueBlock(HEBlockQueue.Mode.Add, blockX, blockY + 1, blockZ, waterId);
				}
			}

			if(blockY > HEServer.instance.getWaterLimitDown(waterId)) {
				if (canFlowInto(world, blockX, blockY - 1, blockZ)) {
					HEBlockQueue.enqueueBlock(HEBlockQueue.Mode.Add, blockX, blockY - 1, blockZ, waterId);
				}
			}

			if(blockX < HEServer.instance.getWaterLimitEast(waterId)) {
				if (canFlowInto(world, blockX + 1, blockY, blockZ)) {
					HEBlockQueue.enqueueBlock(HEBlockQueue.Mode.Add, blockX + 1, blockY, blockZ, waterId);
				}
			}

			if(blockX > HEServer.instance.getWaterLimitWest(waterId)) {
				if (canFlowInto(world, blockX - 1, blockY, blockZ)) {
					HEBlockQueue.enqueueBlock(HEBlockQueue.Mode.Add, blockX - 1, blockY, blockZ, waterId);
				}
			}

			if(blockZ < HEServer.instance.getWaterLimitSouth(waterId)) {
				if (canFlowInto(world, blockX, blockY, blockZ + 1)) {
					HEBlockQueue.enqueueBlock(HEBlockQueue.Mode.Add, blockX, blockY, blockZ + 1, waterId);
				}
			}

			if(blockZ > HEServer.instance.getWaterLimitNorth(waterId)) {
				if (canFlowInto(world, blockX, blockY, blockZ - 1)) {
					HEBlockQueue.enqueueBlock(HEBlockQueue.Mode.Add, blockX, blockY, blockZ - 1, waterId);
				}
			}
		}
		else if(!HEServer.instance.canSpread(waterId)
					|| blockX > HEServer.instance.getWaterLimitEast(waterId)
					|| blockX < HEServer.instance.getWaterLimitWest(waterId)
					|| blockY > HEServer.instance.getWaterLimitUp(waterId)
					|| blockY < HEServer.instance.getWaterLimitDown(waterId)
					|| blockZ > HEServer.instance.getWaterLimitSouth(waterId)
					|| blockZ < HEServer.instance.getWaterLimitNorth(waterId)) {
			HEBlockQueue.enqueueBlock(HEBlockQueue.Mode.Remove, blockX, blockY, blockZ, waterId);
		}
	}
}
