package com.sinthoras.hydroenergy.hewater.render;

import com.google.common.base.Charsets;
import com.sinthoras.hydroenergy.HE;

import com.sinthoras.hydroenergy.controller.HEDamsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
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
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class HEProgram {

    private static final ResourceLocation vertexShaderLocation = new ResourceLocation(HE.MODID, "shader/hewater/shader.vsh");
    private static final ResourceLocation geometryShaderLocation = new ResourceLocation(HE.MODID, "shader/hewater/shader.gsh");
    private static final ResourceLocation fragmentShaderLocation = new ResourceLocation(HE.MODID, "shader/hewater/shader.fsh");

    private static int programID;
    private static int viewProjectionID;
    private static int waterLevelsID;
    private static int debugModesID;
    private static int lightLUTID;
    private static int atlasTextureID;
    private static int texCoordStillMinID;
    private static int texCoordStillDeltaID;
    private static int texCoordFlowingMinID;
    private static int texCoordFlowingDeltaID;
    private static int fogDiffID;
    private static int fogEndID;
    private static int fogDensityID;
    private static int fogModeLinearID;
    private static int fogColorID;
    private static int cameraPositionID;

    private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer modelviewProjection = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer fogColor = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer waterLevels = GLAllocation.createDirectFloatBuffer(HE.maxController);
    private static final FloatBuffer debugModes = GLAllocation.createDirectFloatBuffer(HE.maxController);

    private static Field locationLightMap;
    static {
        try {
            locationLightMap = EntityRenderer.class.getDeclaredField("locationLightMap");
            locationLightMap.setAccessible(true);
        } catch(Exception e) {}
    }


    public static void init() {
        final int vertexShader = loadShader(vertexShaderLocation, GL20.GL_VERTEX_SHADER, "");
        final int geometryShader = loadShader(geometryShaderLocation, GL32.GL_GEOMETRY_SHADER, "#define NUM_CONTROLLERS " + HE.maxController + "\n");
        final int fragmentShader = loadShader(fragmentShaderLocation, GL20.GL_FRAGMENT_SHADER, "");

        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShader);
        GL20.glAttachShader(programID, geometryShader);
        GL20.glAttachShader(programID, fragmentShader);
        GL20.glLinkProgram(programID);

        if(GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0) {
            String s = StringUtils.trim(GL20.glGetProgramInfoLog(programID, 32768));
            HE.LOG.error("Shader program linking failed: " + s);
            programID = -1;
            return;
        }

        viewProjectionID = GL20.glGetUniformLocation(programID, "g_viewProjection");
        waterLevelsID = GL20.glGetUniformLocation(programID, "g_waterLevels");
        debugModesID = GL20.glGetUniformLocation(programID, "g_debugModes");
        lightLUTID = GL20.glGetUniformLocation(programID, "g_lightLUT");
        atlasTextureID = GL20.glGetUniformLocation(programID, "g_atlasTexture");
        texCoordStillMinID = GL20.glGetUniformLocation(programID, "g_texCoordStillMin");
        texCoordStillDeltaID = GL20.glGetUniformLocation(programID, "g_texCoordStillDelta");
        texCoordFlowingMinID = GL20.glGetUniformLocation(programID, "g_texCoordFlowingMin");
        texCoordFlowingDeltaID = GL20.glGetUniformLocation(programID, "g_texCoordFlowingDelta");
        fogDiffID = GL20.glGetUniformLocation(programID, "g_fogDiff");
        fogEndID = GL20.glGetUniformLocation(programID, "g_fogEnd");
        fogDensityID = GL20.glGetUniformLocation(programID, "g_fogDensity");
        fogModeLinearID = GL20.glGetUniformLocation(programID, "g_fogModeLinear");
        fogColorID = GL20.glGetUniformLocation(programID, "g_fogColor");
        cameraPositionID = GL20.glGetUniformLocation(programID, "g_cameraPosition");

        GL20.glUseProgram(0);
    }

    private static int loadShader(ResourceLocation shaderLocation, int type, String defines) {
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
            GL20.glCompileShader(shaderID);
            if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0)
            {
                String s = StringUtils.trim(GL20.glGetShaderInfoLog(shaderID, 32768));  //Const good?
                HE.LOG.error("Couldn't compile shader: " + s);
                return -1;
            }
            GL20.glUseProgram(programID);
            return shaderID;
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // TODO: glGetFloat is quite slow. If possible just recalculate those matricies
    public static void setViewProjection(float cameraX, float cameraY, float cameraZ) {
        projection.clear();
        modelview.clear();
        modelviewProjection.clear();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        Matrix4f translation = new Matrix4f().translate(new Vector3f(-cameraX, -cameraY, -cameraZ));
        Matrix4f projectionMatrix = (Matrix4f) new Matrix4f().load(projection.asReadOnlyBuffer());
        Matrix4f modelViewMatrix = (Matrix4f) new Matrix4f().load(modelview.asReadOnlyBuffer());
        Matrix4f result = Matrix4f.mul(modelViewMatrix, translation, null);
        result = Matrix4f.mul(projectionMatrix, result, null);
        result.store(modelviewProjection);
        modelviewProjection.flip();
        GL20.glUniformMatrix4(viewProjectionID, false, modelviewProjection);
    }

    public static void setCameraPosition(float cameraX, float cameraY, float cameraZ) {
        GL20.glUniform3f(cameraPositionID, cameraX, cameraY, cameraZ);
    }

    public static void setWaterLevels() {
        waterLevels.clear();
        waterLevels.put(HEDamsClient.getAllWaterLevels());
        waterLevels.flip();
        GL20.glUniform1(waterLevelsID, waterLevels);
    }

    public static void setDebugModes() {
        debugModes.clear();
        debugModes.put(HEDamsClient.getDebugModes());
        debugModes.flip();
        GL20.glUniform1(debugModesID, debugModes);
    }

    public static void setWaterUV() {
        IIcon iconStill = FluidRegistry.WATER.getStillIcon();
        float minU = iconStill.getInterpolatedU(0.0);
        float minV = iconStill.getInterpolatedV(0.0);
        GL20.glUniform2f(texCoordStillMinID, minU, minV);
        GL20.glUniform2f(texCoordStillDeltaID, iconStill.getInterpolatedU(16.0) - minU, iconStill.getInterpolatedV(16.0) - minV);
        IIcon iconFlowing = FluidRegistry.WATER.getFlowingIcon();
        minU = iconFlowing.getInterpolatedU(0.0);
        minV = iconFlowing.getInterpolatedV(0.0);
        GL20.glUniform2f(texCoordFlowingMinID, minU, minV);
        GL20.glUniform2f(texCoordFlowingDeltaID, iconFlowing.getInterpolatedU(16.0) - minU, iconFlowing.getInterpolatedV(16.0) - minV);
    }

    // TODO: get fog density and color from event
    public static void setFog() {
        float fogStart = GL11.glGetFloat(GL11.GL_FOG_START);
        float fogEnd = GL11.glGetFloat(GL11.GL_FOG_END);
        GL20.glUniform1f(fogDiffID, fogEnd - fogStart);
        GL20.glUniform1f(fogEndID, fogEnd);

        GL20.glUniform1f(fogDensityID, GL11.glGetFloat(GL11.GL_FOG_DENSITY));

        GL20.glUniform1f(fogModeLinearID, GL11.glGetFloat(GL11.GL_FOG_MODE) == GL11.GL_LINEAR ? 1.0f : 0.0f);

        fogColor.clear();
        GL11.glGetFloat(GL11.GL_FOG_COLOR, fogColor);
        GL20.glUniform3f(fogColorID, fogColor.get(0), fogColor.get(1), fogColor.get(2));
    }

    public static void bindLightLUT() {
        try {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            ResourceLocation lightMapLocation = (ResourceLocation) locationLightMap.get(Minecraft.getMinecraft().entityRenderer);
            Minecraft.getMinecraft().getTextureManager().bindTexture(lightMapLocation);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            GL20.glUniform1i(lightLUTID, 0);
        } catch(Exception e) {}
    }

    public static void bindAtlasTexture() {
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GL20.glUniform1i(atlasTextureID, 1);
    }

    public static void bind() {
        GL20.glUseProgram(programID);
    }

    public static void unbind() {
        GL20.glUseProgram(0);
    }
}
