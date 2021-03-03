package com.sinthoras.hydroenergy.hewater.render;

import com.google.common.base.Charsets;
import com.sinthoras.hydroenergy.HE;

import com.sinthoras.hydroenergy.controller.HEDamsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
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
    private static int lightLUTID;

    private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer modelviewProjection = GLAllocation.createDirectFloatBuffer(16);

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
        lightLUTID = GL20.glGetUniformLocation(programID, "g_lightLUT");

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
    public static void calculateViewProjection(float cameraX, float cameraY, float cameraZ) {
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
    }

    public static void setViewProjection() {
        GL20.glUniformMatrix4(viewProjectionID, false, modelviewProjection);
    }

    public static void setWaterLevels() {
        FloatBuffer waterLevels = GLAllocation.createDirectFloatBuffer(HE.maxController);
        waterLevels.put(HEDamsClient.instance.getAllWaterLevels());
        waterLevels.flip();
        GL20.glUniform1(waterLevelsID, waterLevels);
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

    public static void bind() {
        GL20.glUseProgram(programID);
    }

    public static void unbind() {
        GL20.glUseProgram(0);
    }
}
