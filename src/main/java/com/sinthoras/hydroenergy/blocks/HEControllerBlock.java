package com.sinthoras.hydroenergy.blocks;

import com.sinthoras.hydroenergy.HE;

import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.server.HEBlockQueue;
import com.sinthoras.hydroenergy.server.HEServer;
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
	public TileEntity createNewTileEntity(World world, int metaData) {
		return new HEControllerTileEntity();
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, int blockX, int blockY, int blockZ) {
		if(!world.isRemote) {
			return HEServer.instance.canControllerBePlaced() && super.canPlaceBlockAt(world, blockX, blockY, blockZ);
		}
		else {
			// Is be overruled by server. The block appears briefly for the client and then vanishes
			return true;
		}
    }
	
	@Override
	public void breakBlock(World world, int blockX, int blockY, int blockZ, Block block, int metaData) {
		((HEControllerTileEntity)(world.getTileEntity(blockX, blockY, blockZ))).onRemoveTileEntity();
		super.breakBlock(world, blockX, blockY, blockZ, block, metaData);
    }
	
	@Override
	public void onBlockAdded(World world, int blockX, int blockY, int blockZ) {
		HEControllerTileEntity controllerEntity = (HEControllerTileEntity)world.getTileEntity(blockX, blockY, blockZ);
		HEBlockQueue.enqueueBlock(blockX + 1, blockY, blockZ, controllerEntity.getWaterId());
	}
}
