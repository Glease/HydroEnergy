package com.sinthoras.hydroenergy.asm.gregtech;

import com.sinthoras.hydroenergy.asm.HEClasses;
import com.sinthoras.hydroenergy.asm.HEPlugin;
import com.sinthoras.hydroenergy.asm.HEUtil;
import org.objectweb.asm.tree.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class GT_PollutionRendererTransformer {

    /* Replace
     * event.block.getMaterial() == Material.water
     * with
     * HEGetMaterialUtil.getMaterialWrapper(event) == Material.water
     */
    public static byte[] transform(byte[] basicClass, boolean isObfuscated) {
        final ClassNode classNode = HEUtil.convertByteArrayToClassNode(basicClass);

        final String METHOD_getMaterialHEWrapper = "getMaterialHEWrapper";
        final String METHOD_getMaterialHEWrapper_DESC = "(L" + HEClasses.FogColors + ";)L" + HEClasses.Material + ";";
        final boolean isApiUsed = null != HEUtil.getMethod(classNode, METHOD_getMaterialHEWrapper, METHOD_getMaterialHEWrapper_DESC);
        if(isApiUsed) {
            HEPlugin.info("GregTech is using HydroEnergy API. No injection neccessary.");
            return basicClass;
        }

        final String MARKER_method = "manipulateColor";
        final String MARKER_method_DESC = "(L" + HEClasses.FogColors + ";)V";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
        if(targetMethod == null) {
            HEPlugin.info("Could not find injection target method in GregTech. You will experience visual bugs.");
            return basicClass;
        }

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.Block;
        final String MARKER_instruction = isObfuscated ? "func_149688_o" : "getMaterial";
        final String MARKER_instruction_DESC = "()L" + HEClasses.Material + ";";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 2) {
            HEPlugin.info("Could not find injection target instruction in GregTech. You will experience visual bugs.");
            return basicClass;
        }

        final String REPLACED_method = "getMaterialWrapper";
        final String REPLACED_method_DESC = "(L" + HEClasses.FogColors + ";)L" + HEClasses.Material + ";";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HEGetMaterialUtil,
                REPLACED_method,
                REPLACED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(0), instructionToInsert);
        // Remove GETFIELD before target instruction
        targetMethod.instructions.remove(instructions.get(0).getPrevious());
        // Remove target instruction itself
        targetMethod.instructions.remove(instructions.get(0));
        HEPlugin.info("Fixed mod-interop with GregTech.");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }
}
