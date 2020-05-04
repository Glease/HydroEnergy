package com.sinthoras.hydroenergy.hewater;

import java.util.Random;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.controller.HEDams;
import com.sinthoras.hydroenergy.hewater.render.HEWaterRenderer;
import com.sinthoras.hydroenergy.proxy.HECommonProxy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

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
		spread(world, x, y, z);
    }

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		//world.setBlock(x, y, z, HECommonProxy.blockWaterStill);
		spread(world, x, y, z);
	}
	
	private void spread(World world, int x, int y, int z)
	{
		if(y < HEDams.instance.getWaterLimitUp(getId()))
		{
			if(canFlowInto(world, x, y+1, z))
			{
				//HEBlockQueue.instance.addBlock(x, y+1, z, getId());
			}
		}
		
		if(canFlowInto(world, x, y-1, z))
		{
			//HEBlockQueue.instance.addBlock(x, y-1, z, getId());
		}
	}
}
