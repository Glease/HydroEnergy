package com.sinthoras.hydroenergy.asm.minecraft;

import com.sinthoras.hydroenergy.asm.HEClasses;
import com.sinthoras.hydroenergy.asm.HEPlugin;
import com.sinthoras.hydroenergy.asm.HEUtil;
import org.objectweb.asm.tree.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class EntityRendererTransformer {

    /* After (2x)
     * renderglobal.renderEntities(entitylivingbase, frustrum, p_78471_1_);
     * insert
     * HETessalator.render(frustrum);
     */
    public static byte[] transform(byte[] basicClass, boolean isObfuscated) {
        final ClassNode classNode = HEUtil.convertByteArrayToClassNode(basicClass);

        final String MARKER_method = isObfuscated ? "func_78471_a" : "renderWorld";
        final String MARKER_method_DESC = "(FJ)V";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
        if(targetMethod == null) {
            HEPlugin.error("Could not find injection target method in EntityRenderer. HydroEnergy will not work.");
            return basicClass;
        }

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.RenderGlobal;
        final String MARKER_instruction = isObfuscated ? "func_147589_a" : "renderEntities";
        final String MARKER_instruction_DESC = "(L" + HEClasses.EntityLivingBase + ";L" + HEClasses.ICamera + ";F)V";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 2) {
            HEPlugin.error("Could not find injection target instruction in EntityRenderer. HydroEnergy will not work.");
            return basicClass;
        }

        final String ADDED_method = "render";
        final String ADDED_method_DESC = "(L" + HEClasses.ICamera + ";)V";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ALOAD, 14));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HEHooksUtil,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(0), instructionToInsert);

        instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ALOAD, 14));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HEHooksUtil,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(1), instructionToInsert);
        HEPlugin.info("Injected EntityRenderer.");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }
}
