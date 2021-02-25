package com.sinthoras.hydroenergy.hewater.render;

import com.sinthoras.hydroenergy.proxy.HECommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.*;

import java.nio.IntBuffer;
import java.util.BitSet;

@SideOnly(Side.CLIENT)
public class HESubChunk {

    private BitSet lightUpdateFlags = new BitSet(16*16*16);  // 512B
    private int vaoId = -1;
    private int vboPositionId = -1;
    private int vboWaterIdId = -1;
    private IntBuffer positionBuffer = GLAllocation.createDirectIntBuffer(3 * (1 << 12));  // == 3 * 16 * 16 * 16  (12kInt = 48kB)
    private IntBuffer waterIdBuffer = GLAllocation.createDirectIntBuffer(1 << 12);  // == 1 * 16 * 16 * 16  (4kInt = 16kB)
    private int numWaterBlocks = 0;


    public void onPreRender() {
        if(numWaterBlocks != 0) {
            lightUpdateFlags.clear();
            positionBuffer.clear();
            waterIdBuffer.clear();
            numWaterBlocks = 0;
        }
    }

    public void onPostRender(World world, int chunkX, int chunkY, int chunkZ) {
        if(vaoId == -1) {
            vaoId = GL30.glGenVertexArrays();
            vboPositionId = GL15.glGenBuffers();
            vboWaterIdId = GL15.glGenBuffers();
        }

        if(numWaterBlocks != 0) {
            positionBuffer.flip();
            waterIdBuffer.flip();

            //GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            //GL11.glPushClientAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);

            GL30.glBindVertexArray(vaoId);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboPositionId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(0, 3, GL11.GL_INT, false, 0, 0);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboWaterIdId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(1, 1, GL11.GL_INT, false, 0, 0);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL30.glBindVertexArray(0);

            //GL11.glPopClientAttrib();
            //GL11.glPopAttrib();

            // TODO: Light update (gotta link update flag and waterID to provide patch with correct waterLevel
            /*
            Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
            for (int linearCoord = lightUpdateFlags.nextSetBit(0); linearCoord != -1; linearCoord = lightUpdateFlags.nextSetBit(linearCoord + 1))
                patchBlockLight(chunkX, chunkY, chunkZ, linearCoord >> 8, (linearCoord >> 4) & 15, linearCoord & 15, waterLevel, world, chunk);
            */
        }
    }

    public void addBlock(int x, int y, int z, int waterId, boolean[] shouldSideBeRendered) {
        waterId <<= 6;
        for(int i=0;i<shouldSideBeRendered.length;i++)
            if(shouldSideBeRendered[i])
                waterId |= 1 << i;

        // add to VBO
        positionBuffer.put(x);
        positionBuffer.put(y);
        positionBuffer.put(z);
        waterIdBuffer.put(waterId);
        numWaterBlocks++;

        // Light update stuff
        x = x & 15;
        y = y & 15;
        z = z & 15;
        lightUpdateFlags.set((x << 8) | (y << 4) | z);
    }

    public void render(double partialTickTime) {
        if(numWaterBlocks != 0) {

            // (float)((double)this.renderChunkX - this.cameraX), (float)((double)this.renderChunkY - this.cameraY), (float)((double)this.renderChunkZ - this.cameraZ)


            //GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            //GL11.glPushClientAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);

            HEProgram.bind();

            GL30.glBindVertexArray(vaoId);
            GL20.glEnableVertexAttribArray(0);

            // set uniforms
            HEProgram.setViewProjection();

            GL11.glDrawArrays(GL11.GL_POINT, 0, numWaterBlocks);

            GL20.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);

            HEProgram.unbind();

            //GL11.glPopClientAttrib();
            //GL11.glPopAttrib();
        }
    }

    public void finalize() {
        GL20.glDisableVertexAttribArray(0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(vboPositionId);
        GL15.glDeleteBuffers(vboWaterIdId);

        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoId);
    }

    private void patchBlockLight(int chunkX, int chunkY, int chunkZ, int x, int y, int z, float waterLevel, WorldClient world, Chunk chunk) {
        float diff = Math.min((chunkY << 4) - waterLevel + y, 0);
        int lightVal = (int)(15 + diff * HECommonProxy.blockWaterStill.getLightOpacity());
        lightVal = Math.max(lightVal, 0);
        world.setLightValue(EnumSkyBlock.Sky, chunkX*16+x, chunkY*16+y, chunkZ*16+z, lightVal);
    }
}
