package com.sinthoras.hydroenergy.asm.minecraft;

import com.sinthoras.hydroenergy.asm.HEClasses;
import com.sinthoras.hydroenergy.asm.HEPlugin;
import com.sinthoras.hydroenergy.asm.HEUtil;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.tree.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class WorldRendererTransformer implements IClassTransformer {

    public static final String fullClassName = "net.minecraft.client.renderer.WorldRenderer";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        final boolean isDeobfuscated = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
        if(transformedName == fullClassName) {
            return transform(basicClass, !isDeobfuscated);
        }
        else {
            return basicClass;
        }
    }

    /* After
     * this.setDontDraw();
     * insert
     * HETessalator.onRenderChunkUpdate(posX, posY, posZ, p_78913_1_, p_78913_2_, p_78913_3_);
     */
    public static byte[] transform(byte[] basicClass, boolean isObfuscated) {
        final ClassNode classNode = HEUtil.convertByteArrayToClassNode(basicClass);

        final String MARKER_method = isObfuscated ? "func_78913_a" : "setPosition";
        final String MARKER_method_DESC = "(III)V";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
        if(targetMethod == null) {
            HEPlugin.error("Could not find " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ". Rendering is broken!");
            return basicClass;
        }
        HEPlugin.info("Found " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName);

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.WorldRenderer;
        final String MARKER_instruction = isObfuscated ? "func_78910_b" : "setDontDraw";
        final String MARKER_instruction_DESC = "()V";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 1) {
            HEPlugin.error("Could not find " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in " + fullClassName + ". Rendering is broken!");
            return basicClass;
        }
        HEPlugin.info("Found " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in " + fullClassName);

        final String ADDED_method = "onRenderChunkUpdate";
        final String ADDED_method_DESC = "(IIIIII)V";
        final String FIELD_posX = isObfuscated ? "field_78923_c" : "posX";
        final String FIELD_posX_DESC = "I";
        final String FIELD_posY = isObfuscated ? "field_78920_d" : "posY";
        final String FIELD_posY_DESC = "I";
        final String FIELD_posZ = isObfuscated ? "field_78921_e" : "posZ";
        final String FIELD_posZ_DESC = "I";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ALOAD, 0));
        instructionToInsert.add(new FieldInsnNode(GETFIELD, HEClasses.WorldRenderer, FIELD_posX, FIELD_posX_DESC));
        instructionToInsert.add(new VarInsnNode(ALOAD, 0));
        instructionToInsert.add(new FieldInsnNode(GETFIELD, HEClasses.WorldRenderer, FIELD_posY, FIELD_posY_DESC));
        instructionToInsert.add(new VarInsnNode(ALOAD, 0));
        instructionToInsert.add(new FieldInsnNode(GETFIELD, HEClasses.WorldRenderer, FIELD_posZ, FIELD_posZ_DESC));
        instructionToInsert.add(new VarInsnNode(ILOAD, 1));
        instructionToInsert.add(new VarInsnNode(ILOAD, 2));
        instructionToInsert.add(new VarInsnNode(ILOAD, 3));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HETessalator,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(0), instructionToInsert);
        HEPlugin.info("Injected " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ".");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }
}
