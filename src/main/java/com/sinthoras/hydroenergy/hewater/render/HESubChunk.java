package com.sinthoras.hydroenergy.hewater.render;

import com.sinthoras.hydroenergy.proxy.HECommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.*;

@SideOnly(Side.CLIENT)
public class HESubChunk {


    public int vaoId = -1;
    public int vboId = -1;
    public int numWaterBlocks = 0;

    public void reset() {
        if(vaoId != -1) {
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(2);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL15.glDeleteBuffers(vboId);

            GL30.glBindVertexArray(0);
            GL30.glDeleteVertexArrays(vaoId);
        }

        numWaterBlocks = 0;
    }

    public void render(double partialTickTime) {
        if(numWaterBlocks != 0) {
            HEProgram.bind();

            GL30.glBindVertexArray(vaoId);

            // set uniforms
            HEProgram.setViewProjection();

            GL11.glDrawArrays(GL11.GL_POINTS, 0, numWaterBlocks);

            GL30.glBindVertexArray(0);

            HEProgram.unbind();
        }
    }

    private void patchBlockLight(int chunkX, int chunkY, int chunkZ, int x, int y, int z, float waterLevel, WorldClient world, Chunk chunk) {
        float diff = Math.min((chunkY << 4) - waterLevel + y, 0);
        int lightVal = (int)(15 + diff * HECommonProxy.blockWaterStill.getLightOpacity());
        lightVal = Math.max(lightVal, 0);
        world.setLightValue(EnumSkyBlock.Sky, chunkX*16+x, chunkY*16+y, chunkZ*16+z, lightVal);
    }
}
