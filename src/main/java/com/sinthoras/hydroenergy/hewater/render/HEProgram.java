package com.sinthoras.hydroenergy.hewater.render;

import com.sinthoras.hydroenergy.HE;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.io.*;
import java.nio.ByteBuffer;

public class HEProgram {

    private static final ResourceLocation vertexShaderLocation = new ResourceLocation(HE.MODID, "shader/hewater/shader.vsh");
    private static final ResourceLocation geometryShaderLocation = new ResourceLocation(HE.MODID, "shader/hewater/shader.gsh");
    private static final ResourceLocation fragmentShaderLocation = new ResourceLocation(HE.MODID, "shader/hewater/shader.fsh");

    private static int programID;

    public static void init() {
        final int vertexShader = loadShader(vertexShaderLocation, GL20.GL_VERTEX_SHADER);
        final int geometryShader = loadShader(geometryShaderLocation, GL32.GL_GEOMETRY_SHADER);
        final int fragmentShader = loadShader(fragmentShaderLocation, GL20.GL_FRAGMENT_SHADER);

        programID = GL20.glCreateProgram();
        GL20.glUseProgram(programID);
        GL20.glAttachShader(programID, vertexShader);
        GL20.glAttachShader(programID, geometryShader);
        GL20.glAttachShader(programID, fragmentShader);
        GL20.glLinkProgram(programID);

        if(GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0) {
            String s = StringUtils.trim(GL20.glGetProgramInfoLog(programID, 32768));
            HE.LOG.info("Shader program linking failed: " + s);
            programID = -1;
        }
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
                throw new Exception("Couldn't compile shader: " + s);
            }
            return shaderID;
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
