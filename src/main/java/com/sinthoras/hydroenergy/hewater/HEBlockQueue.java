package com.sinthoras.hydroenergy.hewater;

import java.util.LinkedList;

import com.sinthoras.hydroenergy.HE;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class HEBlockQueue {
	
	private static LinkedList<QueueEntry> queue = new LinkedList<QueueEntry>();
	
	public static void onTick(TickEvent.ServerTickEvent event)
	{
		if(!queue.isEmpty())
		{
			WorldServer world = MinecraftServer.getServer().worldServers[0];
			int countPlaced = 0;
			while(countPlaced < 10 && !queue.isEmpty())  // TODO: move 10 to config
			{
				final QueueEntry element = queue.poll();
				final Block block = world.getBlock(element.x, element.y, element.z);
				if(!(block instanceof HEWater))
				{
					world.setBlock(element.x, element.y, element.z, HE.waterBlocks[element.id]);
					countPlaced++;
				}
			}
		}
	}

	public static void addBlock(int x, int y, int z, int id)
	{
		queue.add(new QueueEntry(x, y, z, id));
	}
}

class QueueEntry
{
	public int x;
	public int y;
	public int z;
	public int id;

	public QueueEntry(int x, int y, int z, int id)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}
}
