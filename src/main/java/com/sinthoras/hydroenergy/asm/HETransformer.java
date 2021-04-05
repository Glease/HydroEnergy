package com.sinthoras.hydroenergy.asm;

import com.sinthoras.hydroenergy.asm.biomesoplenty.FogHandlerTransformer;
import com.sinthoras.hydroenergy.asm.galaxyspace.GSPlanetFogHandlerTransformer;
import com.sinthoras.hydroenergy.asm.gregtech.GT_PollutionRendererTransformer;
import com.sinthoras.hydroenergy.asm.minecraft.*;
import com.sinthoras.hydroenergy.asm.witchery.ClientEventsTransformer;
import net.minecraft.launchwrapper.Launch;

import net.minecraft.launchwrapper.IClassTransformer;

import java.util.ArrayList;
import java.util.List;

public class HETransformer implements IClassTransformer {
	
	private static final List<String> targetClasses = new ArrayList<String>() {{
		add(WorldTransformer.fullClassName);
		add(EntityRendererTransformer.fullClassName);
		add(EntityTransformer.fullClassName);
		add(WorldRendererTransformer.fullClassName);
		add(ChunkTransformer.fullClassName);
		add(ChunkProviderClientTransformer.fullClassName);
		add(ActiveRenderInfoTransformer.fullClassName);
		add(FogHandlerTransformer.fullClassName);
		add(GT_PollutionRendererTransformer.fullClassName);
		add(GSPlanetFogHandlerTransformer.fullClassName);
		add(ClientEventsTransformer.fullClassName);
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
				return ChunkTransformer.transform(basicClass, isObfuscated);
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
}
