package com.sinthoras.hydroenergy.hewater;

import com.sinthoras.hydroenergy.controller.HEDams;
import com.sinthoras.hydroenergy.hewater.render.HEWaterRenderer;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class HEWaterStatic extends HEWater {

	public HEWaterStatic() {
		super();
		setHardness(100.0F);
		setLightOpacity(3);
		setBlockName("water");
		setBlockTextureName("minecraft:water_still");
		setTickRandomly(false);
		setCreativeTab(CreativeTabs.tabBlock);
		//HE.LOG.info("Water created " + getId());
	}
	
	// Will be overwritten by ByteBuddy!
	// Or metadata, or, or...
	@Override
	public int getId()
	{
		return 0;
	}
	
	@Override
    public int getRenderType()
    {
        return HEWaterRenderer.instance.getRenderId();
    }
	
	@Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
    {
		Block block = world.getBlock(x, y, z);
        if (block != this)
        {
            return !block.isOpaqueCube();
        }
        return false;
    }
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
		if(HEDams.instance.getRenderedWaterLevel(getId()) >= 0.0f)
		{
			spread(world, x, y, z);
		}
		else
		{
			// cleanup of spread water here
		}
    }

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		spread(world, x, y, z);
	}
	
	private void spread(World world, int x, int y, int z)
	{
		if(y < HEDams.instance.getWaterLimitUp(getId()))
		{
			if(canFlowInto(world, x, y+1, z))
			{
				HEBlockQueue.instance.addBlock(x, y+1, z, getId());
			}
		}
		
		if(canFlowInto(world, x, y-1, z))
		{
			HEBlockQueue.instance.addBlock(x, y-1, z, getId());
		}
		
		if(canFlowInto(world, x+1, y, z))
		{
			HEBlockQueue.instance.addBlock(x+1, y, z, getId());
		}
		
		if(canFlowInto(world, x-1, y, z))
		{
			HEBlockQueue.instance.addBlock(x-1, y, z, getId());
		}
		
		if(canFlowInto(world, x, y, z+1))
		{
			HEBlockQueue.instance.addBlock(x, y, z+1, getId());
		}
		
		if(canFlowInto(world, x, y, z-1))
		{
			HEBlockQueue.instance.addBlock(x, y, z-1, getId());
		}
	}
}
