package com.sinthoras.hydroenergy.hewater.render;

import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.proxy.HECommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import java.lang.reflect.Field;
import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class HETessalator {

    private static Field frustrumX;
    private static Field frustrumY;
    private static Field frustrumZ;
    static {
        try {
            frustrumX = Frustrum.class.getDeclaredField("xPosition");
            frustrumX.setAccessible(true);
            frustrumY = Frustrum.class.getDeclaredField("yPosition");
            frustrumY.setAccessible(true);
            frustrumZ = Frustrum.class.getDeclaredField("zPosition");
            frustrumZ.setAccessible(true);
        } catch(Exception e) {}
    }

    private HashMap<Long, HESubChunk[]> chunks = new HashMap<Long, HESubChunk[]>();

    public HETessalator() {

    }

    public static final HETessalator instance = new HETessalator();

    public synchronized void onChunkUnload(int chunkX, int chunkZ) {
        long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
        chunks.remove(key);
    }

    public synchronized void onChunkLoad(int chunkX, int chunkZ) {
        long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
        chunks.put(key, new HESubChunk[] {
                new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk(),
                new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk(),
                new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk(),
                new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk()
        });
    }

    public synchronized void onPreRender(int x, int y, int z) {
        int chunkX = HEUtil.bucketInt16(x);
        int chunkY = HEUtil.bucketInt16(y);
        int chunkZ = HEUtil.bucketInt16(z);
        long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
        chunks.get(key)[chunkY].onPreRender();
    }

    public synchronized void onPostRender(World world, int x, int y, int z) {
        int chunkX = HEUtil.bucketInt16(x);
        int chunkY = HEUtil.bucketInt16(y);
        int chunkZ = HEUtil.bucketInt16(z);
        long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
        chunks.get(key)[chunkY].onPostRender(world, chunkX, chunkY, chunkZ);
    }

    public synchronized void addBlock(int x, int y, int z, int waterId, boolean[] shouldSideBeRendered) {
        int chunkX = HEUtil.bucketInt16(x);
        int chunkY = HEUtil.bucketInt16(y);
        int chunkZ = HEUtil.bucketInt16(z);
        long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
        chunks.get(key)[chunkY].addBlock(x, y, z, waterId, shouldSideBeRendered);
    }

    public synchronized void render(ICamera frustrum, float partialTickTime) {
        if(MinecraftForgeClient.getRenderPass() == HECommonProxy.blockWaterStill.getRenderBlockPass()) {
            try {
                float x = (float)frustrumX.getDouble(frustrum);
                float y = (float)frustrumY.getDouble(frustrum);
                float z = (float)frustrumZ.getDouble(frustrum);
                HEProgram.calculateViewProjection(x, y, z);
            } catch(Exception e) {}
            for (long key : chunks.keySet()) {
                int chunkX = (int) (key >> 32);
                int chunkZ = (int) key;
                for (int chunkY = 0; chunkY < 16; chunkY++) {
                    int x = HEUtil.debucketInt16(chunkX);
                    int y = HEUtil.debucketInt16(chunkY);
                    int z = HEUtil.debucketInt16(chunkZ);
                    // TODO: compare with WorldRenderer:112
                    if (frustrum.isBoundingBoxInFrustum(AxisAlignedBB.getBoundingBox(x, y, z, x + 16, y + 16, z + 16))) {
                        chunks.get(key)[chunkY].render(partialTickTime);
                    }
                }
            }
        }
    }
}
