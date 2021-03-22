package com.sinthoras.hydroenergy.asm;

import com.sinthoras.hydroenergy.HE;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

import net.minecraft.launchwrapper.IClassTransformer;

import java.util.ArrayList;
import java.util.List;

public class HETransformer implements IClassTransformer {
	
	private static final List targetClasses = new ArrayList() {{
		add("net.minecraft.world.World");
		add("net.minecraft.block.Block");
		add("net.minecraft.client.renderer.EntityRenderer");
		add("net.minecraft.entity.Entity");
		add("net.minecraft.client.renderer.RenderGlobal");
		add("net.minecraft.client.renderer.WorldRenderer");
		add("net.minecraft.world.chunk.Chunk");
		add("net.minecraft.client.multiplayer.ChunkProviderClient");
	}};

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		boolean isObfuscated = !name.equals(transformedName);
		int index = targetClasses.indexOf(transformedName);
		return index != -1 ? transform(index, basicClass, isObfuscated) : basicClass;
	}
	
	private static byte[] transform(int index, byte[] basicClass, boolean isObfuscated) {
		switch(index) {
			case 0:
				return transformWorld(basicClass, isObfuscated);
			case 1:
				return transformBlock(basicClass, isObfuscated);
			case 2:
				return transformEntityRenderer(basicClass, isObfuscated);
			case 3:
				return transformEntity(basicClass, isObfuscated);
			case 4:
				return transformRenderGlobal(basicClass, isObfuscated);
			case 5:
				return transformWorldRenderer(basicClass, isObfuscated);
			case 6:
				return transformChunk(basicClass, isObfuscated);
			case 7:
				return transformChunkProviderClient(basicClass, isObfuscated);
			default:
				return basicClass;
		}
	}
	
	private static byte[] transformWorld(byte[] basicClass, boolean isObfuscated) {
		final String CLASS_AxisAlignedBB = "net/minecraft/util/AxisAlignedBB";
		final String CLASS_Material = "net/minecraft/block/material/Material";
		final String CLASS_Entity = "net/minecraft/entity/Entity";
		final String CLASS_Block = "net/minecraft/block/Block";
		final String CLASS_HELightSMPHooks = "com/sinthoras/hydroenergy/client/light/HELightSMPHooks";
		final String CLASS_Chunk = "net/minecraft/world/chunk/Chunk";
		final String CLASS_World = "net/minecraft/world/World";

		final String METHOD_handleMaterialAcceleration = isObfuscated ? "a" : "handleMaterialAcceleration";
		final String METHOD_handleMaterialAcceleration_DESC = "(L" + CLASS_AxisAlignedBB + ";L" + CLASS_Material + ";L" + CLASS_Entity + ";)Z";
		final String METHOD_getMaterial = isObfuscated ? "o" : "getMaterial";
		final String METHOD_getMaterial_DESC = "()L" + CLASS_Material + ";";
		final String METHOD_getMaterial_DESC_NEW = "(I)L" + CLASS_Material + ";";

		final String METHOD_setBlock = isObfuscated ? "d" : "setBlock";
		final String METHOD_setBlock_DESC = "(IIIL" + CLASS_Block + ";II)Z";
		final String METHOD_func_150807_a = isObfuscated ? "a" : "func_150807_a";
		final String METHOD_func_150807_a_DESC = "(IIIL" + CLASS_Block + ";I)Z";
		final String METHOD_onSetBlock = "onSetBlock";
		final String METHOD_onSetBlock_DESC = "(L" + CLASS_World + ";IIIL" + CLASS_Block + ";L" + CLASS_Block + ";)V";

		final String METHOD_isAnyLiquid = isObfuscated ? "d" : "isAnyLiquid";
		final String METHOD_isAnyLiquid_DESC = "(L" + CLASS_AxisAlignedBB + ";)Z";

		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ILOAD, 13));
		instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
				CLASS_Block,
				METHOD_getMaterial,
				METHOD_getMaterial_DESC_NEW,
				false));

		basicClass = injectReplaceInvokeVirtual(METHOD_handleMaterialAcceleration, METHOD_handleMaterialAcceleration_DESC,
				CLASS_Block, METHOD_getMaterial, METHOD_getMaterial_DESC, instructionToInsert, basicClass);

		HE.LOG.info("Injected net/minecraft/world/World.handleMaterialAcceleration");


		instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ALOAD, 0));
		instructionToInsert.add(new VarInsnNode(ILOAD, 1));
		instructionToInsert.add(new VarInsnNode(ILOAD, 2));
		instructionToInsert.add(new VarInsnNode(ILOAD, 3));
		instructionToInsert.add(new VarInsnNode(ALOAD, 4));
		instructionToInsert.add(new VarInsnNode(ALOAD, 8));
		instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
				CLASS_HELightSMPHooks,
				METHOD_onSetBlock,
				METHOD_onSetBlock_DESC,
				false));

		basicClass = injectAfterInvokeVirtual(METHOD_setBlock, METHOD_setBlock_DESC,
				CLASS_Chunk, METHOD_func_150807_a, METHOD_func_150807_a_DESC, instructionToInsert, basicClass, 0);

		HE.LOG.info("Injected net/minecraft/world/World.setBlock");


		instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ILOAD, 9));
		instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
				CLASS_Block,
				METHOD_getMaterial,
				METHOD_getMaterial_DESC_NEW,
				false));

		basicClass = injectReplaceInvokeVirtual(METHOD_isAnyLiquid, METHOD_isAnyLiquid_DESC,
				CLASS_Block, METHOD_getMaterial, METHOD_getMaterial_DESC, instructionToInsert, basicClass);

		HE.LOG.info("Injected net/minecraft/world/World.isAnyLiquid");

		return basicClass;
	}

	private static byte[] transformBlock(byte[] basicClass, boolean isObfuscated) {
		final String CLASS_Block = "net/minecraft/block/Block";
		final String CLASS_Material = "net/minecraft/block/material/Material";
		final String CLASS_EntityLivingBase = "net/minecraft/entity/EntityLivingBase";

		final String METHOD_getMaterial = isObfuscated ? "o" : "getMaterial";
		final String METHOD_getMaterial_DESC = "()L" + CLASS_Material + ";";
		final String METHOD_getMaterial_DESC_NEW_I = "(I)L" + CLASS_Material + ";";
		final String METHOD_getMaterial_DESC_NEW_ELB = "(L" + CLASS_EntityLivingBase + ";)L" + CLASS_Material + ";";
		final String METHOD_getMaterial_DESC_NEW_D = "(D)L" + CLASS_Material + ";";

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(classWriter);

		// public Material getMaterial(Entity entity)
		MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, METHOD_getMaterial, METHOD_getMaterial_DESC_NEW_I, null, null);
		mv.visitVarInsn(ALOAD, 0); // load this
		mv.visitMethodInsn(INVOKEVIRTUAL, CLASS_Block, METHOD_getMaterial, METHOD_getMaterial_DESC, false);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();

		// public Material getMaterial(EntityLivingBase entitylivingbase)
		mv = classWriter.visitMethod(ACC_PUBLIC, METHOD_getMaterial, METHOD_getMaterial_DESC_NEW_ELB, null, null);
		mv.visitVarInsn(ALOAD, 0); // load this
		mv.visitMethodInsn(INVOKEVIRTUAL, CLASS_Block, METHOD_getMaterial, METHOD_getMaterial_DESC, false);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();

		// public Material getMaterial(double y)
		mv = classWriter.visitMethod(ACC_PUBLIC, METHOD_getMaterial, METHOD_getMaterial_DESC_NEW_D, null, null);
		mv.visitVarInsn(ALOAD, 0); // load this
		mv.visitMethodInsn(INVOKEVIRTUAL, CLASS_Block, METHOD_getMaterial, METHOD_getMaterial_DESC, false);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();

		HE.LOG.info("Injected net/minecraft/client/block/Block");

		return classWriter.toByteArray();
	}

	private static byte[] transformEntityRenderer(byte[] basicClass, boolean isObfuscated) {
		final String CLASS_Block = "net/minecraft/block/Block";
		final String CLASS_Material = "net/minecraft/block/material/Material";
		final String CLASS_EntityLivingBase = "net/minecraft/entity/EntityLivingBase";

		final String METHOD_setupFog = isObfuscated ? "a" : "setupFog";
		final String METHOD_setupFog_DESC = "(IF)V";
		final String METHOD_updateFogColor = isObfuscated ? "j" : "updateFogColor";
		final String METHOD_updateFogColor_DESC = "(F)V";
		final String METHOD_getFOVModifier = isObfuscated ? "a" : "getFOVModifier";
		final String METHOD_getFOVModifier_DESC = "(FZ)F";

		final String METHOD_getMaterial = isObfuscated ? "o" : "getMaterial";
		final String METHOD_getMaterial_DESC = "()L" + CLASS_Material + ";";
		final String METHOD_getMaterial_DESC_NEW = "(L" + CLASS_EntityLivingBase + ";)L" + CLASS_Material + ";";

		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ALOAD, 3));
		instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
				CLASS_Block,
				METHOD_getMaterial,
				METHOD_getMaterial_DESC_NEW,
				false));

		basicClass = injectReplaceInvokeVirtual(METHOD_setupFog, METHOD_setupFog_DESC,
				CLASS_Block, METHOD_getMaterial, METHOD_getMaterial_DESC, instructionToInsert, basicClass);

		HE.LOG.info("Injected net/minecraft/client/renderer/EntityRenderer.setupFog");


		instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ALOAD, 3));
		instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
				CLASS_Block,
				METHOD_getMaterial,
				METHOD_getMaterial_DESC_NEW,
				false));

		HE.LOG.info("Injected net/minecraft/client/renderer/EntityRenderer.updateFogColor");

		basicClass = injectReplaceInvokeVirtual(METHOD_updateFogColor, METHOD_updateFogColor_DESC,
				CLASS_Block, METHOD_getMaterial, METHOD_getMaterial_DESC, instructionToInsert, basicClass);


		instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ALOAD, 3));
		instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
				CLASS_Block,
				METHOD_getMaterial,
				METHOD_getMaterial_DESC_NEW,
				false));

		basicClass = injectReplaceInvokeVirtual(METHOD_getFOVModifier, METHOD_getFOVModifier_DESC,
				CLASS_Block, METHOD_getMaterial, METHOD_getMaterial_DESC, instructionToInsert, basicClass);

		HE.LOG.info("Injected net/minecraft/client/renderer/EntityRenderer.getFOVModifier");

		return basicClass;
	}

	private static byte[] transformEntity(byte[] basicClass, boolean isObfuscated) {
		final String CLASS_Block = "net/minecraft/block/Block";
		final String CLASS_Material = "net/minecraft/block/material/Material";

		final String METHOD_isInsideOfMaterial = isObfuscated ? "a" : "isInsideOfMaterial";
		final String METHOD_isInsideOfMaterial_DESC = "(L" + CLASS_Material + ";)Z";
		final String METHOD_getMaterial = isObfuscated ? "o" : "getMaterial";
		final String METHOD_getMaterial_DESC = "()L" + CLASS_Material + ";";
		final String METHOD_getMaterial_DESC_NEW = "(D)L" + CLASS_Material + ";";

		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(DLOAD, 2));
		instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
				CLASS_Block,
				METHOD_getMaterial,
				METHOD_getMaterial_DESC_NEW,
				false));

		basicClass = injectReplaceInvokeVirtual(METHOD_isInsideOfMaterial, METHOD_isInsideOfMaterial_DESC,
				CLASS_Block, METHOD_getMaterial, METHOD_getMaterial_DESC, instructionToInsert, basicClass);

		HE.LOG.info("Injected net/minecraft/client/entity/Entity.isInsideOfMaterial");

		return basicClass;
	}

	private static byte[] transformRenderGlobal(byte[] basicClass, boolean isObfuscated) {
		final String CLASS_ICamera = "net/minecraft/client/renderer/culling/ICamera";
		final String CLASS_EntityLivingBase = "net/minecraft/entity/EntityLivingBase";
		final String CLASS_Profiler = "net/minecraft/profiler/Profiler";
		final String CLASS_HETessalator = "com/sinthoras/hydroenergy/client/renderer/HETessalator";

		final String METHOD_renderEntities = isObfuscated ? "a" : "renderEntities";
		final String METHOD_renderEntities_DESC = "(L" + CLASS_EntityLivingBase + ";L" + CLASS_ICamera + ";F)V";

		final String METHOD_endSection = isObfuscated ? "b" : "endSection";
		final String METHOD_endSection_DESC = "()V";

		final String METHOD_render = "render";
		final String METHOD_render_DESC = "(L" + CLASS_ICamera + ";)V";

		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ALOAD, 2));
		instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
				CLASS_HETessalator,
				METHOD_render,
				METHOD_render_DESC,
				false));

		basicClass = injectAfterInvokeVirtual(METHOD_renderEntities, METHOD_renderEntities_DESC,
				CLASS_Profiler, METHOD_endSection, METHOD_endSection_DESC, instructionToInsert, basicClass, 0);

		HE.LOG.info("Injected net/minecraft/client/renderer/RenderGlobal.renderEntities");

		return basicClass;
	}

	private static byte[] transformWorldRenderer(byte[] basicClass, boolean isObfuscated) {
		final String CLASS_WorldRenderer = "net/minecraft/client/renderer/WorldRenderer";
		final String CLASS_HETessalator = "com/sinthoras/hydroenergy/client/renderer/HETessalator";

		final String METHOD_setPosition = isObfuscated ? "a" : "setPosition";
		final String METHOD_setPosition_DESC = "(III)V";

		final String METHOD_setDontDraw = isObfuscated ? "a" : "setDontDraw";
		final String METHOD_setDontDraw_DESC = "()V";

		final String METHOD_onRenderChunkUpdate = "onRenderChunkUpdate";
		final String METHOD_onRenderChunkUpdate_DESC = "(IIIIII)V";

		final String FIELD_posX = isObfuscated ? "c" : "posX";
		final String FIELD_posX_DESC = "I";
		final String FIELD_posY = isObfuscated ? "d" : "posY";
		final String FIELD_posY_DESC = "I";
		final String FIELD_posZ = isObfuscated ? "e" : "posZ";
		final String FIELD_posZ_DESC = "I";

		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ALOAD, 0));
		instructionToInsert.add(new FieldInsnNode(GETFIELD, CLASS_WorldRenderer, FIELD_posX, FIELD_posX_DESC));
		instructionToInsert.add(new VarInsnNode(ALOAD, 0));
		instructionToInsert.add(new FieldInsnNode(GETFIELD, CLASS_WorldRenderer, FIELD_posY, FIELD_posY_DESC));
		instructionToInsert.add(new VarInsnNode(ALOAD, 0));
		instructionToInsert.add(new FieldInsnNode(GETFIELD, CLASS_WorldRenderer, FIELD_posZ, FIELD_posZ_DESC));
		instructionToInsert.add(new VarInsnNode(ILOAD, 1));
		instructionToInsert.add(new VarInsnNode(ILOAD, 2));
		instructionToInsert.add(new VarInsnNode(ILOAD, 3));
		instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
				CLASS_HETessalator,
				METHOD_onRenderChunkUpdate,
				METHOD_onRenderChunkUpdate_DESC,
				false));

		basicClass = injectAfterInvokeVirtual(METHOD_setPosition, METHOD_setPosition_DESC,
				CLASS_WorldRenderer, METHOD_setDontDraw, METHOD_setDontDraw_DESC,
				instructionToInsert, basicClass, 0);

		HE.LOG.info("Injected net/minecraft/client/renderer/WorldRenderer.setPosition");

		return basicClass;
	}

	private static byte[] transformChunk(byte[] basicClass, boolean isObfuscated) {
		final String CLASS_Chunk = "net/minecraft/world/chunk/Chunk";
		final String CLASS_HELightManager = "com/sinthoras/hydroenergy/client/light/HELightManager";
		final String CLASS_HELightSMPHooks = "com/sinthoras/hydroenergy/client/light/HELightSMPHooks";
		final String CLASS_EnumSkyBlock = "net/minecraft/world/EnumSkyBlock";
		final String CLASS_ExtendedBlockStorage = "net/minecraft/world/chunk/storage/ExtendedBlockStorage";

		final String METHOD_fillChunk = isObfuscated ? "a" : "fillChunk";
		final String METHOD_fillChunk_DESC = "([BIIZ)V";
		final String METHOD_generateSkylightMap = isObfuscated ? "b" : "generateSkylightMap";
		final String METHOD_generateSkylightMap_DESC = "()V";
		final String METHOD_relightBlock = isObfuscated ? "h" : "relightBlock";
		final String METHOD_relightBlock_DESC = "(III)V";
		final String METHOD_setLightValue = isObfuscated ? "a" : "setLightValue";
		final String METHOD_setLightValue_DESC = "(L" + CLASS_EnumSkyBlock + ";IIII)V";

		final String METHOD_onChunkDataLoad = "onChunkDataLoad";
		final String METHOD_onChunkDataLoad_DESC = "(L" + CLASS_Chunk + ";)V";
		final String METHOD_onLightUpdate = "onLightUpdate";
		final String METHOD_onLightUpdate_DESC = "(L" + CLASS_Chunk + ";III)V";

		final String METHOD_generateHeightMap = isObfuscated ? "a" : "generateHeightMap";
		final String METHOD_generateHeightMap_DESC = "()V";
		final String METHOD_setExtSkylightValue = isObfuscated ? "b" : "setExtSkylightValue";
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

		HE.LOG.info("Injected net/minecraft/world/chunk/Chunk.fillChunk");


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

		HE.LOG.info("Injected net/minecraft/world/chunk/Chunk.generateSkylightMap");


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

		HE.LOG.info("Injected net/minecraft/world/chunk/Chunk.relightBlock#1");


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

		HE.LOG.info("Injected net/minecraft/world/chunk/Chunk.relightBlock#2");

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

		HE.LOG.info("Injected net/minecraft/world/chunk/Chunk.relightBlock#3");


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

		HE.LOG.info("Injected net/minecraft/world/chunk/Chunk.setLightValue");

		return basicClass;
	}

	private static byte [] transformChunkProviderClient(byte[] basicClass, boolean isObfuscated) {
		final String CLASS_HELightManager = "com/sinthoras/hydroenergy/client/light/HELightManager";
		final String CLASS_LongHashMap = "net/minecraft/util/LongHashMap";

		final String METHOD_unloadChunk = isObfuscated ? "b" : "unloadChunk";
		final String METHOD_unloadChunk_DESC = "(II)V";
		final String METHOD_remove = isObfuscated ? "d" : "remove";
		final String METHOD_remove_DESC = "(J)Ljava/lang/Object;";
		final String METHOD_onChunkUnload = "onChunkUnload";
		final String METHOD_onChunkUnload_DESC = "(II)V";

		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ILOAD, 1));
		instructionToInsert.add(new VarInsnNode(ILOAD, 2));
		instructionToInsert.add(new MethodInsnNode(INVOKESTATIC,
				CLASS_HELightManager,
				METHOD_onChunkUnload,
				METHOD_onChunkUnload_DESC,
				false));

		basicClass = injectAfterInvokeVirtual(METHOD_unloadChunk, METHOD_unloadChunk_DESC,
				CLASS_LongHashMap, METHOD_remove, METHOD_remove_DESC, instructionToInsert, basicClass, 0);

		HE.LOG.info("Injected net/minecraft/client/multiplayer/ChunkProviderClient.unloadChunk");

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

	private static byte[] injectReplaceInvokeVirtual(String METHOD_target, String METHOD_target_DESC,
												   String CLASS_marker, String METHOD_marker, String METHOD_marker_DESC,
												   InsnList instructionToInsert, byte[] basicClass) {
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
						AbstractInsnNode insertAfter = instruction.getPrevious();
						method.instructions.remove(instruction);
						method.instructions.insert(insertAfter, instructionToInsert);
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
