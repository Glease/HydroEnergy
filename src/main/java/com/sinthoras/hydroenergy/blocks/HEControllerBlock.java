package com.sinthoras.hydroenergy.blocks;

import com.sinthoras.hydroenergy.HE;

import com.sinthoras.hydroenergy.client.gui.HEDamGui;
import com.sinthoras.hydroenergy.server.HEBlockQueue;
import com.sinthoras.hydroenergy.server.HEServer;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class HEControllerBlock extends BlockContainer {
	
	
	public HEControllerBlock() {
		super(Material.iron);
		setHardness(100.0F);
		setLightOpacity(15);
		setBlockName("controller");
		setBlockTextureName(HE.MODID + ":" + HE.damTextureName);
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
	public boolean onBlockActivated(World world, int blockX, int blockY, int blockZ, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
		TileEntity tileEntity = world.getTileEntity(blockX, blockY, blockZ);
		if(tileEntity instanceof HEControllerTileEntity) {
			HEControllerTileEntity controllerTileEntity = (HEControllerTileEntity) tileEntity;
			if(!player.isSneaking()) {
				if (world.isRemote) {
					Minecraft.getMinecraft().displayGuiScreen(new HEDamGui(player.inventory, controllerTileEntity.getWaterId(), controllerTileEntity));
				} else {
					FMLNetworkHandler.openGui(player, HE.MODID, HEControllerTileEntity.guiId, controllerTileEntity.getWorldObj(), controllerTileEntity.xCoord, controllerTileEntity.yCoord, controllerTileEntity.zCoord);
					return true;
				}
			}
		}
		return false;
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
