package com.sinthoras.hydroenergy.client.renderer;

import com.google.common.base.Charsets;
import com.sinthoras.hydroenergy.HE;

import com.sinthoras.hydroenergy.HETags;
import com.sinthoras.hydroenergy.client.HEReflection;
import com.sinthoras.hydroenergy.client.HEClient;
import com.sinthoras.hydroenergy.config.HEConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class HEProgram {

    private static final ResourceLocation vertexShaderLocation = new ResourceLocation(HETags.MODID, "shader/hewater/shader.vsh");
    private static final ResourceLocation geometryShaderLocation = new ResourceLocation(HETags.MODID, "shader/hewater/shader.gsh");
    private static final ResourceLocation fragmentShaderLocation = new ResourceLocation(HETags.MODID, "shader/hewater/shader.fsh");

    private static int programId;
    private static int viewProjectionId;
    private static int waterLevelsId;
    private static int debugStatesId;
    private static int lightLookupTableId;
    private static int atlasTextureId;
    private static int texCoordStillMinId;
    private static int texCoordStillDeltaId;
    private static int texCoordFlowingMinId;
    private static int texCoordFlowingDeltaId;
    private static int fogDiffId;
    private static int fogEndId;
    private static int fogDensityId;
    private static int fogModeLinearId;
    private static int fogColorId;
    private static int cameraPositionId;
    private static int renderOffsetId;

    private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer modelviewProjection = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer waterLevels = GLAllocation.createDirectFloatBuffer(HEConfig.maxDams);
    private static final FloatBuffer debugStates = GLAllocation.createDirectFloatBuffer(HEConfig.maxDams);

    private static float fogColorRed = 0;
    private static float fogColorGreen = 0;
    private static float fogColorBlue = 0;


    public static void init() {
        checkError("pre init 0");
        final String defines = "#version 330 core\n"
                + "#define NUM_CONTROLLERS " + HEConfig.maxDams + "\n"
                + "#define CLIPPING_OFFSET " + HEConfig.clippingOffset + "\n";
        final int vertexShader = loadShader(vertexShaderLocation, GL20.GL_VERTEX_SHADER, defines);
        final int geometryShader = loadShader(geometryShaderLocation, GL32.GL_GEOMETRY_SHADER, defines);
        final int fragmentShader = loadShader(fragmentShaderLocation, GL20.GL_FRAGMENT_SHADER, defines);

        programId = GL20.glCreateProgram();
        checkError("post init::glCreateProgram 1");
        GL20.glAttachShader(programId, vertexShader);
        checkError("post init::glAttachShader 2");
        GL20.glAttachShader(programId, geometryShader);
        checkError("post init::glAttachShader 3");
        GL20.glAttachShader(programId, fragmentShader);
        checkError("post init::glAttachShader 4");
        GL20.glLinkProgram(programId);
        checkError("post init::glLinkProgram 5");

        if(GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            String s = StringUtils.trim(GL20.glGetProgramInfoLog(programId, 32768));
            HE.error("Shader program linking failed: " + s);
            programId = GL31.GL_INVALID_INDEX;
            return;
        }
        checkError("post init::glGetProgrami 6");

        viewProjectionId = GL20.glGetUniformLocation(programId, "g_viewProjection");
        checkError("post init::glGetUniformLocation 7");
        waterLevelsId = GL20.glGetUniformLocation(programId, "g_waterLevels");
        checkError("post init::glGetUniformLocation 8");
        debugStatesId = GL20.glGetUniformLocation(programId, "g_debugModes");
        checkError("post init::glGetUniformLocation 9");
        lightLookupTableId = GL20.glGetUniformLocation(programId, "g_lightLUT");
        checkError("post init::glGetUniformLocation 10");
        atlasTextureId = GL20.glGetUniformLocation(programId, "g_atlasTexture");
        checkError("post init::glGetUniformLocation 11");
        texCoordStillMinId = GL20.glGetUniformLocation(programId, "g_texCoordStillMin");
        checkError("post init::glGetUniformLocation 12");
        texCoordStillDeltaId = GL20.glGetUniformLocation(programId, "g_texCoordStillDelta");
        checkError("post init::glGetUniformLocation 13");
        texCoordFlowingMinId = GL20.glGetUniformLocation(programId, "g_texCoordFlowingMin");
        checkError("post init::glGetUniformLocation 14");
        texCoordFlowingDeltaId = GL20.glGetUniformLocation(programId, "g_texCoordFlowingDelta");
        checkError("post init::glGetUniformLocation 15");
        fogDiffId = GL20.glGetUniformLocation(programId, "g_fogDiff");
        checkError("post init::glGetUniformLocation 16");
        fogEndId = GL20.glGetUniformLocation(programId, "g_fogEnd");
        checkError("post init::glGetUniformLocation 17");
        fogDensityId = GL20.glGetUniformLocation(programId, "g_fogDensity");
        checkError("post init::glGetUniformLocation 18");
        fogModeLinearId = GL20.glGetUniformLocation(programId, "g_fogModeLinear");
        checkError("post init::glGetUniformLocation 19");
        fogColorId = GL20.glGetUniformLocation(programId, "g_fogColor");
        checkError("post init::glGetUniformLocation 20");
        cameraPositionId = GL20.glGetUniformLocation(programId, "g_cameraPosition");
        checkError("post init::glGetUniformLocation 21");
        renderOffsetId = GL20.glGetUniformLocation(programId, "g_renderOffset");
        checkError("post init::glGetUniformLocation 22");

        GL20.glUseProgram(0);
        checkError("post init::glUseProgram 23");

        HE.info("Render pipeline initialized.");
    }

    private static int loadShader(ResourceLocation shaderLocation, int type, String defines) {
        checkError("pre loadShader 0");
        try {
            InputStream shaderStream = Minecraft.getMinecraft().getResourceManager().getResource(shaderLocation).getInputStream();
            BufferedInputStream bufferedinputstream = new BufferedInputStream(shaderStream);
            byte[] shaderBytes = IOUtils.toByteArray(bufferedinputstream);
            byte[] definesBytes = defines.getBytes(Charsets.US_ASCII);
            ByteBuffer bytebuffer = BufferUtils.createByteBuffer(shaderBytes.length + definesBytes.length);
            bytebuffer.put(definesBytes);
            bytebuffer.put(shaderBytes);
            bytebuffer.flip();
            final int shaderID = GL20.glCreateShader(type);
            GL20.glShaderSource(shaderID, bytebuffer);
            checkError("post loadShader::glShaderSource 1");
            GL20.glCompileShader(shaderID);
            checkError("post loadShader::glCompileShader 2");
            if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0)
            {
                String s = StringUtils.trim(GL20.glGetShaderInfoLog(shaderID, 32768));  //Const good?
                HE.error("Couldn't compile shader: " + s);
                return GL31.GL_INVALID_INDEX;
            }
            checkError("post loadShader::glGetShaderi 3");
            GL20.glUseProgram(programId);
            checkError("post loadShader::glUseProgram 4");
            return shaderID;
        }
        catch(Exception e) {
            e.printStackTrace();
            return GL31.GL_INVALID_INDEX;
        }
    }

    public static void setViewProjection(float cameraX, float cameraY, float cameraZ) {
        checkError("pre setViewProjection 0");
        projection.clear();
        modelview.clear();
        modelviewProjection.clear();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        checkError("post setViewProjection::glGetFloat 1");
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        checkError("post setViewProjection::glGetFloat 2");
        Matrix4f translation = new Matrix4f().translate(new Vector3f(-cameraX, -cameraY, -cameraZ));
        Matrix4f projectionMatrix = (Matrix4f) new Matrix4f().load(projection.asReadOnlyBuffer());
        Matrix4f modelViewMatrix = (Matrix4f) new Matrix4f().load(modelview.asReadOnlyBuffer());
        Matrix4f result = Matrix4f.mul(modelViewMatrix, translation, null);
        result = Matrix4f.mul(projectionMatrix, result, null);
        result.store(modelviewProjection);
        modelviewProjection.flip();
        GL20.glUniformMatrix4(viewProjectionId, false, modelviewProjection);
        checkError("post setViewProjection::glUniformMatrix4 3");
    }

    public static void setCameraPosition(float cameraX, float cameraY, float cameraZ) {
        checkError("pre setCameraPosition 0");
        GL20.glUniform3f(cameraPositionId, cameraX, cameraY, cameraZ);
        checkError("post setCameraPosition::glUniform3f 1");
    }

    public static void setWaterLevels() {
        checkError("pre setWaterLevels 0");
        waterLevels.clear();
        waterLevels.put(HEClient.getAllWaterLevelsForRendering());
        waterLevels.flip();
        GL20.glUniform1(waterLevelsId, waterLevels);
        checkError("post setWaterLevels::glUniform1 1");
    }

    public static void setDebugStates() {
        checkError("pre setDebugStates 0");
        debugStates.clear();
        debugStates.put(HEClient.getDebugStatesAsFactors());
        debugStates.flip();
        GL20.glUniform1(debugStatesId, debugStates);
        checkError("post setDebugStates::glUniform1 1");
    }

    public static void setWaterUV() {
        checkError("pre setWaterUV 0");
        IIcon iconStill = FluidRegistry.WATER.getStillIcon();
        float minU = iconStill.getMinU();
        float minV = iconStill.getMinV();
        GL20.glUniform2f(texCoordStillMinId, minU, minV);
        checkError("post setFog::glUniform2f 1");
        GL20.glUniform2f(texCoordStillDeltaId, iconStill.getMaxU() - minU, iconStill.getMaxV() - minV);
        checkError("post setFog::glUniform2f 2");
        IIcon iconFlowing = FluidRegistry.WATER.getFlowingIcon();
        minU = iconFlowing.getMinU();
        minV = iconFlowing.getMinV();
        GL20.glUniform2f(texCoordFlowingMinId, minU, minV);
        checkError("post setFog::glUniform2f 3");
        GL20.glUniform2f(texCoordFlowingDeltaId, iconFlowing.getMaxU() - minU, iconFlowing.getMaxV() - minV);
        checkError("post setFog::glUniform2f 4");
    }

    public static void setCullFronts() {
        checkError("pre setCullFronts 0");
        GL11.glCullFace(GL11.GL_FRONT);
        checkError("post setCullFronts::glCullFace 1");
        GL20.glUniform1f(renderOffsetId, 0.005f);
        checkError("post setCullFronts::glUniform1f 2");
    }

    public static void setCullBacks() {
        checkError("pre setCullBacks 0");
        GL11.glCullFace(GL11.GL_BACK);
        checkError("post setCullBacks::glCullFace 1");
        GL20.glUniform1f(renderOffsetId, 0.0f);
        checkError("post setCullBacks::glUniform1f 2");
    }

    // Reads the values from openGL. Sadly, the events EntityViewRenderEvent.FogDensity,
    // EntityViewRenderEvent.RenderFogEvent and EntityViewRenderEvent.FogColors do not
    // provide information for all possible states
    public static void setFog() {
        checkError("pre setFog 0");
        float fogStart = GL11.glGetFloat(GL11.GL_FOG_START);
        checkError("post setFog::glGetFloat 1");
        float fogEnd = GL11.glGetFloat(GL11.GL_FOG_END);
        checkError("post setFog::glGetFloat 2");
        GL20.glUniform1f(fogDiffId, fogEnd - fogStart);
        checkError("post setFog::glUniform1f 3");
        GL20.glUniform1f(fogEndId, fogEnd);
        checkError("post setFog::glUniform1f 4");

        GL20.glUniform1f(fogDensityId, GL11.glGetFloat(GL11.GL_FOG_DENSITY));
        checkError("post setFog::glUniform1f 5");

        GL20.glUniform1f(fogModeLinearId, GL11.glGetFloat(GL11.GL_FOG_MODE) == GL11.GL_LINEAR ? 1.0f : 0.0f);
        checkError("post setFog::glUniform1f 6");

        GL20.glUniform3f(fogColorId, fogColorRed, fogColorGreen, fogColorBlue);
        checkError("post setFog::glUniform3f 7");
    }

    public static void setFogColor(float red, float green, float blue) {
        fogColorRed = red;
        fogColorGreen = green;
        fogColorBlue = blue;
    }

    public static void bindLightLookupTable() {
        checkError("pre bindLightLookupTable 0");
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        checkError("post bindLightLookupTable::glActiveTexture 1");
        Minecraft.getMinecraft().getTextureManager().bindTexture(HEReflection.getLightMapLocation());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        checkError("post bindLightLookupTable::glTexParameteri 2");
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        checkError("post bindLightLookupTable::glTexParameteri 3");
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        checkError("post bindLightLookupTable::glTexParameteri 4");
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        checkError("post bindLightLookupTable::glTexParameteri 5");
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        checkError("post bindLightLookupTable::glTexParameteri 6");
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        checkError("post bindLightLookupTable::glTexParameteri 7");
        GL20.glUniform1i(lightLookupTableId, 1);
        checkError("post bindLightLookupTable::glTexParameteri 8");
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        checkError("post bindLightLookupTable::glTexParameteri 9");
    }

    public static void bindAtlasTexture() {
        checkError("pre bindAtlasTexture 0");
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        checkError("post bindAtlasTexture::glActiveTexture 1");
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GL20.glUniform1i(atlasTextureId, 2);
        checkError("post bindAtlasTexture::glUniform1i 2");
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        checkError("post bindAtlasTexture::glActiveTexture 3");
    }

    public static void bind() {
        checkError("pre bind 0");
        GL20.glUseProgram(programId);
        checkError("post bind::glUseProgram 1");
    }

    public static void unbind() {
        checkError("pre unbind 0");
        GL20.glUseProgram(0);
        checkError("post unbind::glUseProgram 1");
    }

    public static void checkError(String message) {
        final int glError = GL11.glGetError();
        if(glError != GL11.GL_NO_ERROR) {
            HE.warn("OpenGL Error " + glError + ":  " + message);
        }
    }
}
