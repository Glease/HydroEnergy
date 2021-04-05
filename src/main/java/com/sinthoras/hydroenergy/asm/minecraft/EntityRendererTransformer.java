package com.sinthoras.hydroenergy.asm.minecraft;

import com.sinthoras.hydroenergy.asm.HEClasses;
import com.sinthoras.hydroenergy.asm.HEPlugin;
import com.sinthoras.hydroenergy.asm.HEUtil;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.tree.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class EntityRendererTransformer implements IClassTransformer {

    public static final String fullClassName = "net.minecraft.client.renderer.EntityRenderer";

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

    /* After both
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
            HEPlugin.error("Could not find " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ". Water will not be rendered!");
            return basicClass;
        }
        HEPlugin.info("Found " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName);

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.RenderGlobal;
        final String MARKER_instruction = isObfuscated ? "func_147589_a" : "renderEntities";
        final String MARKER_instruction_DESC = "(L" + HEClasses.EntityLivingBase + ";L" + HEClasses.ICamera + ";F)V";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 2) {
            HEPlugin.error("Could not find " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " twice in " + fullClassName + ". Water will not be rendered!");
            return basicClass;
        }
        HEPlugin.info("Found " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " twice in " + fullClassName);

        final String ADDED_method = "render";
        final String ADDED_method_DESC = "(L" + HEClasses.ICamera + ";)V";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ALOAD, 14));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HETessalator,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(0), instructionToInsert);

        instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ALOAD, 14));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HETessalator,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(1), instructionToInsert);
        HEPlugin.info("Injected " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ".");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }
}
