package com.sinthoras.hydroenergy.server;

import java.util.LinkedList;

import com.sinthoras.hydroenergy.HE;

import com.sinthoras.hydroenergy.blocks.HEWater;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class HEBlockQueue {
	
	private static LinkedList<QueueEntry> queue = new LinkedList<QueueEntry>();
	
	public static void onTick() {
		if(!queue.isEmpty()) {
			WorldServer world = MinecraftServer.getServer().worldServers[0];
			int actionsTaken = 0;
			while(actionsTaken < HE.queueActionsPerTick && !queue.isEmpty()) {
				QueueEntry element = queue.poll();
				Block block = world.getBlock(element.blockX, element.blockY, element.blockZ);
				HEWater waterBlock = HE.waterBlocks[element.waterId];
				boolean removeBlock = !HEServer.instance.canSpread(element.waterId)
						|| HEServer.instance.isBlockOutOfBounds(element.waterId, element.blockX, element.blockY, element.blockZ);
				if(removeBlock) {
					if(block == waterBlock) {
						world.setBlock(element.blockX, element.blockY, element.blockZ, Blocks.air);
						HEServer.instance.onBlockRemoved(waterBlock.getWaterId(), element.blockY);
						actionsTaken++;
					}
				}
				else {
					if(waterBlock.canFlowInto(world, element.blockX, element.blockY, element.blockZ)) {
						world.setBlock(element.blockX, element.blockY, element.blockZ, waterBlock);
						HEServer.instance.onBlockPlaced(waterBlock.getWaterId(), element.blockY);
						actionsTaken++;
					}
				}
			}
		}
	}

	public static void enqueueBlock(int blockX, int blockY, int blockZ, int waterId) {
		queue.add(new QueueEntry(blockX, blockY, blockZ, waterId));
	}
}

class QueueEntry {
	public int blockX;
	public int blockY;
	public int blockZ;
	public int waterId;

	public QueueEntry(int blockX, int blockY, int blockZ, int waterId) {
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		this.waterId = waterId;
	}
}
