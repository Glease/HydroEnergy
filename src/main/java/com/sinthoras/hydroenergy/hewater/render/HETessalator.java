package com.sinthoras.hydroenergy.hewater.render;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.proxy.HECommonProxy;
import net.minecraft.client.renderer.RenderList;

public class HETessalator {

    public HETessalator() {

    }

    public static final HETessalator instance = new HETessalator();

    public void onChunkUnload(int chunkX, int chunkZ) {
        // remove all subchunks
    }

    public void onChunkLoad(int chunkX, int chunkZ) {
        // Add all subchunks
    }

    public void onPreRender(int x, int y, int z) {
        // block coords!
        // glBeginList
    }

    public void onPostRender(int x, int y, int z) {
        // block coords!
        // glEndList
    }

    public void addBlock(int x, int y, int z, int waterId, boolean[] shouldSideBeRendered) {

    }

    private int counter = 0;

    public void renderSubchunk(RenderList list, int renderPass, double partialTick) {
        if(renderPass == HECommonProxy.blockWaterStill.getRenderBlockPass()) {
            counter++;
            HE.LOG.info(partialTick);
            int chunkX = list.renderChunkX;
            int chunkY = list.renderChunkY;
            int chunkZ = list.renderChunkZ;
        }
    }
}
