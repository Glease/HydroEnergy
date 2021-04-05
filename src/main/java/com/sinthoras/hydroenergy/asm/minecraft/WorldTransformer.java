package com.sinthoras.hydroenergy.asm.minecraft;

import com.sinthoras.hydroenergy.asm.HEClasses;
import com.sinthoras.hydroenergy.asm.HEPlugin;
import com.sinthoras.hydroenergy.asm.HEUtil;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.tree.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class WorldTransformer implements IClassTransformer {

    public static final String fullClassName = "net.minecraft.world.World";

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

    public static byte[] transform(byte[] basicClass, boolean isObfuscated) {
        basicClass = transformHandleMaterialAcceleration(basicClass, isObfuscated);
        basicClass = transformIsAnyLiquid(basicClass, isObfuscated);
        return transformSetBlock(basicClass, isObfuscated);
    }

    /* Wrap
     * Block block = this.getBlock(k1, l1, i2);
     * to
     * Block block = HEHooksUtil.getBlockForWorldAndEntity(this.getBlock(k1, l1, i2), l1);
     */
    private static byte[] transformHandleMaterialAcceleration(byte[] basicClass, boolean isObfuscated) {
        final ClassNode classNode = HEUtil.convertByteArrayToClassNode(basicClass);

        final String MARKER_method = isObfuscated ? "func_72918_a" : "handleMaterialAcceleration";
        final String MARKER_method_DESC = "(L" + HEClasses.AxisAlignedBB + ";L" + HEClasses.Material + ";L" + HEClasses.Entity + ";)Z";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
        if(targetMethod == null) {
            HEPlugin.error("Could not find " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ". Water physics are broken!");
            return basicClass;
        }
        HEPlugin.info("Found " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName);

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.World;
        final String MARKER_instruction = isObfuscated ? "func_147439_a" : "getBlock";
        final String MARKER_instruction_DESC = "(III)L" + HEClasses.Block + ";";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 1) {
            HEPlugin.error("Could not find " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in " + fullClassName + ". Water physics are broken!");
            return basicClass;
        }
        HEPlugin.info("Found " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in " + fullClassName);

        final String ADDED_method = "getBlockForWorldAndEntity";
        final String ADDED_method_DESC = "(L" + HEClasses.Block + ";I)L" + HEClasses.Block + ";";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ILOAD, 13));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HEHooksUtil,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(0), instructionToInsert);
        HEPlugin.info("Injected " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ".");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }

    /* Wrap
     * Block block = this.getBlock(k1, l1, i2);
     * to
     * Block block = HEHooksUtil.getBlockForWorldAndEntity(this.getBlock(k1, l1, i2), l1);
     */
    private static byte[] transformIsAnyLiquid(byte[] basicClass, boolean isObfuscated) {
        final ClassNode classNode = HEUtil.convertByteArrayToClassNode(basicClass);

        final String MARKER_method = isObfuscated ? "func_72953_d" : "isAnyLiquid";
        final String MARKER_method_DESC = "(L" + HEClasses.AxisAlignedBB + ";)Z";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
        if(targetMethod == null) {
            HEPlugin.error("Could not find " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ". Entities can't leave water!");
            return basicClass;
        }
        HEPlugin.info("Found " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName);

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.World;
        final String MARKER_instruction = isObfuscated ? "func_147439_a" : "getBlock";
        final String MARKER_instruction_DESC = "(III)L" + HEClasses.Block + ";";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 1) {
            HEPlugin.error("Could not find " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in " + fullClassName + ". Entities can't leave water!");
            return basicClass;
        }
        HEPlugin.info("Found " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in " + fullClassName);

        final String ADDED_method = "getBlockForWorldAndEntity";
        final String ADDED_method_DESC = "(L" + HEClasses.Block + ";I)L" + HEClasses.Block + ";";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ILOAD, 9));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HEHooksUtil,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(0), instructionToInsert);
        HEPlugin.info("Injected " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ".");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }

    /* After
     * boolean flag = chunk.func_150807_a(p_147465_1_ & 15, p_147465_2_, p_147465_3_ & 15, p_147465_4_, p_147465_5_);
     * inject
     * HELightSMPHooks.onSetBlock(this, p_147465_1_, p_147465_2_, p_147465_3_, p_147465_4_, block1);
     */
    private static byte[] transformSetBlock(byte[] basicClass, boolean isObfuscated) {
        final ClassNode classNode = HEUtil.convertByteArrayToClassNode(basicClass);

        final String MARKER_method = isObfuscated ? "func_147465_d" : "setBlock";
        final String MARKER_method_DESC = "(IIIL" + HEClasses.Block + ";II)Z";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
        if(targetMethod == null) {
            HEPlugin.warn("Could not find " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ". You will experience lighting bugs!");
            return basicClass;
        }
        HEPlugin.info("Found " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName);

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.Chunk;
        final String MARKER_instruction = "func_150807_a";
        final String MARKER_instruction_DESC = "(IIIL" + HEClasses.Block + ";I)Z";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 1) {
            HEPlugin.warn("Could not find " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in " + fullClassName + ". You will experience lighting bugs!");
            return basicClass;
        }
        HEPlugin.info("Found " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in " + fullClassName);

        final String ADDED_method = "onSetBlock";
        final String ADDED_method_DESC = "(L" + HEClasses.World + ";IIIL" + HEClasses.Block + ";L" + HEClasses.Block + ";)V";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ALOAD, 0));
        instructionToInsert.add(new VarInsnNode(ILOAD, 1));
        instructionToInsert.add(new VarInsnNode(ILOAD, 2));
        instructionToInsert.add(new VarInsnNode(ILOAD, 3));
        instructionToInsert.add(new VarInsnNode(ALOAD, 4));
        instructionToInsert.add(new VarInsnNode(ALOAD, 8));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HELightSMPHooks,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction and subsequent ASTORE
        targetMethod.instructions.insert(instructions.get(0).getNext(), instructionToInsert);
        HEPlugin.info("Injected " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ".");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }
}
