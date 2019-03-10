package com.sinthoras.hydroenergy.hewater;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBGeometryShader4;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.sinthoras.hydroenergy.Main;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.ShaderLoader;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class HEWaterRenderer {
	
	private static int vertexID;
	private static int geometryID;
	private static int fragmentID;
	
	public static enum ShaderType
	{
		VERTEX_SHADER(".vsh", GL20.GL_VERTEX_SHADER),
		GEOMETRY_SHADER(".gsh", ARBGeometryShader4.GL_GEOMETRY_SHADER_ARB),
		GRAGMENT_SHADER(".fsh", GL20.GL_FRAGMENT_SHADER);
		
		public final String ext;
		public final int type;
		
		private ShaderType(String ext, int type)
		{
			this.ext = ext;
			this.type = type;
		}
	}
	
	private static boolean ARB_ENABLED;
	
	@SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
    	//System.out.println("render last call");
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		
		
		
		GL11.glPopAttrib();
    }
	
	public static int loadShader(String name, ShaderType type) throws JsonException, IOException
    {
        ResourceLocation resourcelocation = new ResourceLocation(Main.MODID, "shaders/program/" + name + type.ext);
        BufferedInputStream bufferedinputstream = new BufferedInputStream(Minecraft.getMinecraft().getResourceManager().getResource(resourcelocation).getInputStream());
        byte[] abyte = IOUtils.toByteArray(bufferedinputstream);
        ByteBuffer bytebuffer = BufferUtils.createByteBuffer(abyte.length);
        bytebuffer.put(abyte);
        bytebuffer.position(0);
        final int id = createShaderObject(type.type);
        setShaderSource(id, bytebuffer);
        compileShader(id);

        if (getShaderCompileStatus(id) == 0)
        {
            String s1 = StringUtils.trim(getShaderInfoLog(id, 32768));  //Const good?
            JsonException jsonexception = new JsonException("Couldn\'t compile " + type.ext + " program: " + s1);
            jsonexception.func_151381_b(resourcelocation.getResourcePath());
            throw jsonexception;
        }

        return id;
    }
	
//	public static int func_153175_a(int p_153175_0_, int p_153175_1_)
//    {
//        return field_153214_y ? ARBShaderObjects.glGetObjectParameteriARB(p_153175_0_, p_153175_1_) : GL20.glGetProgrami(p_153175_0_, p_153175_1_);
//    }
//
//    public static void func_153178_b(int p_153178_0_, int p_153178_1_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glAttachObjectARB(p_153178_0_, p_153178_1_);
//        }
//        else
//        {
//            GL20.glAttachShader(p_153178_0_, p_153178_1_);
//        }
//    }
//
//    public static void func_153180_a(int p_153180_0_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glDeleteObjectARB(p_153180_0_);
//        }
//        else
//        {
//            GL20.glDeleteShader(p_153180_0_);
//        }
//    }

    private static int createShaderObject(int type)
    {
        return ARB_ENABLED ? ARBShaderObjects.glCreateShaderObjectARB(type) : GL20.glCreateShader(type);
    }

    private static void setShaderSource(int shaderID, ByteBuffer buffer)
    {
        if (ARB_ENABLED)
        {
            ARBShaderObjects.glShaderSourceARB(shaderID, buffer);
        }
        else
        {
            GL20.glShaderSource(shaderID, buffer);
        }
    }

    private static void compileShader(int shaderID)
    {
        if (ARB_ENABLED)
        {
            ARBShaderObjects.glCompileShaderARB(shaderID);
        }
        else
        {
            GL20.glCompileShader(shaderID);
        }
    }
    
    private static int getShaderCompileStatus(int shaderID)
    {
    	return ARB_ENABLED ? getShaderStatus(shaderID, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) : getShaderStatus(shaderID, GL20.GL_COMPILE_STATUS);
    }

    private static int getShaderStatus(int shaderID, int statusID)
    {
        return ARB_ENABLED ? ARBShaderObjects.glGetObjectParameteriARB(shaderID, statusID) : GL20.glGetShaderi(shaderID, statusID);
    }

    private static String getShaderInfoLog(int shaderID, int logID)
    {
        return ARB_ENABLED ? ARBShaderObjects.glGetInfoLogARB(shaderID, logID) : GL20.glGetShaderInfoLog(shaderID, logID);
    }

//    public static String func_153166_e(int p_153166_0_, int p_153166_1_)
//    {
//        return field_153214_y ? ARBShaderObjects.glGetInfoLogARB(p_153166_0_, p_153166_1_) : GL20.glGetProgramInfoLog(p_153166_0_, p_153166_1_);
//    }
//
//    public static void func_153161_d(int p_153161_0_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glUseProgramObjectARB(p_153161_0_);
//        }
//        else
//        {
//            GL20.glUseProgram(p_153161_0_);
//        }
//    }
//
//    public static int func_153183_d()
//    {
//        return field_153214_y ? ARBShaderObjects.glCreateProgramObjectARB() : GL20.glCreateProgram();
//    }
//
//    public static void func_153187_e(int p_153187_0_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glDeleteObjectARB(p_153187_0_);
//        }
//        else
//        {
//            GL20.glDeleteProgram(p_153187_0_);
//        }
//    }
//
//    public static void func_153179_f(int p_153179_0_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glLinkProgramARB(p_153179_0_);
//        }
//        else
//        {
//            GL20.glLinkProgram(p_153179_0_);
//        }
//    }
//
//    public static int func_153194_a(int p_153194_0_, CharSequence p_153194_1_)
//    {
//        return field_153214_y ? ARBShaderObjects.glGetUniformLocationARB(p_153194_0_, p_153194_1_) : GL20.glGetUniformLocation(p_153194_0_, p_153194_1_);
//    }
//
//    public static void func_153181_a(int p_153181_0_, IntBuffer p_153181_1_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glUniform1ARB(p_153181_0_, p_153181_1_);
//        }
//        else
//        {
//            GL20.glUniform1(p_153181_0_, p_153181_1_);
//        }
//    }
//
//    public static void func_153163_f(int p_153163_0_, int p_153163_1_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glUniform1iARB(p_153163_0_, p_153163_1_);
//        }
//        else
//        {
//            GL20.glUniform1i(p_153163_0_, p_153163_1_);
//        }
//    }
//
//    public static void func_153168_a(int p_153168_0_, FloatBuffer p_153168_1_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glUniform1ARB(p_153168_0_, p_153168_1_);
//        }
//        else
//        {
//            GL20.glUniform1(p_153168_0_, p_153168_1_);
//        }
//    }
//
//    public static void func_153182_b(int p_153182_0_, IntBuffer p_153182_1_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glUniform2ARB(p_153182_0_, p_153182_1_);
//        }
//        else
//        {
//            GL20.glUniform2(p_153182_0_, p_153182_1_);
//        }
//    }
//
//    public static void func_153177_b(int p_153177_0_, FloatBuffer p_153177_1_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glUniform2ARB(p_153177_0_, p_153177_1_);
//        }
//        else
//        {
//            GL20.glUniform2(p_153177_0_, p_153177_1_);
//        }
//    }
//
//    public static void func_153192_c(int p_153192_0_, IntBuffer p_153192_1_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glUniform3ARB(p_153192_0_, p_153192_1_);
//        }
//        else
//        {
//            GL20.glUniform3(p_153192_0_, p_153192_1_);
//        }
//    }
//
//    public static void func_153191_c(int p_153191_0_, FloatBuffer p_153191_1_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glUniform3ARB(p_153191_0_, p_153191_1_);
//        }
//        else
//        {
//            GL20.glUniform3(p_153191_0_, p_153191_1_);
//        }
//    }
//
//    public static void func_153162_d(int p_153162_0_, IntBuffer p_153162_1_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glUniform4ARB(p_153162_0_, p_153162_1_);
//        }
//        else
//        {
//            GL20.glUniform4(p_153162_0_, p_153162_1_);
//        }
//    }
//
//    public static void func_153159_d(int p_153159_0_, FloatBuffer p_153159_1_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glUniform4ARB(p_153159_0_, p_153159_1_);
//        }
//        else
//        {
//            GL20.glUniform4(p_153159_0_, p_153159_1_);
//        }
//    }
//
//    public static void func_153173_a(int p_153173_0_, boolean p_153173_1_, FloatBuffer p_153173_2_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glUniformMatrix2ARB(p_153173_0_, p_153173_1_, p_153173_2_);
//        }
//        else
//        {
//            GL20.glUniformMatrix2(p_153173_0_, p_153173_1_, p_153173_2_);
//        }
//    }
//
//    public static void func_153189_b(int p_153189_0_, boolean p_153189_1_, FloatBuffer p_153189_2_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glUniformMatrix3ARB(p_153189_0_, p_153189_1_, p_153189_2_);
//        }
//        else
//        {
//            GL20.glUniformMatrix3(p_153189_0_, p_153189_1_, p_153189_2_);
//        }
//    }
//
//    public static void func_153160_c(int p_153160_0_, boolean p_153160_1_, FloatBuffer p_153160_2_)
//    {
//        if (field_153214_y)
//        {
//            ARBShaderObjects.glUniformMatrix4ARB(p_153160_0_, p_153160_1_, p_153160_2_);
//        }
//        else
//        {
//            GL20.glUniformMatrix4(p_153160_0_, p_153160_1_, p_153160_2_);
//        }
//    }
//
//    public static int func_153164_b(int p_153164_0_, CharSequence p_153164_1_)
//    {
//        return field_153214_y ? ARBVertexShader.glGetAttribLocationARB(p_153164_0_, p_153164_1_) : GL20.glGetAttribLocation(p_153164_0_, p_153164_1_);
//    }
}
