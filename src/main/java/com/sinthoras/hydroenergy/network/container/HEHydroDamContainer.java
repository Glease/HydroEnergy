package com.sinthoras.hydroenergy.network.container;

import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_Container_MultiMachineEM;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class HEHydroDamContainer extends GT_Container_MultiMachineEM {
    public HEHydroDamContainer(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(aInventoryPlayer, aTileEntity, false, false, false);
    }
}
