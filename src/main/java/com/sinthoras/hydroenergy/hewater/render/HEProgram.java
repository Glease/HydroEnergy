package com.sinthoras.hydroenergy.hewater.render;

import com.sinthoras.hydroenergy.HE;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class HEProgram {

    private static final ResourceLocation vertexShaderLocation = new ResourceLocation(HE.MODID, "shader/hewater/shader.vsh");
    private static final ResourceLocation geometryShaderLocation = new ResourceLocation(HE.MODID, "shader/hewater/shader.gsh");
    private static final ResourceLocation fragmentShaderLocation = new ResourceLocation(HE.MODID, "shader/hewater/shader.fsh");

    private static int programID;
    private static int viewProjectionID;
    private static int cameraPositionId;

    private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer modelviewProjection = GLAllocation.createDirectFloatBuffer(16);

    public static void init() {
        final int vertexShader = loadShader(vertexShaderLocation, GL20.GL_VERTEX_SHADER);
        final int geometryShader = loadShader(geometryShaderLocation, GL32.GL_GEOMETRY_SHADER);
        final int fragmentShader = loadShader(fragmentShaderLocation, GL20.GL_FRAGMENT_SHADER);

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
        cameraPositionId = GL20.glGetUniformLocation(programID, "g_cameraPosition");

        GL20.glUseProgram(0);
    }

    public static int getProgramID() {
        return programID;
    }

    private static int loadShader(ResourceLocation shaderLocation, int type) {
        try {
            InputStream shaderStream = Minecraft.getMinecraft().getResourceManager().getResource(shaderLocation).getInputStream();
            BufferedInputStream bufferedinputstream = new BufferedInputStream(shaderStream);
            byte[] shaderBytes = IOUtils.toByteArray(bufferedinputstream);
            ByteBuffer bytebuffer = BufferUtils.createByteBuffer(shaderBytes.length);
            bytebuffer.put(shaderBytes);
            bytebuffer.position(0);
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

    public static void setViewProjection() {
        projection.clear();
        modelview.clear();
        modelviewProjection.clear();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        Matrix4f projectionMatrix = (Matrix4f) new Matrix4f().load(projection.asReadOnlyBuffer());
        Matrix4f modelViewMatrix = (Matrix4f) new Matrix4f().load(modelview.asReadOnlyBuffer());
        Matrix4f result = Matrix4f.mul(projectionMatrix, modelViewMatrix, null);
        result.store(modelviewProjection);
        GL20.glUniformMatrix4(viewProjectionID, false, modelviewProjection);
    }

    public static void setCameraPosition(float x, float y, float z) {
        GL20.glUniform3f(cameraPositionId, x, y, z);
    }

    public static void bind() {
        GL20.glUseProgram(programID);
    }

    public static void unbind() {
        GL20.glUseProgram(0);
    }
}
