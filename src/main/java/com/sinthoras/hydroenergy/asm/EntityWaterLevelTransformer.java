package com.sinthoras.hydroenergy.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

import com.sinthoras.hydroenergy.HE;

import net.minecraft.launchwrapper.IClassTransformer;
import scala.actors.threadpool.Arrays;

import java.util.List;

public class EntityWaterLevelTransformer implements IClassTransformer {
	
	private static final List targetClasses = Arrays.asList(new String[] {
			"net.minecraft.world.World",
			"net.minecraft.block.Block"});

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		boolean isObfuscated = !name.equals(transformedName);
		int index = targetClasses.indexOf(transformedName);
		return index != -1 ? transform(index, basicClass, isObfuscated) : basicClass;
	}
	
	private static byte[] transform(int index, byte[] basicClass, boolean isObfuscated) {
		System.out.println("Transforming " + targetClasses.get(index));
		try {
			// Transform to human readable byte code
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, 0);
			
			switch(index) {
				case 0:
					if(transformWorld(classNode, isObfuscated)) {
						System.out.println("Successfully injected net.minecraft.world.World");
					} else {
						System.out.println("Failed to inject net.minecraft.world.World");
					}
					break;
				case 1:
					transformBlock(classNode, isObfuscated);
					break;
			}
			
			// Transform back into pure machine code
			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(classWriter);
			return classWriter.toByteArray();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return basicClass;
	}
	
	private static boolean transformWorld(ClassNode worldClass, boolean isObfuscated) {
		final String TARGET_METHOD = isObfuscated ? "a" : "handleMaterialAcceleration";
		final String TARGET_DESC = isObfuscated ? "(Lazt;Lawt;Lsa;)Z" : "(Lnet/minecraft/util/AxisAlignedBB;Lnet/minecraft/block/material/Material;Lnet/minecraft/entity/Entity;)Z";
		final String CALL_OWNER = isObfuscated ? "aji" : "net/minecraft/block/Block";
		final String CALL_NAME = isObfuscated ? "o" : "getMaterial";
		final String CALL_DESC = isObfuscated ? "()Lawt;" : "()Lnet/minecraft/block/material/Material;";
		final String CALL_NEW_DESC = isObfuscated ? "(III)Lawt;" : "(III)Lnet/minecraft/block/material/Material;";


		InsnList instructionToInsert = new InsnList();
		instructionToInsert.add(new VarInsnNode(ILOAD, 12));
		instructionToInsert.add(new VarInsnNode(ILOAD, 13));
		instructionToInsert.add(new VarInsnNode(ILOAD, 14));
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
						return true;
					}
				}
			}
		}
		return false;
	}

	private static void transformBlock(ClassNode blockClass, boolean isObfuscated) {
		final String GET_MATERIAL = isObfuscated ? "o" : "getMaterial";
		final String GET_MATERIAL_DESC = isObfuscated ? "()Lawt;" : "()Lnet/minecraft/block/material/Material;";
		
		for(MethodNode method : blockClass.methods) {
			if(method.name.equals(GET_MATERIAL) && method.desc.equals(GET_MATERIAL_DESC)) {
				
			}
		}
	}
}
