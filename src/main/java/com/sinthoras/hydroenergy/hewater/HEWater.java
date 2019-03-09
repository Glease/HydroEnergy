package com.sinthoras.hydroenergy.hewater;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class HEWater extends BlockFluidBase {

	public HEWater() {
		super(FluidRegistry.WATER, Material.water);
		setHardness(100.0F);
		setLightOpacity(3);
		setBlockName("water");
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
		return 1;
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
        return HEWaterFakeRenderer.instance.getRenderId();
    }
	
	@Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
    {
		Block block;
		switch(side)
		{
		case 1:
			block = world.getBlock(x, y, z);
			if(block == this)
				return false;
			return true;
		case 6:
			return false;
		default:
			return false; //TODO handle flowing water and glass sides
		}
//		block = world.getBlock(x, y, z);
//        if (block != this)
//        {
//            return !block.isOpaqueCube();
//        }
//        return block.getMaterial() == this.getMaterial() ? false : super.shouldSideBeRendered(world, x, y, z, side);
    }

}
