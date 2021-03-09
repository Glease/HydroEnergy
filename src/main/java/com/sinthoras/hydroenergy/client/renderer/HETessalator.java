package com.sinthoras.hydroenergy.client.renderer;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.HEUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Stack;

@SideOnly(Side.CLIENT)
public class HETessalator {

    private static final HashMap<Long, HERenderChunk> chunks = new HashMap<Long, HERenderChunk>();
    private static final Stack<HEBufferIds> availableBuffers = new Stack<HEBufferIds>();
    private static final FloatBuffer vboBuffer = GLAllocation.createDirectFloatBuffer(7 * (16 * 16 * 16));
    private static int numWaterBlocks = 0;


    public static void onPostRender(World world, int blockX, int blockY, int blockZ) {
        if(numWaterBlocks != 0) {
            int chunkX = HEUtil.coordBlockToChunk(blockX);
            int chunkY = HEUtil.coordBlockToChunk(blockY);
            int chunkZ = HEUtil.coordBlockToChunk(blockZ);
            long key = HEUtil.chunkCoordsToKey(chunkX, chunkZ);
            HERenderSubChunk subChunk = chunks.get(key).subChunks[chunkY];

            if (subChunk.vaoId == -1) {
                if(availableBuffers.empty()) {
                    subChunk.vaoId = GL30.glGenVertexArrays();
                    subChunk.vboId = GL15.glGenBuffers();

                    GL30.glBindVertexArray(subChunk.vaoId);

                    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, subChunk.vboId);
                    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vboBuffer.capacity() * HE.FLOAT_SIZE, GL15.GL_STATIC_DRAW);

                    GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 7 * HE.FLOAT_SIZE, 0 * HE.FLOAT_SIZE);
                    GL20.glEnableVertexAttribArray(0);

                    GL20.glVertexAttribPointer(1, 1, GL11.GL_FLOAT, false, 7 * HE.FLOAT_SIZE, 3 * HE.FLOAT_SIZE);
                    GL20.glEnableVertexAttribArray(1);

                    GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, 7 * HE.FLOAT_SIZE, 4 * HE.FLOAT_SIZE);
                    GL20.glEnableVertexAttribArray(2);

                    GL20.glVertexAttribPointer(3, 1, GL11.GL_FLOAT, false, 7 * HE.FLOAT_SIZE, 5 * HE.FLOAT_SIZE);
                    GL20.glEnableVertexAttribArray(3);

                    GL20.glVertexAttribPointer(4, 1, GL11.GL_FLOAT, false, 7 * HE.FLOAT_SIZE, 6 * HE.FLOAT_SIZE);
                    GL20.glEnableVertexAttribArray(4);

                    GL30.glBindVertexArray(0);
                }
                else {
                    HEBufferIds ids = availableBuffers.pop();
                    subChunk.vaoId = ids.vaoId;
                    subChunk.vboId = ids.vboId;
                }
            }

            vboBuffer.flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, subChunk.vboId);
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vboBuffer);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

            subChunk.numWaterBlocks = numWaterBlocks;

            // reset tesselator
            vboBuffer.clear();
            numWaterBlocks = 0;
        }
    }

    public static void addBlock(int blockX, int blockY, int blockZ, int waterId, int worldColorModifier, boolean[] shouldSideBeRendered) {
        int renderSides = 0;
        for(int i=0;i<shouldSideBeRendered.length;i++) {
            if (shouldSideBeRendered[i]) {
                renderSides |= 1 << i;
            }
        }

        vboBuffer.put(blockX);
        vboBuffer.put(blockY);
        vboBuffer.put(blockZ);

        int lightXMinus = 15, lightXPlus = 15, lightYMinus = 15, lightYPlus = 15, lightZMinus = 15, lightZPlus = 15;
        int light0 = (lightXMinus << 16) | (lightXPlus << 8) | lightYMinus;
        int light1 = (lightYPlus << 16) | (lightZMinus << 8) | lightZPlus;
        vboBuffer.put(light0);
        vboBuffer.put(light1);

        int info = (waterId << 6) | renderSides;
        vboBuffer.put(info);

        vboBuffer.put(worldColorModifier);

        numWaterBlocks++;
    }

    public static void render(ICamera frustrum) {
        if(MinecraftForgeClient.getRenderPass() == HE.waterBlocks[0].getRenderBlockPass()) {
            float cameraBlockX = HEUtil.getCameraBlockX(frustrum);
            float cameraBlockY = HEUtil.getCameraBlockY(frustrum);
            float cameraBlockZ = HEUtil.getCameraBlockZ(frustrum);

            GL11.glEnable(GL11.GL_BLEND);

            HEProgram.bind();

            HEProgram.setViewProjection(cameraBlockX, cameraBlockY, cameraBlockZ);
            HEProgram.setCameraPosition(cameraBlockX, cameraBlockY, cameraBlockZ);
            HEProgram.setWaterLevels();
            HEProgram.setDebugModes();
            HEProgram.setWaterUV();
            HEProgram.setFog();
            HEProgram.bindLightLUT();
            HEProgram.bindAtlasTexture();

            HESortedRenderList.setup(HEUtil.coordBlockToChunk((int)cameraBlockX),
                    HEUtil.coordBlockToChunk((int)cameraBlockY),
                    HEUtil.coordBlockToChunk((int)cameraBlockZ));

            for (long key : chunks.keySet()) {
                int chunkX = (int) (key >> 32);
                int chunkZ = (int) key;
                for (int chunkY = 0; chunkY < 16; chunkY++) {
                    int blockX = HEUtil.coordChunkToBlock(chunkX);
                    int blockY = HEUtil.coordChunkToBlock(chunkY);
                    int blockZ = HEUtil.coordChunkToBlock(chunkZ);
                    if (frustrum.isBoundingBoxInFrustum(AxisAlignedBB.getBoundingBox(blockX, blockY, blockZ, blockX + 16, blockY + 16, blockZ + 16))) {
                        HERenderSubChunk subChunk = chunks.get(key).subChunks[chunkY];
                        HESortedRenderList.add(subChunk.vaoId, subChunk.numWaterBlocks, chunkX, chunkY, chunkZ);
                    }
                }
            }
            HESortedRenderList.render();

            HEProgram.unbind();

            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    // One can argue to use ChunkEvent.Load and ChunkEvent.Unload for this stuff,
    // but those are not in the GL thread and cause issues with cleanup etc
    public static void onRenderChunkUpdate(int oldBlockX, int oldBlockY, int oldBlockZ, int blockX, int blockY, int blockZ) {
        // Just execute once per vertical SubChunk-stack (aka chunk)
        if(blockY == 0) {
            int oldChunkX = HEUtil.coordBlockToChunk(oldBlockX);
            int oldChunkZ = HEUtil.coordBlockToChunk(oldBlockZ);
            long oldKey = HEUtil.chunkCoordsToKey(oldChunkX, oldChunkZ);
            int chunkX = HEUtil.coordBlockToChunk(blockX);
            int chunkZ = HEUtil.coordBlockToChunk(blockZ);
            long newKey = HEUtil.chunkCoordsToKey(chunkX, chunkZ);

            HERenderChunk renderChunk = null;
            if(chunks.containsKey(oldKey)) {
                renderChunk = chunks.get(oldKey);
                for (HERenderSubChunk subChunk : renderChunk.subChunks) {
                    if (subChunk.vaoId != -1) {
                        HEBufferIds ids = new HEBufferIds();
                        ids.vaoId = subChunk.vaoId;
                        ids.vboId = subChunk.vboId;
                        availableBuffers.push(ids);
                        subChunk.vaoId = -1;
                        subChunk.vboId = -1;
                        subChunk.numWaterBlocks = 0;
                    }
                }
                chunks.remove(oldKey);
            }

            if(!chunks.containsKey(newKey)) {
                if(renderChunk == null) {
                    renderChunk = new HERenderChunk();
                }
                chunks.put(newKey, renderChunk);
            }
        }
    }
}

@SideOnly(Side.CLIENT)
class HEBufferIds {
    public int vaoId;
    public int vboId;
}

@SideOnly(Side.CLIENT)
class HERenderChunk {
    public HERenderSubChunk[] subChunks;

    public HERenderChunk() {
        subChunks = new HERenderSubChunk[16];
        for(int i=0;i<subChunks.length;i++) {
            subChunks[i] = new HERenderSubChunk();
        }
    }
}

@SideOnly(Side.CLIENT)
class HERenderSubChunk {
    public int vaoId = -1;
    public int vboId = -1;
    public int numWaterBlocks = 0;
}
