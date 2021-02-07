package com.sinthoras.hydroenergy.hewater.render;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.proxy.HECommonProxy;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;

public class HERenderManagerDam {
	
	private HashMap<Long, Short> chunks = new HashMap<Long, Short>();
	private TreeSet<Long> rerenderQueue = new TreeSet<Long>(new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {
			long key_A = (Long)o1;
			int chunkX_A = (int)(key_A >> 32);
			int chunkZ_A = (int)key_A;
			float dist_A = Minecraft.getMinecraft().thePlayer.getPlayerCoordinates().getDistanceSquaredToChunkCoordinates(new ChunkCoordinates(chunkX_A, 0, chunkZ_A));
			
			long key_B = (Long)o2;
			int chunkX_B = (int)(key_B >> 32);
			int chunkZ_B = (int)key_B;
			float dist_B = Minecraft.getMinecraft().thePlayer.getPlayerCoordinates().getDistanceSquaredToChunkCoordinates(new ChunkCoordinates(chunkX_B, 0, chunkZ_B));
			
			return -Float.compare(dist_A, dist_B);
		}
	});
	private HELightManagerDam lightDam = new HELightManagerDam();
	
	// x, y, z in block coords
	public void addBlock(int x, int y, int z, boolean lightNeedsPatching)
	{
		int chunkX = HEUtil.bucketInt16(x);
		int chunkY = HEUtil.bucketInt16(y);
		int chunkZ = HEUtil.bucketInt16(z);
		long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
		short verticalField = chunks.containsKey(key) ? chunks.get(key) : 0;
		verticalField |= 1 << chunkY;
		chunks.put(key, verticalField);
		
		if(lightNeedsPatching)
			lightDam.addBlock(x, y, z);
	}
	
	// x, y, z in Chunk coords (/16)
	public void onChunkUnload(int chunkX, int chunkZ)
	{
		long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
		if(chunks.containsKey(key))
			chunks.remove(key);
		lightDam.onChunkUnload(chunkX, chunkZ);
	}
	
	public void onChunkLoad(int chunkX, int chunkZ) {
		lightDam.onChunkLoad(chunkX, chunkZ);
	}
	
	public void onRenderTick() {
		WorldClient world = Minecraft.getMinecraft().theWorld;
		for(int i=0;i<HE.maxRerenderChunksPerRenderTick;i++ ) {
			if(rerenderQueue.isEmpty())
				return;
			long key = rerenderQueue.pollFirst();
			lightDam.triggerLightPatch(key, world);
			int chunkX = (int)(key >> 32);
			int chunkZ = (int)key;
			int x = HEUtil.debucketInt16(chunkX);
			int z = HEUtil.debucketInt16(chunkZ);
			short verticalField = chunks.get(key);
			for(int y=0;y<16;y++)
				if(((verticalField >> y) & 1) == 1)
					Minecraft.getMinecraft().renderGlobal.markBlocksForUpdate(x, y << 4, z, x, y << 4, z);
		}
	}
	
	public void triggerRenderUpdate(float waterLevel)
	{
		rerenderQueue.addAll(chunks.keySet());
		lightDam.waterLevel = waterLevel;
	}
}























