package com.sinthoras.hydroenergy.asm;

import com.sinthoras.hydroenergy.HE;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

import net.minecraft.launchwrapper.IClassTransformer;
import scala.actors.threadpool.Arrays;

import java.util.List;

public class EntityWaterLevelTransformer implements IClassTransformer {
	
	private static final List targetClasses = Arrays.asList(new String[] {
			"net.minecraft.world.World",
			"net.minecraft.block.Block",
			"net.minecraft.client.renderer.EntityRenderer",
			"net.minecraft.entity.Entity",
			"net.minecraft.client.renderer.RenderGlobal",
			"net.minecraft.client.renderer.WorldRenderer"});

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
			default:
				return basicClass;
		}
	}
	
	private static byte[] transformWorld(byte[] basicClass, boolean isObfuscated) {
		final String TARGET_METHOD = isObfuscated ? "a" : "handleMaterialAcceleration";
		final String TARGET_DESC = isObfuscated ? "(Lazt;Lawt;Lsa;)Z" : "(Lnet/minecraft/util/AxisAlignedBB;Lnet/minecraft/block/material/Material;Lnet/minecraft/entity/Entity;)Z";
		final String CALL_OWNER = isObfuscated ? "aji" : "net/minecraft/block/Block";
		final String CALL_NAME = isObfuscated ? "o" : "getMaterial";
		final String CALL_DESC = isObfuscated ? "()Lawt;" : "()Lnet/minecraft/block/material/Material;";
		final String CALL_NEW_DESC = isObfuscated ? "(I)Lawt;" : "(I)Lnet/minecraft/block/material/Material;";

		// Transform to human readable byte code
		ClassNode worldClass = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(worldClass, 0);

		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ILOAD, 13));
		instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
				CALL_OWNER,
				CALL_NAME,
				CALL_NEW_DESC,
				false));

		for(MethodNode method : worldClass.methods) {
			if(method.name.equals(TARGET_METHOD) && method.desc.equals(TARGET_DESC)) {
				for(AbstractInsnNode instruction : method.instructions.toArray()) {
					if(instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode)instruction).owner.equals(CALL_OWNER)
							&& ((MethodInsnNode)instruction).name.equals(CALL_NAME)
							&& ((MethodInsnNode)instruction).desc.equals(CALL_DESC)) {
						AbstractInsnNode insertAfter = instruction.getPrevious();
						method.instructions.remove(instruction);
						method.instructions.insert(insertAfter, instructionToInsert);
						HE.LOG.info("Successfully injected net.minecraft.world.World.handleMaterialAcceleration");
						break;
					}
				}
			}
		}

		// Transform back into pure machine code
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		worldClass.accept(classWriter);
		return classWriter.toByteArray();
	}

	private static byte[] transformBlock(byte[] basicClass, boolean isObfuscated) {
		final String CALL_OWNER = isObfuscated ? "aji" : "net/minecraft/block/Block";
		final String CALL_NAME = isObfuscated ? "o" : "getMaterial";
		final String CALL_DESC = isObfuscated ? "()Lawt;" : "()Lnet/minecraft/block/material/Material;";
		final String CALL_NEW_I_DESC = isObfuscated ? "(I)Lawt;" : "(I)Lnet/minecraft/block/material/Material;";
		final String CALL_NEW_ELB_DESC = isObfuscated ? "(Lsv;)Lawt;" : "(Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/block/material/Material;";
		final String CALL_NEW_D_DESC = isObfuscated ? "(D)Lawt;" : "(D)Lnet/minecraft/block/material/Material;";

		ClassNode blockClass = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(blockClass, 0);
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		blockClass.accept(classWriter);

		// public Material getMaterial(Entity entity)
		MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, CALL_NAME, CALL_NEW_I_DESC, null, null);
		mv.visitVarInsn(ALOAD, 0); // load this
		mv.visitMethodInsn(INVOKEVIRTUAL, CALL_OWNER, CALL_NAME, CALL_DESC, false);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();

		// public Material getMaterial(EntityLivingBase entitylivingbase)
		mv = classWriter.visitMethod(ACC_PUBLIC, CALL_NAME, CALL_NEW_ELB_DESC, null, null);
		mv.visitVarInsn(ALOAD, 0); // load this
		mv.visitMethodInsn(INVOKEVIRTUAL, CALL_OWNER, CALL_NAME, CALL_DESC, false);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();

		// public Material getMaterial(double y)
		mv = classWriter.visitMethod(ACC_PUBLIC, CALL_NAME, CALL_NEW_D_DESC, null, null);
		mv.visitVarInsn(ALOAD, 0); // load this
		mv.visitMethodInsn(INVOKEVIRTUAL, CALL_OWNER, CALL_NAME, CALL_DESC, false);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();

		HE.LOG.info("Successfully injected net.minecraft.client.block.Block");

		return classWriter.toByteArray();
	}

	private static byte[] transformEntityRenderer(byte[] basicClass, boolean isObfuscated) {
		final String TARGET_METHOD_setupFog = isObfuscated ? "a" : "setupFog";
		final String TARGET_DESC_setupFog = "(IF)V";
		final String TARGET_METHOD_updateFogColor = isObfuscated ? "j" : "updateFogColor";
		final String TARGET_DESC_updateFogColor = "(F)V";
		final String TARGET_METHOD_getFOVModifier = isObfuscated ? "a" : "getFOVModifier";
		final String TARGET_DESC_getFOVModifier = "(FZ)F";


		final String CALL_OWNER = isObfuscated ? "aji" : "net/minecraft/block/Block";
		final String CALL_NAME = isObfuscated ? "o" : "getMaterial";
		final String CALL_DESC = isObfuscated ? "()Lawt;" : "()Lnet/minecraft/block/material/Material;";
		final String CALL_NEW_DESC = isObfuscated ? "(Lsv;)Lawt;" : "(Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/block/material/Material;";

		// Transform to human readable byte code
		ClassNode worldClass = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(worldClass, 0);

		for (MethodNode method : worldClass.methods) {
			if (method.name.equals(TARGET_METHOD_setupFog) && method.desc.equals(TARGET_DESC_setupFog)) {
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode) instruction).owner.equals(CALL_OWNER)
							&& ((MethodInsnNode) instruction).name.equals(CALL_NAME)
							&& ((MethodInsnNode) instruction).desc.equals(CALL_DESC)) {
						InsnList instructionToInsert = new InsnList();
						instructionToInsert.add(new VarInsnNode(ALOAD, 3));
						instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
								CALL_OWNER,
								CALL_NAME,
								CALL_NEW_DESC,
								false));
						AbstractInsnNode insertAfter = instruction.getPrevious();
						method.instructions.remove(instruction);
						method.instructions.insert(insertAfter, instructionToInsert);
						HE.LOG.info("Successfully injected net.minecraft.client.renderer.EntityRenderer.setupFog");
						break;
					}
				}
			} else if (method.name.equals(TARGET_METHOD_updateFogColor) && method.desc.equals(TARGET_DESC_updateFogColor)) {
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode) instruction).owner.equals(CALL_OWNER)
							&& ((MethodInsnNode) instruction).name.equals(CALL_NAME)
							&& ((MethodInsnNode) instruction).desc.equals(CALL_DESC)) {
						InsnList instructionToInsert = new InsnList();
						instructionToInsert.add(new VarInsnNode(ALOAD, 3));
						instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
								CALL_OWNER,
								CALL_NAME,
								CALL_NEW_DESC,
								false));
						AbstractInsnNode insertAfter = instruction.getPrevious();
						method.instructions.remove(instruction);
						method.instructions.insert(insertAfter, instructionToInsert);
						HE.LOG.info("Successfully injected net.minecraft.client.renderer.EntityRenderer.updateFogColor");
						break;
					}
				}
			} else if (method.name.equals(TARGET_METHOD_getFOVModifier) && method.desc.equals(TARGET_DESC_getFOVModifier)) {
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode) instruction).owner.equals(CALL_OWNER)
							&& ((MethodInsnNode) instruction).name.equals(CALL_NAME)
							&& ((MethodInsnNode) instruction).desc.equals(CALL_DESC)) {
						InsnList instructionToInsert = new InsnList();
						instructionToInsert.add(new VarInsnNode(ALOAD, 3));
						instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
								CALL_OWNER,
								CALL_NAME,
								CALL_NEW_DESC,
								false));
						AbstractInsnNode insertAfter = instruction.getPrevious();
						method.instructions.remove(instruction);
						method.instructions.insert(insertAfter, instructionToInsert);
						HE.LOG.info("Successfully injected net.minecraft.client.renderer.EntityRenderer.getFOVModifier");
						break;
					}
				}
			}
		}

		// Transform back into pure machine code
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		worldClass.accept(classWriter);
		return classWriter.toByteArray();
	}

	private static byte[] transformEntity(byte[] basicClass, boolean isObfuscated) {
		final String TARGET_METHOD = isObfuscated ? "a" : "isInsideOfMaterial";
		final String TARGET_DESC = isObfuscated ? "(Lawt;)Z" : "(Lnet/minecraft/block/material/Material;)Z";
		final String CALL_OWNER = isObfuscated ? "aji" : "net/minecraft/block/Block";
		final String CALL_NAME = isObfuscated ? "o" : "getMaterial";
		final String CALL_DESC = isObfuscated ? "()Lawt;" : "()Lnet/minecraft/block/material/Material;";
		final String CALL_NEW_DESC = isObfuscated ? "(D)Lawt;" : "(D)Lnet/minecraft/block/material/Material;";

		// Transform to human readable byte code
		ClassNode worldClass = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(worldClass, 0);

		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(DLOAD, 2));
		instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
				CALL_OWNER,
				CALL_NAME,
				CALL_NEW_DESC,
				false));

		for(MethodNode method : worldClass.methods) {
			if(method.name.equals(TARGET_METHOD) && method.desc.equals(TARGET_DESC)) {
				for(AbstractInsnNode instruction : method.instructions.toArray()) {
					if(instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode)instruction).owner.equals(CALL_OWNER)
							&& ((MethodInsnNode)instruction).name.equals(CALL_NAME)
							&& ((MethodInsnNode)instruction).desc.equals(CALL_DESC)) {
						AbstractInsnNode insertAfter = instruction.getPrevious();
						method.instructions.remove(instruction);
						method.instructions.insert(insertAfter, instructionToInsert);
						HE.LOG.info("Successfully injected net.minecraft.client.entity.Entity.isInsideOfMaterial");
						break;
					}
				}
			}
		}

		// Transform back into pure machine code
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		worldClass.accept(classWriter);
		return classWriter.toByteArray();
	}

	private static byte[] transformRenderGlobal(byte[] basicClass, boolean isObfuscated) {
		final String CLASS_ICamera = isObfuscated ? "bmv" : "net/minecraft/client/renderer/culling/ICamera";
		final String CLASS_EntityLivingBase = isObfuscated ? "sv" : "net/minecraft/entity/EntityLivingBase";
		final String CLASS_Profiler = isObfuscated ? "qi" : "net/minecraft/profiler/Profiler";
		final String CLASS_HETessalator = "com/sinthoras/hydroenergy/hewater/render/HETessalator";

		final String METHOD_renderEntities = isObfuscated ? "a" : "renderEntities";
		final String METHOD_renderEntities_DESC = "(L" + CLASS_EntityLivingBase + ";L" + CLASS_ICamera + ";F)V";

		final String METHOD_endSection = isObfuscated ? "b" : "endSection";
		final String METHOD_endSection_DESC = "()V";

		final String FIELD_instance = "instance";
		final String FIELD_instance_DESC = "L" + CLASS_HETessalator + ";";

		final String METHOD_render = "render";
		final String METHOD_render_DESC = "(L" + CLASS_ICamera + ";F)V";

		// Transform to human readable byte code
		ClassNode worldClass = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(worldClass, 0);

		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new FieldInsnNode(GETSTATIC, CLASS_HETessalator, FIELD_instance, FIELD_instance_DESC));
		instructionToInsert.add(new VarInsnNode(ALOAD, 2));
		instructionToInsert.add(new VarInsnNode(FLOAD, 3));
		instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
				CLASS_HETessalator,
				METHOD_render,
				METHOD_render_DESC,
				false));

		for(MethodNode method : worldClass.methods) {
			if(method.name.equals(METHOD_renderEntities) && method.desc.equals(METHOD_renderEntities_DESC)) {
				for(AbstractInsnNode instruction : method.instructions.toArray()) {
					if(instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode)instruction).owner.equals(CLASS_Profiler)
							&& ((MethodInsnNode)instruction).name.equals(METHOD_endSection)
							&& ((MethodInsnNode)instruction).desc.equals(METHOD_endSection_DESC)) {
						method.instructions.insert(instruction, instructionToInsert);
						HE.LOG.info("Successfully injected net.minecraft.client.renderer.RenderGlobal.renderEntities");
						break;
					}
				}
			}
		}

		// Transform back into pure machine code
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		worldClass.accept(classWriter);
		return classWriter.toByteArray();
	}

	private static byte[] transformWorldRenderer(byte[] basicClass, boolean isObfuscated) {
		final String CLASS_WorldRenderer = isObfuscated ? "blo" : "net/minecraft/client/renderer/WorldRenderer";
		final String CLASS_HETessalator = "com/sinthoras/hydroenergy/hewater/render/HETessalator";

		final String METHOD_setPosition = isObfuscated ? "a" : "setPosition";
		final String METHOD_setPosition_DESC = "(III)V";

		final String METHOD_setDontDraw = isObfuscated ? "a" : "setDontDraw";
		final String METHOD_setDontDraw_DESC = "()V";

		final String FIELD_instance = "instance";
		final String FIELD_instance_DESC = "L" + CLASS_HETessalator + ";";

		final String METHOD_hook = "onRenderChunkUpdate";
		final String METHOD_hook_DESC = "(IIIIII)V";

		final String FIELD_posX = isObfuscated ? "c" : "posX";
		final String FIELD_posX_DESC = "I";
		final String FIELD_posY = isObfuscated ? "d" : "posY";
		final String FIELD_posY_DESC = "I";
		final String FIELD_posZ = isObfuscated ? "e" : "posZ";
		final String FIELD_posZ_DESC = "I";

		// Transform to human readable byte code
		ClassNode worldClass = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(worldClass, 0);

		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new FieldInsnNode(GETSTATIC, CLASS_HETessalator, FIELD_instance, FIELD_instance_DESC));
		instructionToInsert.add(new VarInsnNode(ALOAD, 0));
		instructionToInsert.add(new FieldInsnNode(GETFIELD, CLASS_WorldRenderer, FIELD_posX, FIELD_posX_DESC));
		instructionToInsert.add(new VarInsnNode(ALOAD, 0));
		instructionToInsert.add(new FieldInsnNode(GETFIELD, CLASS_WorldRenderer, FIELD_posY, FIELD_posY_DESC));
		instructionToInsert.add(new VarInsnNode(ALOAD, 0));
		instructionToInsert.add(new FieldInsnNode(GETFIELD, CLASS_WorldRenderer, FIELD_posZ, FIELD_posZ_DESC));
		instructionToInsert.add(new VarInsnNode(ILOAD, 1));
		instructionToInsert.add(new VarInsnNode(ILOAD, 2));
		instructionToInsert.add(new VarInsnNode(ILOAD, 3));
		instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
				CLASS_HETessalator,
				METHOD_hook,
				METHOD_hook_DESC,
				false));

		for(MethodNode method : worldClass.methods) {
			if(method.name.equals(METHOD_setPosition) && method.desc.equals(METHOD_setPosition_DESC)) {
				for(AbstractInsnNode instruction : method.instructions.toArray()) {
					if(instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode)instruction).owner.equals(CLASS_WorldRenderer)
							&& ((MethodInsnNode)instruction).name.equals(METHOD_setDontDraw)
							&& ((MethodInsnNode)instruction).desc.equals(METHOD_setDontDraw_DESC)) {
						method.instructions.insert(instruction, instructionToInsert);
						HE.LOG.info("Successfully injected net.minecraft.client.renderer.WorldRenderer.setPosition");
						break;
					}
				}
			}
		}

		// Transform back into pure machine code
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		worldClass.accept(classWriter);
		return classWriter.toByteArray();
	}
}
