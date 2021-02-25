package com.sinthoras.hydroenergy.hewater.render;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.proxy.HECommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderList;
import net.minecraft.world.World;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class HETessalator {

    private HashMap<Long, HESubChunk[]> chunks = new HashMap<Long, HESubChunk[]>();

    public HETessalator() {

    }

    public static final HETessalator instance = new HETessalator();

    public void onChunkUnload(int chunkX, int chunkZ) {
        long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
        chunks.remove(key);
    }

    public void onChunkLoad(int chunkX, int chunkZ) {
        long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
        chunks.put(key, new HESubChunk[] {
                new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk(),
                new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk(),
                new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk(),
                new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk()
        });
    }

    public void onPreRender(int x, int y, int z) {
        int chunkX = HEUtil.bucketInt16(x);
        int chunkY = HEUtil.bucketInt16(y);
        int chunkZ = HEUtil.bucketInt16(z);
        long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
        (chunks.get(key)[chunkY]).onPreRender();
    }

    public void onPostRender(World world, int x, int y, int z) {
        int chunkX = HEUtil.bucketInt16(x);
        int chunkY = HEUtil.bucketInt16(y);
        int chunkZ = HEUtil.bucketInt16(z);
        long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
        (chunks.get(key)[chunkY]).onPostRender(world, chunkX, chunkY, chunkZ);
    }

    public void addBlock(int x, int y, int z, int waterId, boolean[] shouldSideBeRendered) {
        int chunkX = HEUtil.bucketInt16(x);
        int chunkY = HEUtil.bucketInt16(y);
        int chunkZ = HEUtil.bucketInt16(z);
        long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
        (chunks.get(key)[chunkY]).addBlock(x, y, z, waterId, shouldSideBeRendered);
    }

    public void renderSubchunk(RenderList list, int renderPass, double partialTick) {
        if(renderPass == HECommonProxy.blockWaterStill.getRenderBlockPass()) {
            int chunkX = list.renderChunkX;
            int chunkY = list.renderChunkY;
            int chunkZ = list.renderChunkZ;
            long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
            if(chunks.containsKey(key))
                (chunks.get(key)[chunkY]).render(partialTick);
        }
    }
}
