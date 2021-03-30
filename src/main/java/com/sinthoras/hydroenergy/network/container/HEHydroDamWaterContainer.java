package com.sinthoras.hydroenergy.network.container;

import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_Container_MultiMachineEM;
import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.blocks.HEHydroDamTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import java.nio.ByteBuffer;
import java.util.List;

public class HEHydroDamWaterContainer extends GT_Container_MultiMachineEM {

    private long waterStored;
    private long waterCapacity;
    private int waterPerTickIn;
    private int waterPerTickOut;
    private long timestamp = 0;

    private ByteBuffer buffer;

    private class Buffer {

        public static final int waterStoredOffset = 0 * Long.BYTES;
        public static final int waterCapacityOffset = 1 * Long.BYTES;
        public static final int waterPerTickInOffset = 2 * Long.BYTES;
        public static final int waterPerTickOutOffset = 2 * Long.BYTES + Integer.BYTES;
    }

    private static final int parameterIdOffset = 21;

    public HEHydroDamWaterContainer(InventoryPlayer inventoryPlayer, IGregTechTileEntity hydroDamMetaTileEntity) {
        super(inventoryPlayer, hydroDamMetaTileEntity, false, false, false);
    }

    @Override
    public void addCraftingToCrafters(ICrafting clientHandle)
    {
        buffer.putLong(Buffer.waterStoredOffset, waterStored);
        sendStateUpdate(clientHandle, Buffer.waterStoredOffset);

        buffer.putLong(Buffer.waterCapacityOffset, waterCapacity);
        sendStateUpdate(clientHandle, Buffer.waterCapacityOffset);

        buffer.putInt(Buffer.waterPerTickInOffset, waterPerTickIn);
        sendStateUpdate(clientHandle, Buffer.waterPerTickInOffset);

        buffer.putInt(Buffer.waterPerTickOutOffset, waterPerTickOut);
        sendStateUpdate(clientHandle, Buffer.waterPerTickOutOffset);

        super.addCraftingToCrafters(clientHandle);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if(buffer == null) {
            buffer = ByteBuffer.allocate(2 * Long.BYTES + 2 * Integer.BYTES);
        }
        HEHydroDamTileEntity hydroDamTileEntity = (HEHydroDamTileEntity)mTileEntity.getMetaTileEntity();
        long currentTime = System.currentTimeMillis();
        if(!mTileEntity.getWorld().isRemote && currentTime > HE.controllerGuiUpdateDelay + timestamp) {
            boolean updateWaterCapacity = false;
            long currentWaterCapacity = hydroDamTileEntity.getWaterCapacity();
            if (waterCapacity != currentWaterCapacity) {
                waterCapacity = currentWaterCapacity;
                buffer.putLong(Buffer.waterCapacityOffset, waterCapacity);
                updateWaterCapacity = true;
            }

            boolean updateWaterStored = false;
            long currentEnergyStored = hydroDamTileEntity.getWaterStored();
            if (waterStored != currentEnergyStored) {
                waterStored = currentEnergyStored;
                buffer.putLong(Buffer.waterStoredOffset, waterStored);
                updateWaterStored = true;
            }

            boolean updateWaterPerTickIn = false;
            int currentEnergyPerTickIn = hydroDamTileEntity.getWaterPerTickIn();
            if (waterPerTickIn != currentEnergyPerTickIn) {
                waterPerTickIn = currentEnergyPerTickIn;
                buffer.putInt(Buffer.waterPerTickInOffset, waterPerTickIn);
                updateWaterPerTickIn = true;
            }

            boolean updateWaterPerTickOut = false;
            int currentEnergyPerTickOut = hydroDamTileEntity.getWaterPerTickOut();
            if (waterPerTickOut != currentEnergyPerTickOut) {
                waterPerTickOut = currentEnergyPerTickOut;
                buffer.putInt(Buffer.waterPerTickOutOffset, waterPerTickOut);
                updateWaterPerTickOut = true;
            }

            for (ICrafting clientHandle : (List<ICrafting>) crafters) {
                if (updateWaterCapacity) {
                    sendStateUpdate(clientHandle, Buffer.waterCapacityOffset);
                }

                if (updateWaterStored) {
                    sendStateUpdate(clientHandle, Buffer.waterStoredOffset);
                }

                if (updateWaterPerTickIn) {
                    sendStateUpdate(clientHandle, Buffer.waterPerTickInOffset);
                }

                if (updateWaterPerTickOut) {
                    sendStateUpdate(clientHandle, Buffer.waterPerTickOutOffset);
                }
            }

            if(updateWaterCapacity || updateWaterStored || updateWaterPerTickIn ||updateWaterPerTickOut) {
                timestamp = currentTime;
            }
        }
    }


    private void sendStateUpdate(ICrafting clientHandle, int bufferOffset) {
        final int bytes;
        if(bufferOffset >= Buffer.waterPerTickInOffset) {
            bytes = Integer.BYTES;
        }
        else {
            bytes = Long.BYTES;
        }
        for (int i = 0; i < bytes; i++) {
            int index = bufferOffset + i;
            clientHandle.sendProgressBarUpdate(this, index + parameterIdOffset, buffer.get(index));
        }
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int index, int value)
    {
        super.updateProgressBar(index, value);
        index = index - parameterIdOffset;
        if(index >= 0 && index < buffer.capacity()) {
            buffer.put(index, (byte) value);
        }
    }

    @SideOnly(Side.CLIENT)
    public long getWaterStored() {
        return buffer.getLong(Buffer.waterStoredOffset);
    }

    @SideOnly(Side.CLIENT)
    public long getWaterCapacity() {
        return buffer.getLong(Buffer.waterCapacityOffset);
    }

    @SideOnly(Side.CLIENT)
    public int getWaterPerTickIn() {
        return buffer.getInt(Buffer.waterPerTickInOffset);
    }

    @SideOnly(Side.CLIENT)
    public int getWaterPerTickOut() {
        return buffer.getInt(Buffer.waterPerTickOutOffset);
    }
}
