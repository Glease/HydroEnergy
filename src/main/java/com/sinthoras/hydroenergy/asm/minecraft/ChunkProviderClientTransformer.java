package com.sinthoras.hydroenergy.asm.minecraft;

import com.sinthoras.hydroenergy.asm.HEClasses;
import com.sinthoras.hydroenergy.asm.HEPlugin;
import com.sinthoras.hydroenergy.asm.HEUtil;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.tree.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class ChunkProviderClientTransformer implements IClassTransformer {

    public static final String fullClassName = "net.minecraft.client.multiplayer.ChunkProviderClient";

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
     * this.chunkMapping.remove(ChunkCoordIntPair.chunkXZ2Int(p_73234_1_, p_73234_2_));
     * insert
     * HELightManager.onChunkUnload(p_73234_1_, p_73234_2_);
     */
    public static byte[] transform(byte[] basicClass, boolean isObfuscated) {
        final ClassNode classNode = HEUtil.convertByteArrayToClassNode(basicClass);

        final String MARKER_method = isObfuscated ? "func_73234_b" : "unloadChunk";
        final String MARKER_method_DESC = "(II)V";
        final MethodNode targetMethod = HEUtil.getMethod(classNode, MARKER_method, MARKER_method_DESC);
        if(targetMethod == null) {
            HEPlugin.error("Could not find " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ". HydroEnergy will not work!");
            return basicClass;
        }
        HEPlugin.info("Found " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName);

        final boolean isStatic = false;
        final String MARKER_instruction_OWNER = HEClasses.LongHashMap;
        final String MARKER_instruction = isObfuscated ? "func_76159_d" : "remove";
        final String MARKER_instruction_DESC = "(J)L" + HEClasses.Object + ";";
        List<MethodInsnNode> instructions = HEUtil.getInstructions(targetMethod, isStatic, MARKER_instruction_OWNER, MARKER_instruction, MARKER_instruction_DESC);
        if(instructions.size() != 1) {
            HEPlugin.error("Could not find " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in " + fullClassName + ". HydroEnergy will not work!");
            return basicClass;
        }
        HEPlugin.info("Found " + MARKER_instruction_OWNER + "." + MARKER_instruction + ":" + MARKER_instruction_DESC + " in " + fullClassName);

        final String ADDED_method = "onChunkUnload";
        final String ADDED_method_DESC = "(II)V";
        InsnList instructionToInsert = new InsnList();
        instructionToInsert.add(new VarInsnNode(ILOAD, 1));
        instructionToInsert.add(new VarInsnNode(ILOAD, 2));
        instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
                HEClasses.HELightManager,
                ADDED_method,
                ADDED_method_DESC,
                false));
        // Add instruction after target instruction and a subsequent POP instruction
        targetMethod.instructions.insert(instructions.get(0).getNext(), instructionToInsert);
        HEPlugin.info("Injected " + MARKER_method + ":" + MARKER_method_DESC + " in " + fullClassName + ".");

        return HEUtil.convertClassNodeToByteArray(classNode);
    }
}
