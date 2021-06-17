package com.sinthoras.hydroenergy.asm.minecraft;

import com.sinthoras.hydroenergy.asm.HEClasses;
import com.sinthoras.hydroenergy.asm.HEPlugin;
import com.sinthoras.hydroenergy.asm.HEUtil;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class EntityTransformer implements IClassTransformer {

    public static final String fullClassName = "net.minecraft.entity.Entity";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        final boolean isDeobfuscated = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
        if(transformedName.equals(fullClassName)) {
            return transform(basicClass, !isDeobfuscated);
        }
        else {
            return basicClass;
        }
    }

    /* Wrap
     * Block block = this.worldObj.getBlock(i, j, k);
     * to
     * Block block = HEHooksUtil.getBlockForWorldAndEntity(this.worldObj.getBlock(i, j, k), j);
     */
    public static byte[] transform(byte[] basicClass, boolean isObfuscated) {
        final ClassNode classNode = HEUtil.convertByteArrayToClassNode(basicClass);

        final String MARKER_method = isObfuscated ? "func_70055_a" : "isInsideOfMaterial";
        final String MARKER_method_DESC = "(L" + HEClasses.Material + ";)Z";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
        if(targetMethod == null) {
            HEPlugin.error("Could not find method " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ". Entity interaction is broken!");
            return basicClass;
        }
        HEPlugin.info("Found method " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName);

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.World;
        final String MARKER_instruction = isObfuscated ? "func_147439_a" : "getBlock";
        final String MARKER_instruction_DESC = "(III)L" + HEClasses.Block + ";";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 1) {
            HEPlugin.error("Could not find instruction " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in said method. Entity interaction is broken!");
            return basicClass;
        }
        HEPlugin.info("Found instruction " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in said method.");

        final String ADDED_method = "getBlockForWorldAndEntity";
        final String ADDED_method_DESC = "(L" + HEClasses.Block + ";I)L" + HEClasses.Block + ";";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ILOAD, 5));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HEHooksUtil,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(0), instructionToInsert);
        HEPlugin.info("Injected into " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ".");

        // Thermos is a magical creature. If i instruct the ClassWriter to COMPUTE_MAXS and COMPUTE_FRAMES it crashes.
        // Otherwise: return HEUtil.convertClassNodeToByteArray(classNode);
        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
