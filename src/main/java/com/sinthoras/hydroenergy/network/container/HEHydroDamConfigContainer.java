package com.sinthoras.hydroenergy.network.container;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.blocks.HEHydroDamTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;

import java.nio.ByteBuffer;
import java.util.List;

public class HEHydroDamConfigContainer extends Container {

    private int waterId;
    private HEHydroDamTileEntity hydroDamTileEntity;

    private long timestamp = 0;
    private ByteBuffer buffer = ByteBuffer.allocate(1 * Integer.BYTES);

    private class Buffer {
        public static final int waterIdOffset = 0 * Integer.BYTES;
    }

    public HEHydroDamConfigContainer(HEHydroDamTileEntity hydroDamTileEntity) {
        this.hydroDamTileEntity = hydroDamTileEntity;
        waterId = hydroDamTileEntity.getWaterId();
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    public void addCraftingToCrafters(ICrafting clientHandle)
    {
        buffer.putInt(Buffer.waterIdOffset, waterId);
        sendStateUpdate(clientHandle, Buffer.waterIdOffset);

        super.addCraftingToCrafters(clientHandle);
    }

    @Override
    public void detectAndSendChanges() {
        long currentTime = System.currentTimeMillis();
        if(hydroDamTileEntity.getBaseMetaTileEntity().isServerSide() && currentTime > HE.controllerGuiUpdateDelay + timestamp) {
            boolean updateWaterId = false;
            int currentWaterId = hydroDamTileEntity.getWaterId();
            if (waterId != currentWaterId) {
                waterId = currentWaterId;
                buffer.putInt(Buffer.waterIdOffset, waterId);
                updateWaterId = true;
            }

            if (updateWaterId) {
                for (ICrafting clientHandle : (List<ICrafting>) crafters) {
                    sendStateUpdate(clientHandle, Buffer.waterIdOffset);
                }
            }

            if(updateWaterId) {
                timestamp = currentTime;
            }
        }
    }

    private void sendStateUpdate(ICrafting clientHandle, int bufferOffset) {
        for(int i=0;i<Integer.BYTES;i++) {
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
    public int getWaterId() {
        return buffer.getInt(Buffer.waterIdOffset);
    }
}
