package com.sinthoras.hydroenergy.blocks;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.client.HEClient;
import com.sinthoras.hydroenergy.server.HEServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import gregtech.api.enums.GT_Values;
import gregtech.api.interfaces.tileentity.IEnergyConnected;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.net.GT_Packet_Block_Event;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

public class HEControllerTileEntity extends TileEntity implements IEnergyConnected {

	private long energyStored = 0;
	private long energyCapacity = 10000000;
	private long voltage = 512;
	private int ouputAmperage = 1;
	private long energyPerTickIn = 0;
	private long energyPerTickOut = 0;

	private int energyPerTickInValid = 0;

	public void onRemoveTileEntity() {
		if(!this.worldObj.isRemote) {
			HEServer.instance.onBreakController(getWaterId());
		}
	}

	public long getEnergyStored() {
		return energyStored;
	}

	public long getEnergyCapacity() {
		return energyCapacity;
	}

	public long getEnergyPerTickIn() {
		if(energyPerTickInValid < 2) {
			return energyPerTickIn;
		}
		else {
			return 0;
		}
	}

	public long getEnergyPerTickOut() {
		return energyPerTickOut;
	}

	@Override
	public long injectEnergyUnits(byte side, long voltage, long amperage) {
		if(voltage <= this.voltage) {
			long usedAmperage = Math.min((energyCapacity - energyStored) / voltage, amperage);
			energyPerTickIn = usedAmperage * voltage;
			energyPerTickInValid = 0;
			energyStored += energyPerTickIn;
			return usedAmperage;
		}
		else {
			return 0;
		}
	}

	@Override
	public boolean inputEnergyFrom(byte side) {
		return true;
	}

	@Override
	public boolean outputsEnergyTo(byte side) {
		return true;
	}

	public void provideEnergy() {
		if (!this.worldObj.isRemote && gtTileEntitiesAround() && energyStored >= voltage * ouputAmperage) {
			long usedAmperage = IEnergyConnected.Util.emitEnergyToNetwork(voltage, ouputAmperage, this);
			energyPerTickOut = voltage * usedAmperage;
			energyStored -= energyPerTickOut;
		}
		else {
			energyPerTickOut = 0;
		}
	}

	private boolean gtTileEntitiesAround() {
		for (int side = 0; side < 6; side++) {
			if (getIGregTechTileEntityAtSide((byte)side) != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public byte getColorization() {
		return 0;
	}

	@Override
	public byte setColorization(byte color) {
		return 0;
	}

	private static class Tags {
		public static final String waterId = "waId";
		public static final String energyStored = "stor";
		public static final String energyCapacity = "capa";
	}

	public static final int guiId = 0;

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
		provideEnergy();
		energyPerTickInValid++;
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public int getXCoord() {
		return xCoord;
	}

	@Override
	public short getYCoord() {
		return (short)yCoord;
	}

	@Override
	public int getZCoord() {
		return zCoord;
	}

	@Override
	public boolean isServerSide() {
		return !isClientSide();
	}

	@Override
	public boolean isClientSide() {
		return FMLCommonHandler.instance().getSide() == Side.CLIENT;
	}

	@Override
	public int getRandomNumber(int range) {
		return worldObj.rand.nextInt(range);
	}

	@Override
	public TileEntity getTileEntity(int blockX, int blockY, int blockZ) {
		return worldObj.getTileEntity(blockX, blockY, blockZ);
	}

	@Override
	public TileEntity getTileEntityOffset(int blockX, int blockY, int blockZ) {
		return getTileEntity(blockX + this.xCoord, blockY + this.yCoord, blockZ + this.zCoord);
	}

	@Override
	public TileEntity getTileEntityAtSide(byte side) {
		if ((side < 0) || (side >= 6)) {
			return null;
		}
		int blockX = getOffsetX(side, 1);
		int blockY = getOffsetY(side, 1);
		int blockZ = getOffsetZ(side, 1);
		return getTileEntity(blockX, blockY, blockZ);
	}

	@Override
	public TileEntity getTileEntityAtSideAndDistance(byte side, int distance) {
		if (distance == 1) {
			return getTileEntityAtSide(side);
		}
		return getTileEntity(getOffsetX(side, distance), getOffsetY(side, distance), getOffsetZ(side, distance));
	}

	@Override
	public IInventory getIInventory(int blockX, int blockY, int blockZ) {
		return null;
	}

	@Override
	public IInventory getIInventoryOffset(int blockX, int blockY, int blockZ) {
		return getIInventory(blockX + this.xCoord, blockY + this.yCoord, blockZ + this.zCoord);
	}

	@Override
	public IInventory getIInventoryAtSide(byte side) {
		if ((side < 0) || (side >= 6)) {
			return null;
		}
		int blockX = getOffsetX(side, 1);
		int blockY = getOffsetY(side, 1);
		int blockZ = getOffsetZ(side, 1);
		return getIInventory(blockX, blockY, blockZ);
	}

	@Override
	public IInventory getIInventoryAtSideAndDistance(byte side, int distance) {
		if (distance == 1) {
			return getIInventoryAtSide(side);
		}
		return getIInventory(getOffsetX(side, distance), getOffsetY(side, distance), getOffsetZ(side, distance));
	}

	@Override
	public IFluidHandler getITankContainer(int blockX, int blockY, int blockZ) {
		return null;
	}

	@Override
	public IFluidHandler getITankContainerOffset(int blockX, int blockY, int blockZ) {
		return getITankContainer(blockX + this.xCoord, blockY + this.yCoord, blockZ + this.zCoord);
	}

	@Override
	public IFluidHandler getITankContainerAtSide(byte side) {
		if ((side < 0) || (side >= 6)) {
			return null;
		}
		int blockX = getOffsetX(side, 1);
		int blockY = getOffsetY(side, 1);
		int blockZ = getOffsetZ(side, 1);
		return getITankContainer(blockX, blockY, blockZ);
	}

	@Override
	public IFluidHandler getITankContainerAtSideAndDistance(byte side, int distance) {
		if (distance == 1) {
			return getITankContainerAtSide(side);
		}
		return getITankContainer(getOffsetX(side, distance), getOffsetY(side, distance), getOffsetZ(side, distance));
	}

	@Override
	public IGregTechTileEntity getIGregTechTileEntity(int blockX, int blockY, int blockZ) {
		TileEntity tileEntity = getTileEntity(blockX, blockY, blockZ);
		if ((tileEntity instanceof IGregTechTileEntity)) {
			return (IGregTechTileEntity) tileEntity;
		}
		return null;
	}

	@Override
	public IGregTechTileEntity getIGregTechTileEntityOffset(int blockX, int blockY, int blockZ) {
		return getIGregTechTileEntity(blockX + this.xCoord, blockY + this.yCoord, blockZ + this.zCoord);
	}

	@Override
	public IGregTechTileEntity getIGregTechTileEntityAtSide(byte side) {
		if ((side < 0) || (side >= 6)) {
			return null;
		}
		int blockX = getOffsetX(side, 1);
		int blockY = getOffsetY(side, 1);
		int blockZ = getOffsetZ(side, 1);
		return getIGregTechTileEntity(blockX, blockY, blockZ);
	}

	@Override
	public IGregTechTileEntity getIGregTechTileEntityAtSideAndDistance(byte side, int distance) {
		if (distance == 1) {
			return getIGregTechTileEntityAtSide(side);
		}
		return getIGregTechTileEntity(getOffsetX(side, distance), getOffsetY(side, distance), getOffsetZ(side, distance));
	}

	@Override
	public Block getBlock(int blockX, int blockY, int blockZ) {
		return worldObj.getBlock(blockX, blockY, blockZ);
	}

	@Override
	public Block getBlockOffset(int blockX, int blockY, int blockZ) {
		return getBlock(xCoord + blockX, yCoord + blockY, zCoord + blockZ);
	}

	@Override
	public Block getBlockAtSide(byte side) {
		if ((side < 0) || (side >= 6)) {
			return null;
		}
		int blockX = getOffsetX(side, 1);
		int blockY = getOffsetY(side, 1);
		int blockZ = getOffsetZ(side, 1);
		return getBlock(blockX, blockY, blockZ);
	}

	@Override
	public Block getBlockAtSideAndDistance(byte side, int distance) {
		if (distance == 1) {
			return getBlockAtSide(side);
		}
		return getBlockOffset(getOffsetX(side, distance), getOffsetY(side, distance), getOffsetZ(side, distance));
	}

	@Override
	public byte getMetaID(int blockX, int blockY, int blockZ) {
		return (byte)worldObj.getBlockMetadata(blockX, blockY, blockZ);
	}

	@Override
	public byte getMetaIDOffset(int blockX, int blockY, int blockZ) {
		return getMetaID(xCoord + blockX, yCoord + blockY, zCoord + blockZ);
	}

	@Override
	public byte getMetaIDAtSide(byte side) {
		if ((side < 0) || (side >= 6)) {
			return 0;
		}
		int blockX = getOffsetX(side, 1);
		int blockY = getOffsetY(side, 1);
		int blockZ = getOffsetZ(side, 1);
		return getMetaID(blockX, blockY, blockZ);
	}

	@Override
	public byte getMetaIDAtSideAndDistance(byte side, int distance) {
		if (distance == 1) {
			return getMetaIDAtSide(side);
		}
		return getMetaID(getOffsetX(side, distance), getOffsetY(side, distance), getOffsetZ(side, distance));
	}

	@Override
	public byte getLightLevel(int blockX, int blockY, int blockZ) {
		return (byte)(worldObj.getLightBrightness(blockX, blockY, blockZ) * 15);
	}

	@Override
	public byte getLightLevelOffset(int blockX, int blockY, int blockZ) {
		return getLightLevel(xCoord + blockX, yCoord + blockY, zCoord + blockZ);
	}

	@Override
	public byte getLightLevelAtSide(byte side) {
		if ((side < 0) || (side >= 6)) {
			return 0;
		}
		int blockX = getOffsetX(side, 1);
		int blockY = getOffsetY(side, 1);
		int blockZ = getOffsetZ(side, 1);
		return getLightLevel(blockX, blockY, blockZ);
	}

	@Override
	public byte getLightLevelAtSideAndDistance(byte side, int distance) {
		if (distance == 1) {
			return getMetaIDAtSide(side);
		}
		return getLightLevel(getOffsetX(side, distance), getOffsetY(side, distance), getOffsetZ(side, distance));
	}

	@Override
	public boolean getOpacity(int blockX, int blockY, int blockZ) {
		return worldObj.getBlock(blockX, blockY, blockZ).isOpaqueCube();
	}

	@Override
	public boolean getOpacityOffset(int blockX, int blockY, int blockZ) {
		return getOpacity(xCoord + blockX, yCoord + blockY, zCoord + blockZ);
	}

	@Override
	public boolean getOpacityAtSide(byte side) {
		if ((side < 0) || (side >= 6)) {
			return false;
		}
		int blockX = getOffsetX(side, 1);
		int blockY = getOffsetY(side, 1);
		int blockZ = getOffsetZ(side, 1);
		return getOpacity(blockX, blockY, blockZ);
	}

	@Override
	public boolean getOpacityAtSideAndDistance(byte side, int distance) {
		if (distance == 1) {
			return getSkyAtSide(side);
		}
		return getOpacity(getOffsetX(side, distance), getOffsetY(side, distance), getOffsetZ(side, distance));
	}

	@Override
	public boolean getSky(int blockX, int blockY, int blockZ) {
		return !worldObj.canBlockSeeTheSky(blockX, blockY, blockZ);
	}

	@Override
	public boolean getSkyOffset(int blockX, int blockY, int blockZ) {
		return getSky(xCoord + blockX, yCoord + blockY, zCoord + blockZ);
	}

	@Override
	public boolean getSkyAtSide(byte side) {
		if ((side < 0) || (side >= 6)) {
			return false;
		}
		int blockX = getOffsetX(side, 1);
		int blockY = getOffsetY(side, 1);
		int blockZ = getOffsetZ(side, 1);
		return getSky(blockX, blockY, blockZ);
	}

	@Override
	public boolean getSkyAtSideAndDistance(byte side, int distance) {
		if (distance == 1) {
			return getSkyAtSide(side);
		}
		return getSky(getOffsetX(side, distance), getOffsetY(side, distance), getOffsetZ(side, distance));
	}

	@Override
	public boolean getAir(int blockX, int blockY, int blockZ) {
		return worldObj.isAirBlock(blockX, blockY, blockZ);
	}

	@Override
	public boolean getAirOffset(int blockX, int blockY, int blockZ) {
		return getAir(xCoord + blockX, yCoord + blockY, zCoord + blockZ);
	}

	@Override
	public boolean getAirAtSide(byte side) {
		if ((side < 0) || (side >= 6)) {
			return false;
		}
		int blockX = getOffsetX(side, 1);
		int blockY = getOffsetY(side, 1);
		int blockZ = getOffsetZ(side, 1);
		return getAir(blockX, blockY, blockZ);
	}

	@Override
	public boolean getAirAtSideAndDistance(byte side, int distance) {
		if (distance == 1) {
			return getAirAtSide(side);
		}
		return getAir(getOffsetX(side, distance), getOffsetY(side, distance), getOffsetZ(side, distance));
	}

	@Override
	public BiomeGenBase getBiome() {
		return worldObj.getBiomeGenForCoords(xCoord, zCoord);
	}

	@Override
	public BiomeGenBase getBiome(int blockX, int blockZ) {
		return worldObj.getBiomeGenForCoords(blockX, blockZ);
	}

	@Override
	public int getOffsetX(byte side, int multiplier) {
		return xCoord + ForgeDirection.getOrientation(side).offsetX * multiplier;
	}

	@Override
	public short getOffsetY(byte side, int multiplier) {
		return (short) (yCoord + ForgeDirection.getOrientation(side).offsetY * multiplier);
	}

	@Override
	public int getOffsetZ(byte side, int multiplier) {
		return zCoord + ForgeDirection.getOrientation(side).offsetZ * multiplier;
	}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public void sendBlockEvent(byte íd, byte value) {
		GT_Values.NW.sendPacketToAllPlayersInRange(worldObj, new GT_Packet_Block_Event(xCoord, (short)yCoord, zCoord, íd, value), xCoord, zCoord);
	}

	@Override
	public long getTimer() {
		return 0;
	}

	@Override
	public void setLightValue(byte lightValue) {
		HE.LOG.info("Why does anyone wants to set a light value here?");
	}

	@Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger(Tags.waterId, getWaterId());
        compound.setLong(Tags.energyStored, energyStored);
        compound.setLong(Tags.energyCapacity, energyCapacity);
	}
	
	@Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        
        waterId = compound.getInteger(Tags.waterId);
        energyStored = compound.getLong(Tags.energyStored);
        energyCapacity = compound.getLong(Tags.energyCapacity);
	}

	@Override
	public boolean isInvalidTileEntity() {
		return false;
	}

	@Override
	public boolean openGUI(EntityPlayer entityPlayer, int id) {
		return false;
	}

	@Override
	public boolean openGUI(EntityPlayer entityPlayer) {
		return false;
	}
}
