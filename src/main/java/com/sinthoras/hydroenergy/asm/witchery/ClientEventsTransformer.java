package com.sinthoras.hydroenergy.asm.witchery;

import com.sinthoras.hydroenergy.asm.HEClasses;
import com.sinthoras.hydroenergy.asm.HEPlugin;
import com.sinthoras.hydroenergy.asm.HEUtil;
import org.objectweb.asm.tree.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class ClientEventsTransformer {

    /* Replace
     * block1.getMaterial() == Material.water
     * with
     * HEGetMaterialUtil.getMaterialWrapper(block1, entityplayer.posY + entityplayer.getEyeHeight()) == Material.water
     */
    public static byte[] transform(byte[] basicClass, boolean isObfuscated) {
        final ClassNode classNode = HEUtil.convertByteArrayToClassNode(basicClass);

        final String METHOD_getMaterialHEWrapper = "getMaterialHEWrapper";
        final String METHOD_getMaterialHEWrapper_DESC = "(L" + HEClasses.Block + ";L" + HEClasses.EntityLivingBase + ";)L" + HEClasses.Material + ";";
        final boolean isApiUsed = null != HEUtil.getMethod(classNode, METHOD_getMaterialHEWrapper, METHOD_getMaterialHEWrapper_DESC);
        if(isApiUsed) {
            HEPlugin.info("Witchery is using HydroEnergy API. No injection neccessary.");
            return basicClass;
        }

        final String MARKER_method = "getFOVModifier";
        final String MARKER_method_DESC = "(FL" + HEClasses.EntityRenderer + ";L" + HEClasses.Minecraft + ";)F";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
        if(targetMethod == null) {
            HEPlugin.info("Could not find injection target method in Witchery. You will experience visual bugs.");
            return basicClass;
        }

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.Block;
        final String MARKER_instruction = isObfuscated ? "func_149688_o" : "getMaterial";
        final String MARKER_instruction_DESC = "()L" + HEClasses.Material + ";";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 1) {
            HEPlugin.info("Could not find injection target instruction in Witchery. You will experience visual bugs.");
            return basicClass;
        }

        final String REPLACED_method = "getMaterialWrapper";
        final String REPLACED_method_DESC = "(L" + HEClasses.Block + ";D)L" + HEClasses.Material + ";";
        final String FIELD_posY = isObfuscated ? "field_70163_u" : "posY";
        final String FIELD_posY_DESC = "D";
        final String METHOD_getEyeHeight = isObfuscated ? "func_70047_e" : "getEyeHeight";
        final String METHOD_getEyeHeight_DESC = "()F";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ALOAD, 6));
        instructionToInsert.add(new VarInsnNode(ALOAD, 4));
        instructionToInsert.add(new FieldInsnNode(GETFIELD, HEClasses.Entity, FIELD_posY, FIELD_posY_DESC));
        instructionToInsert.add(new VarInsnNode(ALOAD, 4));
        instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
                HEClasses.EntityLivingBase,
                METHOD_getEyeHeight,
                METHOD_getEyeHeight_DESC,
                false));
        instructionToInsert.add(new InsnNode(F2D));
        instructionToInsert.add(new InsnNode(DADD));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HEGetMaterialUtil,
                REPLACED_method,
                REPLACED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(0), instructionToInsert);
        // Remove ALOAD before target instruction
        targetMethod.instructions.remove(instructions.get(0).getPrevious());
        // Remove target instruction itself
        targetMethod.instructions.remove(instructions.get(0));
        HEPlugin.info("Fixed mod-interop with Witchery.");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }
}
