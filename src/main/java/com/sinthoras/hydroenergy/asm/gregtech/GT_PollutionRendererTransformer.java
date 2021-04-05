package com.sinthoras.hydroenergy.asm.gregtech;

import com.sinthoras.hydroenergy.asm.HEClasses;
import com.sinthoras.hydroenergy.asm.HEPlugin;
import com.sinthoras.hydroenergy.asm.HEUtil;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.tree.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class GT_PollutionRendererTransformer implements IClassTransformer {

    public static final String fullClassName = "gregtech.common.render.GT_PollutionRenderer";

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
            HEPlugin.info(fullClassName + " is using HydroEnergy API. No injection necessary.");
            return basicClass;
        }
        HEPlugin.info(fullClassName + " does not use HydroEnergy API. Fallback to injection.");

        final String MARKER_method = "manipulateColor";
        final String MARKER_method_DESC = "(L" + HEClasses.FogColors + ";)V";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
        if(targetMethod == null) {
            HEPlugin.warn("Could not find " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ". You might experience visual bugs.");
            return basicClass;
        }
        HEPlugin.info("Found " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName);

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.Block;
        final String MARKER_instruction = isObfuscated ? "func_149688_o" : "getMaterial";
        final String MARKER_instruction_DESC = "()L" + HEClasses.Material + ";";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 2) {
            HEPlugin.warn("Could not find " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " twice in " + fullClassName + ". You might experience visual bugs.");
            return basicClass;
        }
        HEPlugin.info("Found " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " twice in " + fullClassName);

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
        HEPlugin.info("Fixed mod-interop with " + fullClassName + ".");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }
}
