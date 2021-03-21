package com.sinthoras.hydroenergy.client.gui;

import com.sinthoras.hydroenergy.blocks.HEControllerTileEntity;
import com.sinthoras.hydroenergy.network.container.HEControllerContainer;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class HEGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer entityPlayer, World world, int blockX, int blockY, int blockZ) {
        if(id == 0) {
            TileEntity tileEntity = world.getTileEntity(blockX, blockY, blockZ);
            if (tileEntity instanceof HEControllerTileEntity) {
                return new HEControllerContainer((HEControllerTileEntity)tileEntity);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer entityPlayer, World world, int blockX, int blockY, int blockZ) {
        if(world.isRemote) {
            if(id == 0) {
                TileEntity tileEntity = world.getTileEntity(blockX, blockY, blockZ);
                if (tileEntity instanceof HEControllerTileEntity) {
                    return new HEControllerGui(new HEControllerContainer((HEControllerTileEntity)tileEntity));
                }
            }
        }
        else {
            return getServerGuiElement(id, entityPlayer, world, blockX, blockY, blockZ);
        }
        return null;
    }
}
