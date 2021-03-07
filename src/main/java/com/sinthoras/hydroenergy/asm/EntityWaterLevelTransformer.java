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
		final String CLASS_AxisAlignedBB = isObfuscated ? "azt" : "net/minecraft/util/AxisAlignedBB";
		final String CLASS_Material = isObfuscated ? "awt" : "net/minecraft/block/material/Material";
		final String CLASS_Entity = isObfuscated ? "sa" : "net/minecraft/entity/Entity";
		final String CLASS_Block = isObfuscated ? "aji" : "net/minecraft/block/Block";

		final String METHOD_handleMaterialAcceleration = isObfuscated ? "a" : "handleMaterialAcceleration";
		final String METHOD_handleMaterialAcceleration_DESC = "(L" + CLASS_AxisAlignedBB + ";L" + CLASS_Material + ";L" + CLASS_Entity + ";)Z";
		final String METHOD_getMaterial = isObfuscated ? "o" : "getMaterial";
		final String METHOD_getMaterial_DESC = "()L" + CLASS_Material + ";";
		final String METHOD_getMaterial_DESC_NEW = "(I)L" + CLASS_Material + ";";

		// Transform to human readable byte code
		ClassNode worldClass = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(worldClass, 0);

		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ILOAD, 13));
		instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
				CLASS_Block,
				METHOD_getMaterial,
				METHOD_getMaterial_DESC_NEW,
				false));

		for(MethodNode method : worldClass.methods) {
			if(method.name.equals(METHOD_handleMaterialAcceleration) && method.desc.equals(METHOD_handleMaterialAcceleration_DESC)) {
				for(AbstractInsnNode instruction : method.instructions.toArray()) {
					if(instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode)instruction).owner.equals(CLASS_Block)
							&& ((MethodInsnNode)instruction).name.equals(METHOD_getMaterial)
							&& ((MethodInsnNode)instruction).desc.equals(METHOD_getMaterial_DESC)) {
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
		final String CLASS_Block = isObfuscated ? "aji" : "net/minecraft/block/Block";
		final String CLASS_Material = isObfuscated ? "awt" : "net/minecraft/block/material/Material";
		final String CLASS_EntityLivingBase = isObfuscated ? "sa" : "net/minecraft/entity/EntityLivingBase";

		final String METHOD_getMaterial = isObfuscated ? "o" : "getMaterial";
		final String METHOD_getMaterial_DESC = "()L" + CLASS_Material + ";";
		final String METHOD_getMaterial_DESC_NEW_I = "(I)L" + CLASS_Material + ";";
		final String METHOD_getMaterial_DESC_NEW_ELB = "(L" + CLASS_EntityLivingBase + ";)L" + CLASS_Material + ";";
		final String METHOD_getMaterial_DESC_NEW_D = "(D)L" + CLASS_Material + ";";

		ClassNode blockClass = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(blockClass, 0);
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		blockClass.accept(classWriter);

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

		HE.LOG.info("Successfully injected net.minecraft.client.block.Block");

		return classWriter.toByteArray();
	}

	private static byte[] transformEntityRenderer(byte[] basicClass, boolean isObfuscated) {
		final String CLASS_Block = isObfuscated ? "aji" : "net/minecraft/block/Block";
		final String CLASS_Material = isObfuscated ? "awt" : "net/minecraft/block/material/Material";
		final String CLASS_EntityLivingBase = isObfuscated ? "sa" : "net/minecraft/entity/EntityLivingBase";

		final String METHOD_setupFog = isObfuscated ? "a" : "setupFog";
		final String METHOD_setupFog_DESC = "(IF)V";
		final String METHOD_updateFogColor = isObfuscated ? "j" : "updateFogColor";
		final String METHOD_updateFogColor_DESC = "(F)V";
		final String METHOD_getFOVModifier = isObfuscated ? "a" : "getFOVModifier";
		final String METHOD_getFOVModifier_DESC = "(FZ)F";

		final String METHOD_getMaterial = isObfuscated ? "o" : "getMaterial";
		final String METHOD_getMaterial_DESC = "()L" + CLASS_Material + ";";
		final String METHOD_getMaterial_DESC_NEW = "(L" + CLASS_EntityLivingBase + ";)L" + CLASS_Material + ";";

		// Transform to human readable byte code
		ClassNode worldClass = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(worldClass, 0);

		for (MethodNode method : worldClass.methods) {
			if (method.name.equals(METHOD_setupFog) && method.desc.equals(METHOD_setupFog_DESC)) {
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode) instruction).owner.equals(CLASS_Block)
							&& ((MethodInsnNode) instruction).name.equals(METHOD_getMaterial)
							&& ((MethodInsnNode) instruction).desc.equals(METHOD_getMaterial_DESC)) {
						InsnList instructionToInsert = new InsnList();
						instructionToInsert.add(new VarInsnNode(ALOAD, 3));
						instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
								CLASS_Block,
								METHOD_getMaterial,
								METHOD_getMaterial_DESC_NEW,
								false));
						AbstractInsnNode insertAfter = instruction.getPrevious();
						method.instructions.remove(instruction);
						method.instructions.insert(insertAfter, instructionToInsert);
						HE.LOG.info("Successfully injected net.minecraft.client.renderer.EntityRenderer.setupFog");
						break;
					}
				}
			} else if (method.name.equals(METHOD_updateFogColor) && method.desc.equals(METHOD_updateFogColor_DESC)) {
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode) instruction).owner.equals(CLASS_Block)
							&& ((MethodInsnNode) instruction).name.equals(METHOD_getMaterial)
							&& ((MethodInsnNode) instruction).desc.equals(METHOD_getMaterial_DESC)) {
						InsnList instructionToInsert = new InsnList();
						instructionToInsert.add(new VarInsnNode(ALOAD, 3));
						instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
								CLASS_Block,
								METHOD_getMaterial,
								METHOD_getMaterial_DESC_NEW,
								false));
						AbstractInsnNode insertAfter = instruction.getPrevious();
						method.instructions.remove(instruction);
						method.instructions.insert(insertAfter, instructionToInsert);
						HE.LOG.info("Successfully injected net.minecraft.client.renderer.EntityRenderer.updateFogColor");
						break;
					}
				}
			} else if (method.name.equals(METHOD_getFOVModifier) && method.desc.equals(METHOD_getFOVModifier_DESC)) {
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode) instruction).owner.equals(CLASS_Block)
							&& ((MethodInsnNode) instruction).name.equals(METHOD_getMaterial)
							&& ((MethodInsnNode) instruction).desc.equals(METHOD_getMaterial_DESC)) {
						InsnList instructionToInsert = new InsnList();
						instructionToInsert.add(new VarInsnNode(ALOAD, 3));
						instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
								CLASS_Block,
								METHOD_getMaterial,
								METHOD_getMaterial_DESC_NEW,
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
		final String CLASS_Block = isObfuscated ? "aji" : "net/minecraft/block/Block";
		final String CLASS_Material = isObfuscated ? "awt" : "net/minecraft/block/material/Material";

		final String METHOD_isInsideOfMaterial = isObfuscated ? "a" : "isInsideOfMaterial";
		final String METHOD_isInsideOfMaterial_DESC = "(L" + CLASS_Material + ";)Z";
		final String METHOD_getMaterial = isObfuscated ? "o" : "getMaterial";
		final String METHOD_getMaterial_DESC = "()L" + CLASS_Material + ";";
		final String METHOD_getMaterial_DESC_NEW = "(D)L" + CLASS_Material + ";";

		// Transform to human readable byte code
		ClassNode worldClass = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(worldClass, 0);

		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(DLOAD, 2));
		instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
				CLASS_Block,
				METHOD_getMaterial,
				METHOD_getMaterial_DESC_NEW,
				false));

		for(MethodNode method : worldClass.methods) {
			if(method.name.equals(METHOD_isInsideOfMaterial) && method.desc.equals(METHOD_isInsideOfMaterial_DESC)) {
				for(AbstractInsnNode instruction : method.instructions.toArray()) {
					if(instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode)instruction).owner.equals(CLASS_Block)
							&& ((MethodInsnNode)instruction).name.equals(METHOD_getMaterial)
							&& ((MethodInsnNode)instruction).desc.equals(METHOD_getMaterial_DESC)) {
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
