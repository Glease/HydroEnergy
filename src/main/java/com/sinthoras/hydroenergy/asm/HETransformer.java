package com.sinthoras.hydroenergy.asm;

import com.sinthoras.hydroenergy.asm.biomesoplenty.FogHandlerTransformer;
import com.sinthoras.hydroenergy.asm.galaxyspace.GSPlanetFogHandlerTransformer;
import com.sinthoras.hydroenergy.asm.gregtech.GT_PollutionRendererTransformer;
import com.sinthoras.hydroenergy.asm.minecraft.*;
import com.sinthoras.hydroenergy.asm.witchery.ClientEventsTransformer;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

import net.minecraft.launchwrapper.IClassTransformer;

import java.util.ArrayList;
import java.util.List;

public class HETransformer implements IClassTransformer {
	
	private static final List<String> targetClasses = new ArrayList<String>() {{
		add("net.minecraft.world.World");
		add("net.minecraft.client.renderer.EntityRenderer");
		add("net.minecraft.entity.Entity");
		add("net.minecraft.client.renderer.WorldRenderer");
		add("net.minecraft.world.chunk.Chunk");
		add("net.minecraft.client.multiplayer.ChunkProviderClient");
		add("net.minecraft.client.renderer.ActiveRenderInfo");
		add("biomesoplenty.client.fog.FogHandler");
		add("gregtech.common.render.GT_PollutionRenderer");
		add("galaxyspace.core.handler.GSPlanetFogHandler");
		add("com.emoniph.witchery.client.ClientEvents");
	}};

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		boolean isDeobfuscated = (boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
		int index = targetClasses.indexOf(transformedName);
		return index != -1 ? transform(index, basicClass, !isDeobfuscated) : basicClass;
	}
	
	private static byte[] transform(int index, byte[] basicClass, boolean isObfuscated) {
		switch(index) {
			case 0:
				return WorldTransformer.transform(basicClass, isObfuscated);
			case 1:
				return EntityRendererTransformer.transform(basicClass, isObfuscated);
			case 2:
				return EntityTransformer.transform(basicClass, isObfuscated);
			case 3:
				return WorldRendererTransformer.transform(basicClass, isObfuscated);
			case 4:
				return transformChunk(basicClass, isObfuscated);
			case 5:
				return ChunkProviderClientTransformer.transform(basicClass, isObfuscated);
			case 6:
				return ActiveRenderInfoTransformer.transform(basicClass, isObfuscated);
			case 7:
				return FogHandlerTransformer.transform(basicClass, isObfuscated);
			case 8:
				return GT_PollutionRendererTransformer.transform(basicClass, isObfuscated);
			case 9:
				return GSPlanetFogHandlerTransformer.transform(basicClass, isObfuscated);
			case 10:
				return ClientEventsTransformer.transform(basicClass, isObfuscated);
			default:
				return basicClass;
		}
	}

	private static byte[] transformChunk(byte[] basicClass, boolean isObfuscated) {
		final String CLASS_Chunk = "net/minecraft/world/chunk/Chunk";
		final String CLASS_HELightManager = "com/sinthoras/hydroenergy/client/light/HELightManager";
		final String CLASS_HELightSMPHooks = "com/sinthoras/hydroenergy/client/light/HELightSMPHooks";
		final String CLASS_EnumSkyBlock = "net/minecraft/world/EnumSkyBlock";
		final String CLASS_ExtendedBlockStorage = "net/minecraft/world/chunk/storage/ExtendedBlockStorage";

		final String METHOD_fillChunk = isObfuscated ? "func_76607_a" : "fillChunk";
		final String METHOD_fillChunk_DESC = "([BIIZ)V";
		final String METHOD_generateSkylightMap = isObfuscated ? "func_76603_b" : "generateSkylightMap";
		final String METHOD_generateSkylightMap_DESC = "()V";
		final String METHOD_relightBlock = isObfuscated ? "func_76615_h" : "relightBlock";
		final String METHOD_relightBlock_DESC = "(III)V";
		final String METHOD_setLightValue = isObfuscated ? "func_76633_a" : "setLightValue";
		final String METHOD_setLightValue_DESC = "(L" + CLASS_EnumSkyBlock + ";IIII)V";

		final String METHOD_onChunkDataLoad = "onChunkDataLoad";
		final String METHOD_onChunkDataLoad_DESC = "(L" + CLASS_Chunk + ";)V";
		final String METHOD_onLightUpdate = "onLightUpdate";
		final String METHOD_onLightUpdate_DESC = "(L" + CLASS_Chunk + ";III)V";

		final String METHOD_generateHeightMap = isObfuscated ? "func_76590_a" : "generateHeightMap";
		final String METHOD_generateHeightMap_DESC = "()V";
		final String METHOD_setExtSkylightValue = isObfuscated ? "func_76657_c" : "setExtSkylightValue";
		final String METHOD_setExtSkylightValue_DESC = "(IIII)V";

		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ALOAD, 0));
		instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
				CLASS_HELightManager,
				METHOD_onChunkDataLoad,
				METHOD_onChunkDataLoad_DESC,
				false));

		basicClass = injectAfterInvokeVirtual(METHOD_fillChunk, METHOD_fillChunk_DESC,
				CLASS_Chunk, METHOD_generateHeightMap, METHOD_generateHeightMap_DESC,
				instructionToInsert, basicClass, 0);

		HEPlugin.info("Injected net/minecraft/world/chunk/Chunk.fillChunk");


		instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ALOAD, 0));
		instructionToInsert.add(new VarInsnNode(ILOAD, 2));
		instructionToInsert.add(new VarInsnNode(ILOAD, 5));
		instructionToInsert.add(new VarInsnNode(ILOAD, 3));
		instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
				CLASS_HELightSMPHooks,
				METHOD_onLightUpdate,
				METHOD_onLightUpdate_DESC,
				false));

		basicClass = injectAfterInvokeVirtual(METHOD_generateSkylightMap, METHOD_generateSkylightMap_DESC,
				CLASS_ExtendedBlockStorage, METHOD_setExtSkylightValue, METHOD_setExtSkylightValue_DESC,
				instructionToInsert, basicClass, 0);

		HEPlugin.info("Injected net/minecraft/world/chunk/Chunk.generateSkylightMap");


		instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ALOAD, 0));
		instructionToInsert.add(new VarInsnNode(ILOAD, 1));
		instructionToInsert.add(new VarInsnNode(ILOAD, 8));
		instructionToInsert.add(new VarInsnNode(ILOAD, 3));
		instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
				CLASS_HELightSMPHooks,
				METHOD_onLightUpdate,
				METHOD_onLightUpdate_DESC,
				false));

		basicClass = injectAfterInvokeVirtual(METHOD_relightBlock, METHOD_relightBlock_DESC,
				CLASS_ExtendedBlockStorage, METHOD_setExtSkylightValue, METHOD_setExtSkylightValue_DESC,
				instructionToInsert, basicClass, 0);

		HEPlugin.info("Injected net/minecraft/world/chunk/Chunk.relightBlock#1");


		instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ALOAD, 0));
		instructionToInsert.add(new VarInsnNode(ILOAD, 1));
		instructionToInsert.add(new VarInsnNode(ILOAD, 8));
		instructionToInsert.add(new VarInsnNode(ILOAD, 3));
		instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
				CLASS_HELightSMPHooks,
				METHOD_onLightUpdate,
				METHOD_onLightUpdate_DESC,
				false));

		basicClass = injectAfterInvokeVirtual(METHOD_relightBlock, METHOD_relightBlock_DESC,
				CLASS_ExtendedBlockStorage, METHOD_setExtSkylightValue, METHOD_setExtSkylightValue_DESC,
				instructionToInsert, basicClass, 1);

		HEPlugin.info("Injected net/minecraft/world/chunk/Chunk.relightBlock#2");

		instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ALOAD, 0));
		instructionToInsert.add(new VarInsnNode(ILOAD, 1));
		instructionToInsert.add(new VarInsnNode(ILOAD, 5));
		instructionToInsert.add(new VarInsnNode(ILOAD, 3));
		instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
				CLASS_HELightSMPHooks,
				METHOD_onLightUpdate,
				METHOD_onLightUpdate_DESC,
				false));

		basicClass = injectAfterInvokeVirtual(METHOD_relightBlock, METHOD_relightBlock_DESC,
				CLASS_ExtendedBlockStorage, METHOD_setExtSkylightValue, METHOD_setExtSkylightValue_DESC,
				instructionToInsert, basicClass, 2);

		HEPlugin.info("Injected net/minecraft/world/chunk/Chunk.relightBlock#3");


		instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ALOAD, 0));
		instructionToInsert.add(new VarInsnNode(ILOAD, 2));
		instructionToInsert.add(new VarInsnNode(ILOAD, 3));
		instructionToInsert.add(new VarInsnNode(ILOAD, 4));
		instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
				CLASS_HELightSMPHooks,
				METHOD_onLightUpdate,
				METHOD_onLightUpdate_DESC,
				false));

		basicClass = injectAfterInvokeVirtual(METHOD_setLightValue, METHOD_setLightValue_DESC,
				CLASS_ExtendedBlockStorage, METHOD_setExtSkylightValue, METHOD_setExtSkylightValue_DESC,
				instructionToInsert, basicClass, 0);

		HEPlugin.info("Injected net/minecraft/world/chunk/Chunk.setLightValue");

		return basicClass;
	}

	private static byte[] injectAfterInvokeVirtual(String METHOD_target, String METHOD_target_DESC,
												   String CLASS_marker, String METHOD_marker, String METHOD_marker_DESC,
												   InsnList instructionToInsert, byte[] basicClass, int skip) {
		// Transform to human readable byte code
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);

		for (MethodNode method : classNode.methods) {
			if (method.name.equals(METHOD_target) && method.desc.equals(METHOD_target_DESC)) {
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode) instruction).owner.equals(CLASS_marker)
							&& ((MethodInsnNode) instruction).name.equals(METHOD_marker)
							&& ((MethodInsnNode) instruction).desc.equals(METHOD_marker_DESC)) {
						if(skip <= 0) {
							method.instructions.insert(instruction, instructionToInsert);
							break;
						}
						else {
							skip--;
						}
					}
				}
			}
		}

		// Transform back into pure machine code
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(classWriter);
		return classWriter.toByteArray();
	}
}
