package com.sinthoras.hydroenergy.network.container;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.blocks.HEControllerTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;

import java.nio.ByteBuffer;
import java.util.List;

public class HEControllerContainer extends Container {

    private long energyStored;
    private long energyCapacity;
    private long energyPerTickIn;
    private long energyPerTickOut;
    private HEControllerTileEntity controllerTileEntity;

    private long timestamp = 0;
    private ByteBuffer buffer = ByteBuffer.allocate(4 * HE.LONG_SIZE);

    private class Buffer {

        public static final int energyStoredOffset = 0 * HE.LONG_SIZE;
        public static final int energyCapacityOffset = 1 * HE.LONG_SIZE;
        public static final int energyPerTickInOffset = 2 * HE.LONG_SIZE;
        public static final int getEnergyPerTickOutOffset = 3 * HE.LONG_SIZE;
    }

    public HEControllerContainer(HEControllerTileEntity controllerTileEntity) {
        this.controllerTileEntity = controllerTileEntity;
        energyStored = controllerTileEntity.getEnergyStored();
        energyCapacity = controllerTileEntity.getEnergyCapacity();
        energyPerTickIn = controllerTileEntity.getEnergyPerTickIn();
        energyPerTickOut = controllerTileEntity.getEnergyPerTickOut();
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    // addClientHandleToList
    public void addCraftingToCrafters(ICrafting clientHandle)
    {
        super.addCraftingToCrafters(clientHandle);

        buffer.putLong(Buffer.energyStoredOffset, energyStored);
        sendStateUpdate(clientHandle, Buffer.energyStoredOffset);

        buffer.putLong(Buffer.energyCapacityOffset, energyCapacity);
        sendStateUpdate(clientHandle, Buffer.energyCapacityOffset);

        buffer.putLong(Buffer.energyPerTickInOffset, energyPerTickIn);
        sendStateUpdate(clientHandle, Buffer.energyPerTickInOffset);

        buffer.putLong(Buffer.getEnergyPerTickOutOffset, energyPerTickOut);
        sendStateUpdate(clientHandle, Buffer.getEnergyPerTickOutOffset);
    }

    @Override
    public void detectAndSendChanges() {
        long currentTime = System.currentTimeMillis();
        if(!controllerTileEntity.getWorldObj().isRemote && currentTime > HE.controllerGuiUpdateDelay + timestamp) {
            boolean updateEnergyCapacity = false;
            long currentEnergyCapacity = controllerTileEntity.getEnergyCapacity();
            if (energyCapacity != currentEnergyCapacity) {
                energyCapacity = currentEnergyCapacity;
                buffer.putLong(Buffer.energyCapacityOffset, energyCapacity);
                updateEnergyCapacity = true;
            }

            boolean updateEnergyStored = false;
            long currentEnergyStored = controllerTileEntity.getEnergyStored();
            if (energyStored != currentEnergyStored) {
                energyStored = currentEnergyStored;
                buffer.putLong(Buffer.energyStoredOffset, energyStored);
                updateEnergyStored = true;
            }

            boolean updateEnergyPerTickIn = false;
            long currentEnergyPerTickIn = controllerTileEntity.getEnergyPerTickIn();
            if (energyPerTickIn != currentEnergyPerTickIn) {
                energyPerTickIn = currentEnergyPerTickIn;
                buffer.putLong(Buffer.energyPerTickInOffset, energyPerTickIn);
                updateEnergyPerTickIn = true;
            }

            boolean updateEnergyPerTickOut = false;
            long currentEnergyPerTickOut = controllerTileEntity.getEnergyPerTickOut();
            if (energyPerTickOut != currentEnergyPerTickOut) {
                energyPerTickOut = currentEnergyPerTickOut;
                buffer.putLong(Buffer.getEnergyPerTickOutOffset, energyPerTickOut);
                updateEnergyPerTickOut = true;
            }

            for (ICrafting clientHandle : (List<ICrafting>) crafters) {
                if (updateEnergyCapacity) {
                    sendStateUpdate(clientHandle, Buffer.energyCapacityOffset);
                }

                if (updateEnergyStored) {
                    sendStateUpdate(clientHandle, Buffer.energyStoredOffset);
                }

                if (updateEnergyPerTickIn) {
                    sendStateUpdate(clientHandle, Buffer.energyStoredOffset);
                }

                if (updateEnergyPerTickOut) {
                    sendStateUpdate(clientHandle, Buffer.energyStoredOffset);
                }
            }

            if(updateEnergyCapacity || updateEnergyStored || updateEnergyPerTickIn ||updateEnergyPerTickOut) {
                timestamp = currentTime;
            }
        }
    }

    private void sendStateUpdate(ICrafting clientHandle, int bufferOffset) {
        for(int i=0;i<HE.LONG_SIZE;i++) {
            int index = bufferOffset + i;
            clientHandle.sendProgressBarUpdate(this, index, buffer.get(index));
        }
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int index, int value)
    {
        buffer.put(index, (byte)value);
    }

    @SideOnly(Side.CLIENT)
    public long getEnergyStored() {
        return buffer.getLong(Buffer.energyStoredOffset);
    }

    @SideOnly(Side.CLIENT)
    public long getEnergyCapacity() {
        return buffer.getLong(Buffer.energyCapacityOffset);
    }

    @SideOnly(Side.CLIENT)
    public long getEnergyPerTickIn() {
        return buffer.getLong(Buffer.energyPerTickInOffset);
    }

    @SideOnly(Side.CLIENT)
    public long getEnergyPerTickOut() {
        return buffer.getLong(Buffer.getEnergyPerTickOutOffset);
    }

    @SideOnly(Side.CLIENT)
    public int getWaterId() {
        return controllerTileEntity.getWaterId();
    }
}
