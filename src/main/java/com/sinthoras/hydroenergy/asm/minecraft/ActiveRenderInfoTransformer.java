package com.sinthoras.hydroenergy.asm.minecraft;

import com.sinthoras.hydroenergy.asm.HEClasses;
import com.sinthoras.hydroenergy.asm.HEPlugin;
import com.sinthoras.hydroenergy.asm.HEUtil;
import org.objectweb.asm.tree.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class ActiveRenderInfoTransformer {

    /* Change
     * Block block = p_151460_0_.getBlock(chunkposition.chunkPosX, chunkposition.chunkPosY, chunkposition.chunkPosZ);
     * to
     * Block block = HEHooksUtil.getBlockForActiveRenderInfo(p_151460_0_.getBlock(chunkposition.chunkPosX, chunkposition.chunkPosY, chunkposition.chunkPosZ), vec3);
     */
    public static byte[] transform(byte[] basicClass, boolean isObfuscated) {
        final ClassNode classNode = HEUtil.convertByteArrayToClassNode(basicClass);

        final String MARKER_method = isObfuscated ? "func_151460_a" : "getBlockAtEntityViewpoint";
        final String MARKER_method_DESC = "(L" + HEClasses.World + ";L" + HEClasses.EntityLivingBase + ";F)L" + HEClasses.Block + ";";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
            if(targetMethod == null) {
            HEPlugin.info("Could not find injection target method in ActiveRenderInfo. You will experience visual bugs.");
            return basicClass;
        }

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.World;
        final String MARKER_instruction = isObfuscated ? "func_147439_a" : "getBlock";
        final String MARKER_instruction_DESC = "(III)L" + HEClasses.Block + ";";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
            if(instructions.size() != 2) {
            HEPlugin.info("Could not find injection target instruction in ActiveRenderInfo. You will experience visual bugs.");
            return basicClass;
        }

        final String ADDED_method = "getBlockForActiveRenderInfo";
        final String ADDED_method_DESC = "(L" + HEClasses.Block + ";L" + HEClasses.Vec3 + ";)L" + HEClasses.Block + ";";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ALOAD, 3));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HEHooksUtil,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(0), instructionToInsert);
        HEPlugin.info("Injected ActiveRenderInfo.");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }
}
