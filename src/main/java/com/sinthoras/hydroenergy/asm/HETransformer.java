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
