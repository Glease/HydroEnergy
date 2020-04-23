package com.sinthoras.hydroenergy.hewater;

import com.sinthoras.hydroenergy.controller.HEController;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class HEWater extends BlockFluidBase {

	public HEWater() {
		super(FluidRegistry.WATER, Material.water);
		setHardness(100.0F);
		setLightOpacity(3);
		setBlockName("water");
		setBlockTextureName("minecraft:water_still");
		setTickRandomly(false);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public FluidStack drain(World world, int x, int y, int z, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canDrain(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public int getQuantaValue(IBlockAccess world, int x, int y, int z) {
		float val = getWaterLevel(world, x, y, z) - y;
		if (val < 0.0f)
			return 0;
		if (val >= 1.0f)
			return 8;
		return Math.round(val * 8);
	}

	@Override
	public boolean canCollideCheck(int meta, boolean fullHit) {
		return false;
	}

	@Override
	public int getMaxRenderHeightMeta() {
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
	public int getLightOpacity(IBlockAccess world, int x, int y, int z)
    {
		if (getWaterLevel(world, x, y, z) <= y)
        {
        	return 0;
        }
        return getLightOpacity();
    }
	
	public float getWaterLevel(IBlockAccess world, int x, int y, int z)
	{
		// return HEController.getWaterLevel(world.getBlockMetadata(x, y, z));
		//dev only!
		int meta = world.getBlockMetadata(x, y, z);
		meta = 0;
		return HEController.getWaterLevel(meta);
	}
}
