package com.sinthoras.hydroenergy.asm.minecraft;

import com.sinthoras.hydroenergy.asm.HEClasses;
import com.sinthoras.hydroenergy.asm.HEPlugin;
import com.sinthoras.hydroenergy.asm.HEUtil;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.tree.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class ChunkTransformer implements IClassTransformer {

    public static final String fullClassName = "net.minecraft.world.chunk.Chunk";

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

    public static byte[] transform(byte[] basicClass, boolean isObfuscated) {
        basicClass = transformFillChunk(basicClass, isObfuscated);
        basicClass = transformGenerateSkylightMap(basicClass, isObfuscated);
        basicClass = transformRelightBlock(basicClass, isObfuscated);
        return transformSetLightValue(basicClass, isObfuscated);
    }

    /* After
     * this.generateHeightMap();
     * inject
     * HELightManager.onChunkDataLoad(this);
     */
    private static byte[] transformFillChunk(byte[] basicClass, boolean isObfuscated) {
        final ClassNode classNode = HEUtil.convertByteArrayToClassNode(basicClass);

        final String MARKER_method = isObfuscated ? "func_76607_a" : "fillChunk";
        final String MARKER_method_DESC = "([BIIZ)V";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
        if(targetMethod == null) {
            HEPlugin.error("Could not find method " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ". HydroEnergy will not work!");
            return basicClass;
        }
        HEPlugin.info("Found method " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName);

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.Chunk;
        final String MARKER_instruction = isObfuscated ? "func_76590_a" : "generateHeightMap";
        final String MARKER_instruction_DESC = "()V";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 1) {
            HEPlugin.error("Could not find instruction " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in said method. HydroEnergy will not work!");
            return basicClass;
        }
        HEPlugin.info("Found instruction " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in said method.");

        final String ADDED_method = "onChunkDataLoad";
        final String ADDED_method_DESC = "(L" + HEClasses.Chunk + ";)V";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ALOAD, 0));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HELightManager,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(0), instructionToInsert);
        HEPlugin.info("Injected into " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ".");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }

    /* After
     * extendedblockstorage.setExtSkylightValue(j, i1 & 15, k, l);
     * inject
     * HELightSMPHooks.onLightUpdate(this, j, i1, k);
     */
    private static byte[] transformGenerateSkylightMap(byte[] basicClass, boolean isObfuscated) {
        final ClassNode classNode = HEUtil.convertByteArrayToClassNode(basicClass);

        final String MARKER_method = isObfuscated ? "func_76603_b" : "generateSkylightMap";
        final String MARKER_method_DESC = "()V";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
        if(targetMethod == null) {
            HEPlugin.warn("Could not find method " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ". You will experience severe lighting bugs!");
            return basicClass;
        }
        HEPlugin.info("Found method " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName);

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.ExtendedBlockStorage;
        final String MARKER_instruction = isObfuscated ? "func_76657_c" : "setExtSkylightValue";
        final String MARKER_instruction_DESC = "(IIII)V";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 1) {
            HEPlugin.warn("Could not find instruction " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in said method. You will experience severe lighting bugs!");
            return basicClass;
        }
        HEPlugin.info("Found instruction " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in said method.");

        final String ADDED_method = "onLightUpdate";
        final String ADDED_method_DESC = "(L" + HEClasses.Chunk + ";III)V";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ALOAD, 0));
        instructionToInsert.add(new VarInsnNode(ILOAD, 2));
        instructionToInsert.add(new VarInsnNode(ILOAD, 5));
        instructionToInsert.add(new VarInsnNode(ILOAD, 3));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HELightSMPHooks,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(0), instructionToInsert);
        HEPlugin.info("Injected into " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ".");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }

    /* After both
     * extendedblockstorage.setExtSkylightValue(p_76615_1_, l1 & 15, p_76615_3_, *);
     * inject
     * HELightSMPHooks.onLightUpdate(this, p_76615_1_, l1, p_76615_3_);
     *
     * After
     * extendedblockstorage1.setExtSkylightValue(p_76615_1_, i1 & 15, p_76615_3_, l1);
     * inject
     * HELightSMPHooks.onLightUpdate(this, p_76615_1_, i1, p_76615_3_);
     */
    private static byte[] transformRelightBlock(byte[] basicClass, boolean isObfuscated) {
        final ClassNode classNode = HEUtil.convertByteArrayToClassNode(basicClass);

        final String MARKER_method = isObfuscated ? "func_76615_h" : "relightBlock";
        final String MARKER_method_DESC = "(III)V";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
        if(targetMethod == null) {
            HEPlugin.warn("Could not find method " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ". You will experience severe lighting bugs!");
            return basicClass;
        }
        HEPlugin.info("Found method " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName);

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.ExtendedBlockStorage;
        final String MARKER_instruction = isObfuscated ? "func_76657_c" : "setExtSkylightValue";
        final String MARKER_instruction_DESC = "(IIII)V";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 3) {
            HEPlugin.warn("Could not find instruction " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " thrice in said method. You will experience severe lighting bugs!");
            return basicClass;
        }
        HEPlugin.info("Found instruction " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " thrice in said method.");

        final String ADDED_method = "onLightUpdate";
        final String ADDED_method_DESC = "(L" + HEClasses.Chunk + ";III)V";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ALOAD, 0));
        instructionToInsert.add(new VarInsnNode(ILOAD, 1));
        instructionToInsert.add(new VarInsnNode(ILOAD, 8));
        instructionToInsert.add(new VarInsnNode(ILOAD, 3));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HELightSMPHooks,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(0), instructionToInsert);

        instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ALOAD, 0));
        instructionToInsert.add(new VarInsnNode(ILOAD, 1));
        instructionToInsert.add(new VarInsnNode(ILOAD, 8));
        instructionToInsert.add(new VarInsnNode(ILOAD, 3));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HELightSMPHooks,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(1), instructionToInsert);

        instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ALOAD, 0));
        instructionToInsert.add(new VarInsnNode(ILOAD, 1));
        instructionToInsert.add(new VarInsnNode(ILOAD, 5));
        instructionToInsert.add(new VarInsnNode(ILOAD, 3));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HELightSMPHooks,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(2), instructionToInsert);
        HEPlugin.info("Injected into " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ".");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }

    /* After
     * extendedblockstorage.setExtSkylightValue(p_76633_2_, p_76633_3_ & 15, p_76633_4_, p_76633_5_);
     * inject
     * HELightSMPHooks.onLightUpdate(this, p_76633_2_, p_76633_3_, p_76633_4_);
     */
    private static byte[] transformSetLightValue(byte[] basicClass, boolean isObfuscated) {
        final ClassNode classNode = HEUtil.convertByteArrayToClassNode(basicClass);

        final String MARKER_method = isObfuscated ? "func_76633_a" : "setLightValue";
        final String MARKER_method_DESC = "(L" + HEClasses.EnumSkyBlock + ";IIII)V";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
        if(targetMethod == null) {
            HEPlugin.warn("Could not find method " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ". You will experience severe lighting bugs!");
            return basicClass;
        }
        HEPlugin.info("Found method " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName);

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.ExtendedBlockStorage;
        final String MARKER_instruction = isObfuscated ? "func_76657_c" : "setExtSkylightValue";
        final String MARKER_instruction_DESC = "(IIII)V";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 1) {
            HEPlugin.warn("Could not find instruction " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in said method. You will experience severe lighting bugs!");
            return basicClass;
        }
        HEPlugin.info("Found instruction " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in said method.");

        final String ADDED_method = "onLightUpdate";
        final String ADDED_method_DESC = "(L" + HEClasses.Chunk + ";III)V";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ALOAD, 0));
        instructionToInsert.add(new VarInsnNode(ILOAD, 2));
        instructionToInsert.add(new VarInsnNode(ILOAD, 3));
        instructionToInsert.add(new VarInsnNode(ILOAD, 4));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HELightSMPHooks,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction
        targetMethod.instructions.insert(instructions.get(0), instructionToInsert);
        HEPlugin.info("Injected into " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ".");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }
}
