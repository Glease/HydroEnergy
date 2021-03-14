package com.sinthoras.hydroenergy.blocks;

import com.sinthoras.hydroenergy.HE;

import com.sinthoras.hydroenergy.server.HEServer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class HEControllerTileEntity extends TileEntity {

	public static final int guiId = 0;

	public static class Tags {
		public static final String waterId = "waId";
	}

	private int waterId = -1;
	
	private static float min = 2.3f;
	private static float max = 4.95f;
	private static int dir = 1;
	
	private boolean markDirtyHack = false;
	
	public HEControllerTileEntity() {
		super();
	}
	
	@Override
	public void validate() {
		if(waterId == -1 && !this.worldObj.isRemote) {
			waterId = HEServer.instance.onPlacecontroller(xCoord, yCoord, zCoord);
			HE.LOG.info("New controller " + waterId);
			this.markDirtyHack = true;
			markDirty(); //triggers validate() again
		}
		else {
			if(this.markDirtyHack) {
				this.markDirtyHack = false;
			}
			this.tileEntityInvalid = false;
		}
    }

    public int getWaterId() {
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

        compound.setInteger(Tags.waterId, waterId);
	}
	
	@Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        
        waterId = compound.getInteger(Tags.waterId);
	}
	
	public void onRemoveTileEntity() {
		if(!this.worldObj.isRemote) {
			HEServer.instance.onBreakController(waterId);
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
