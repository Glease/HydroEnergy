package com.sinthoras.hydroenergy.server;

import java.util.LinkedList;

import com.sinthoras.hydroenergy.HE;

import com.sinthoras.hydroenergy.blocks.HEWater;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class HEBlockQueue {
	
	private static LinkedList<QueueEntry> queue = new LinkedList<QueueEntry>();
	
	public static void onTick(TickEvent.ServerTickEvent event) {
		if(!queue.isEmpty()) {
			WorldServer world = MinecraftServer.getServer().worldServers[0];
			int countPlaced = 0;
			while(countPlaced < 10 && !queue.isEmpty()) {  // TODO: move 10 to config
				final QueueEntry element = queue.poll();
				final Block block = world.getBlock(element.blockX, element.blockY, element.blockZ);
				if(!(block instanceof HEWater)) {
					world.setBlock(element.blockX, element.blockY, element.blockZ, HE.waterBlocks[element.id]);
					countPlaced++;
				}
			}
		}
	}

	public static void addBlock(int blockX, int blockY, int blockZ, int id) {
		queue.add(new QueueEntry(blockX, blockY, blockZ, id));
	}
}

class QueueEntry {
	public int blockX;
	public int blockY;
	public int blockZ;
	public int id;

	public QueueEntry(int blockX, int blockY, int blockZ, int id) {
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		this.id = id;
	}
}
