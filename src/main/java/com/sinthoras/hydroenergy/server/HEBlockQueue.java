package com.sinthoras.hydroenergy.server;

import java.util.LinkedList;

import com.sinthoras.hydroenergy.HE;

import com.sinthoras.hydroenergy.blocks.HEWater;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class HEBlockQueue {

	public enum Mode {
		Add,
		Remove
	}
	
	private static LinkedList<QueueEntry> queue = new LinkedList<QueueEntry>();
	
	public static void onTick() {
		if(!queue.isEmpty()) {
			WorldServer world = MinecraftServer.getServer().worldServers[0];
			int actionsTaken = 0;
			while(actionsTaken < HE.queueActionsPerTick && !queue.isEmpty()) {
				final QueueEntry element = queue.poll();
				final Block block = world.getBlock(element.blockX, element.blockY, element.blockZ);
				if(element.mode == Mode.Add && !(block instanceof HEWater)) {
					world.setBlock(element.blockX, element.blockY, element.blockZ, HE.waterBlocks[element.id]);
					actionsTaken++;
				}
				if(element.mode == Mode.Remove && block instanceof HEWater) {
					world.setBlock(element.blockX, element.blockY, element.blockZ, Blocks.air);
					actionsTaken++;
				}
			}
		}
	}

	public static void enqueueBlock(Mode mode, int blockX, int blockY, int blockZ, int id) {
		queue.add(new QueueEntry(mode, blockX, blockY, blockZ, id));
	}
}

class QueueEntry {
	public HEBlockQueue.Mode mode;
	public int blockX;
	public int blockY;
	public int blockZ;
	public int id;

	public QueueEntry(HEBlockQueue.Mode mode, int blockX, int blockY, int blockZ, int id) {
		this.mode = mode;
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		this.id = id;
	}
}
