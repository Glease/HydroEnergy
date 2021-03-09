package com.sinthoras.hydroenergy.blocks;

import com.sinthoras.hydroenergy.HE;

import com.sinthoras.hydroenergy.server.HEServer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class HEControllerTileEntity extends TileEntity {
	private int id = -1;
	
	private static float min = 2.3f;
	private static float max = 4.95f;
	private static int dir = 1;
	
	private boolean markDirtyHack = false;
	
	public HEControllerTileEntity() {
		super();
	}
	
	@Override
	public void validate() {
		if(id == -1 && !this.worldObj.isRemote) {
			id = HEServer.instance.reserveControllerId(yCoord);
			HE.LOG.info("New controller " + id);
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
	
	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (id != -1 && !this.worldObj.isRemote) {
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

        //TODO save stuff
        compound.setInteger("ID", id);
        HE.LOG.info("SAVED");
	}
	
	@Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        
        id = compound.getInteger("ID");
        HE.LOG.info("LOADED");
	}
	
	public void onRemoveTileEntity() {
		if(!this.worldObj.isRemote) {
			HEServer.instance.onBreakController(id);
		}
	}
}
