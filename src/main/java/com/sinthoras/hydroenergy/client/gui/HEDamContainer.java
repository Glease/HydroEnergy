package com.sinthoras.hydroenergy.client.gui;

import com.sinthoras.hydroenergy.blocks.HEControllerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class HEDamContainer extends Container {

    public HEDamContainer(InventoryPlayer inventoryPlayer, HEControllerTileEntity controllerTileEntity) {

    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }
}
