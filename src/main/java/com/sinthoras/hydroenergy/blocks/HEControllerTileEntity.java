package com.sinthoras.hydroenergy.blocks;

import com.sinthoras.hydroenergy.client.HEClient;
import com.sinthoras.hydroenergy.server.HEServer;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class HEControllerTileEntity extends TileEntity {

	public static final int guiId = 0;

	public static class Tags {
		public static final String waterId = "waId";
	}

	private int waterId = -1;
	
	public HEControllerTileEntity() {
		super();
	}

    public int getWaterId() {
		if(waterId == -1) {
			if(FMLCommonHandler.instance().getSide().isClient()) {
				waterId = HEClient.getWaterId(xCoord, yCoord, zCoord);
			}
			else {
				waterId = HEServer.instance.getWaterId(xCoord, yCoord, zCoord);
			}
		}
		return waterId;
	}
	
	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (waterId != -1 && !this.worldObj.isRemote) {
			// TODO water stuff
			/*float level = HEDams.instance.getWaterLevel(id);
			if (level <= min)
				dir = 1;
			else if (level >= max)
				dir = -1;
			level += 0.01f * dir;
			HEDams.instance.updateWaterLevel(id, level);*/
		}
	}
	
	@Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger(Tags.waterId, getWaterId());
	}
	
	@Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        
        waterId = compound.getInteger(Tags.waterId);
	}
	
	public void onRemoveTileEntity() {
		if(!this.worldObj.isRemote) {
			HEServer.instance.onBreakController(getWaterId());
		}
	}

	public int getEnergyStored() {
		return 656432435;
	}

	public int getEnergyCapacity() {
		return 856432435;
	}

	public int getEnergyPerTickIn() {
		return 456;
	}

	public int getEnergyPerTickOut() {
		return 512;
	}
}
