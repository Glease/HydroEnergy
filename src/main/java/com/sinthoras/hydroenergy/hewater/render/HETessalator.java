package com.sinthoras.hydroenergy.hewater.render;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.proxy.HECommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.util.BitSet;
import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class HETessalator {

    private static FloatBuffer vboBuffer = GLAllocation.createDirectFloatBuffer(7 * (1 << 12));
    private static BitSet lightUpdateFlags = new BitSet(16*16*16);
    private static int numWaterBlocks = 0;

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


    public synchronized void onPreRender(World world, int x, int y, int z) {
        if(numWaterBlocks != 0) {
            lightUpdateFlags.clear();
            vboBuffer.clear();
            numWaterBlocks = 0;
        }

        // TODO: Light update (gotta link update flag and waterID to provide patch with correct waterLevel
        // Or post render? hmmmm
        /*
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        for (int linearCoord = lightUpdateFlags.nextSetBit(0); linearCoord != -1; linearCoord = lightUpdateFlags.nextSetBit(linearCoord + 1))
            patchBlockLight(chunkX, chunkY, chunkZ, linearCoord >> 8, (linearCoord >> 4) & 15, linearCoord & 15, waterLevel, world, chunk);
        */
    }

    public synchronized void onPostRender(World world, int x, int y, int z) {
        if(numWaterBlocks != 0) {
            int chunkX = HEUtil.bucketInt16(x);
            int chunkY = HEUtil.bucketInt16(y);
            int chunkZ = HEUtil.bucketInt16(z);
            long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
            HESubChunk subChunk = chunks.get(key)[chunkY];

            if (subChunk.vaoId == -1) {
                subChunk.vaoId = GL30.glGenVertexArrays();
                subChunk.vboId = GL15.glGenBuffers();

                GL30.glBindVertexArray(subChunk.vaoId);

                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, subChunk.vboId);
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vboBuffer.capacity() * HE.FLOAT_SIZE, GL15.GL_STATIC_DRAW);

                GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 7 * HE.FLOAT_SIZE, 0 * HE.FLOAT_SIZE);
                GL20.glEnableVertexAttribArray(0);

                GL20.glVertexAttribPointer(1, 1, GL11.GL_FLOAT, false, 7 * HE.FLOAT_SIZE, 3 * HE.FLOAT_SIZE);
                GL20.glEnableVertexAttribArray(1);

                GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 7 * HE.FLOAT_SIZE, 4 * HE.FLOAT_SIZE);
                GL20.glEnableVertexAttribArray(2);

                GL30.glBindVertexArray(0);
            }

            vboBuffer.flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, subChunk.vboId);
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vboBuffer);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

            subChunk.numWaterBlocks = numWaterBlocks;
        }
    }

    public synchronized void addBlock(int x, int y, int z, int waterId, Vector3f worldColorModifier, boolean[] shouldSideBeRendered) {
        waterId <<= 6;
        for(int i=0;i<shouldSideBeRendered.length;i++)
            if(shouldSideBeRendered[i])
                waterId |= 1 << i;

        vboBuffer.put(x);
        vboBuffer.put(y);
        vboBuffer.put(z);
        vboBuffer.put(waterId);
        vboBuffer.put(worldColorModifier.x);
        vboBuffer.put(worldColorModifier.y);
        vboBuffer.put(worldColorModifier.z);
        numWaterBlocks++;

        // Light update stuff
        x = x & 15;
        y = y & 15;
        z = z & 15;
        lightUpdateFlags.set((x << 8) | (y << 4) | z);
    }

    public synchronized void render(ICamera frustrum, float partialTickTime) {
        if(MinecraftForgeClient.getRenderPass() == HECommonProxy.blockWaterStill.getRenderBlockPass()) {
            try {
                float x = (float)frustrumX.getDouble(frustrum);
                float y = (float)frustrumY.getDouble(frustrum);
                float z = (float)frustrumZ.getDouble(frustrum);
                HEProgram.calculateViewProjection(x, y, z);
            } catch(Exception e) {}

            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);

            HEProgram.bind();
            HEProgram.setViewProjection();
            HEProgram.setWaterLevels();
            HEProgram.setWaterUV();
            HEProgram.bindLightLUT();
            HEProgram.bindAtlasTexture();

            // TODO: sort chunks
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

            HEProgram.unbind();

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }

    // One can argue to use ChunkEvent.Load and ChunkEvent.Unload for this stuff,
    // but those are not in the GL thread and cause issues with cleanup etc
    public void onRenderChunkUpdate(int oldX, int oldY, int oldZ, int x, int y, int z) {
        // Just execute once per vertical SubChunk-stack
        if(y == 0) {
            int oldChunkX = HEUtil.bucketInt16(oldX);
            int oldChunkZ = HEUtil.bucketInt16(oldZ);
            long oldKey = HEUtil.chunkXZ2Int(oldChunkX, oldChunkZ);
            int chunkX = HEUtil.bucketInt16(x);
            int chunkZ = HEUtil.bucketInt16(z);
            long newKey = HEUtil.chunkXZ2Int(chunkX, chunkZ);

            HESubChunk[] subChunks = null;
            if(chunks.containsKey(oldKey)) {
                subChunks = chunks.get(oldKey);
                for (HESubChunk subChunk : subChunks)
                    subChunk.reset();
                chunks.remove(oldKey);
            }

            if(!chunks.containsKey(newKey)) {
                if(subChunks == null)
                    subChunks = new HESubChunk[] {
                            new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk(),
                            new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk(),
                            new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk(),
                            new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk()};
                chunks.put(newKey, subChunks);
            }
        }
    }
}
