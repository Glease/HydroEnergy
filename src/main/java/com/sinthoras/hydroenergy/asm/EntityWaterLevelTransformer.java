package com.sinthoras.hydroenergy.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

import com.sinthoras.hydroenergy.HE;

import net.minecraft.launchwrapper.IClassTransformer;
import scala.actors.threadpool.Arrays;

import java.util.List;

public class EntityWaterLevelTransformer implements IClassTransformer {
	
	private static final List targetClasses = Arrays.asList(new String[] {
			"net.minecraft.world.World",
			"net.minecraft.block.Block",
			"net.minecraft.client.renderer.EntityRenderer"});

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
						System.out.println("Successfully injected net.minecraft.world.World");
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
		final String CALL_NEW_E_DESC = isObfuscated ? "(Lsv;)Lawt;" : "(Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/block/material/Material;";

		ClassNode blockClass = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(blockClass, 0);
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		blockClass.accept(classWriter);

		// public Material getMaterial(int y)
		MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, CALL_NAME, CALL_NEW_I_DESC, null, null);
		mv.visitVarInsn(ALOAD, 0); // load this
		mv.visitMethodInsn(INVOKEVIRTUAL, CALL_OWNER, CALL_NAME, CALL_DESC, false);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();

		// public Material getMaterial(EntityLivingBase entitylivingbase)
		mv = classWriter.visitMethod(ACC_PUBLIC, CALL_NAME, CALL_NEW_E_DESC, null, null);
		mv.visitVarInsn(ALOAD, 0); // load this
		mv.visitMethodInsn(INVOKEVIRTUAL, CALL_OWNER, CALL_NAME, CALL_DESC, false);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();

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

		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ALOAD, 3));
		instructionToInsert.add(new MethodInsnNode(INVOKEVIRTUAL,
				CALL_OWNER,
				CALL_NAME,
				CALL_NEW_DESC,
				false));

		for (MethodNode method : worldClass.methods) {
			if (method.name.equals(TARGET_METHOD_setupFog) && method.desc.equals(TARGET_DESC_setupFog)) {
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode) instruction).owner.equals(CALL_OWNER)
							&& ((MethodInsnNode) instruction).name.equals(CALL_NAME)
							&& ((MethodInsnNode) instruction).desc.equals(CALL_DESC)) {
						AbstractInsnNode insertAfter = instruction.getPrevious();
						method.instructions.remove(instruction);
						method.instructions.insert(insertAfter, instructionToInsert);
						System.out.println("Successfully injected net.minecraft.world.World");
						break;
					}
				}
			} else if (method.name.equals(TARGET_METHOD_updateFogColor) && method.desc.equals(TARGET_DESC_updateFogColor)) {
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode) instruction).owner.equals(CALL_OWNER)
							&& ((MethodInsnNode) instruction).name.equals(CALL_NAME)
							&& ((MethodInsnNode) instruction).desc.equals(CALL_DESC)) {
						AbstractInsnNode insertAfter = instruction.getPrevious();
						method.instructions.remove(instruction);
						method.instructions.insert(insertAfter, instructionToInsert);
						System.out.println("Successfully injected net.minecraft.world.World");
						break;
					}
				}
			} else if (method.name.equals(TARGET_METHOD_getFOVModifier) && method.desc.equals(TARGET_DESC_getFOVModifier)) {
				for (AbstractInsnNode instruction : method.instructions.toArray()) {
					if (instruction.getOpcode() == INVOKEVIRTUAL
							&& ((MethodInsnNode) instruction).owner.equals(CALL_OWNER)
							&& ((MethodInsnNode) instruction).name.equals(CALL_NAME)
							&& ((MethodInsnNode) instruction).desc.equals(CALL_DESC)) {
						AbstractInsnNode insertAfter = instruction.getPrevious();
						method.instructions.remove(instruction);
						method.instructions.insert(insertAfter, instructionToInsert);
						System.out.println("Successfully injected net.minecraft.world.World");
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
