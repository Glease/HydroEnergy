package com.sinthoras.hydroenergy.controller;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class HEControllerBlock extends BlockContainer{
	
	
	public HEControllerBlock() {
		super(Material.iron);
		setHardness(100.0F);
		setLightOpacity(15);
		setBlockName("controller");
		//setBlockTextureName("");
		setTickRandomly(false);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return new HEControllerTileEntity();
	}
}
