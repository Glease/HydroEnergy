package com.sinthoras.hydroenergy.controller;

import com.sinthoras.hydroenergy.HE;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class HEControllerBlock extends BlockContainer {
	
	
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
	
	@Override
	public boolean canPlaceBlockAt(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_) {
		return HEDamsServer.instance.canControllerBePlaced() && super.canPlaceBlockAt(p_149742_1_, p_149742_2_, p_149742_3_, p_149742_4_);
    }
	
	@Override
	public void breakBlock(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_) {
		((HEControllerTileEntity)(p_149749_1_.getTileEntity(p_149749_2_, p_149749_3_, p_149749_4_))).onRemoveTileEntity();
		super.breakBlock(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
    }
	
	@Override
	public void onBlockAdded(World world, int blockX, int blockY, int blockZ) {
		world.setBlock(blockX + 1, blockY, blockZ, HE.waterBlocks[0]);
	}
}
