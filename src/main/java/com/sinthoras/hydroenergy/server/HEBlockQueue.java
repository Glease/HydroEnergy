package com.sinthoras.hydroenergy.server;

import java.util.*;

import com.sinthoras.hydroenergy.HE;

import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.blocks.HEWater;
import com.sinthoras.hydroenergy.network.HEPacketChunkUpdate;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class HEBlockQueue {
	
	private static HashMap<Long, HEQueueChunk> chunks = new HashMap<Long, HEQueueChunk>();

	private static long timestamp = 0;
	public static void onTick() {
		long currentTime = System.currentTimeMillis();
		if(currentTime - timestamp < HE.spreadingDelayBetweenChunks) {
			return;
		}
		timestamp = currentTime;

		Iterator<Map.Entry<Long, HEQueueChunk>> it = chunks.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Long, HEQueueChunk> entry = it.next();
			HEQueueChunk chunk = entry.getValue();
			it.remove();
			if(chunk.resolve()) {
				long key = entry.getKey();
				int chunkX = (int)(key >> 32);
				int chunkZ = (int)key;
				World world = chunk.chunk.worldObj;
				addToChunk(world, chunkX - 1, chunkZ, chunk.neighborChunkWest);
				addToChunk(world, chunkX, chunkZ - 1, chunk.neighborChunkNorth);
				addToChunk(world, chunkX + 1, chunkZ, chunk.neighborChunkEast);
				addToChunk(world, chunkX, chunkZ + 1, chunk.neighborChunkSouth);
				return;
			}
		}
	}

	private static void addToChunk(World world, int chunkX, int chunkZ, Stack<QueueEntry> stack) {
		if(!stack.isEmpty()) {
			long key = HEUtil.chunkCoordsToKey(chunkX, chunkZ);
			if(chunks.containsKey(key)) {
				chunks.get(key).blockStack.addAll(stack);
			}
			else {
				Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
				chunks.put(key, new HEQueueChunk(chunk, stack));
			}
		}
	}

	public static void enqueueBlock(World world, int blockX, int blockY, int blockZ, int waterId) {
		int chunkX = HEUtil.coordBlockToChunk(blockX);
		int chunkZ = HEUtil.coordBlockToChunk(blockZ);
		long key = HEUtil.chunkCoordsToKey(chunkX, chunkZ);
		if(chunks.containsKey(key)) {
			chunks.get(key).add(blockX, blockY, blockZ, HE.waterBlocks[waterId]);
		}
		else {
			chunks.put(key, new HEQueueChunk(world.getChunkFromChunkCoords(chunkX, chunkZ), HE.waterBlocks[waterId], blockX, blockY, blockZ));
		}
	}
}

class HEQueueChunk {

	public Stack<QueueEntry> blockStack;
	public Stack<QueueEntry> neighborChunkWest = new Stack<QueueEntry>();
	public Stack<QueueEntry> neighborChunkNorth = new Stack<QueueEntry>();
	public Stack<QueueEntry> neighborChunkEast = new Stack<QueueEntry>();
	public Stack<QueueEntry> neighborChunkSouth = new Stack<QueueEntry>();
	public Chunk chunk;

	HEQueueChunk(Chunk chunk, Stack<QueueEntry> blockStack) {
		this.chunk = chunk;
		this.blockStack = blockStack;
	}

	HEQueueChunk(Chunk chunk, HEWater waterBlock, int blockX, int blockY, int blockZ) {
		this.chunk = chunk;
		blockStack = new Stack<QueueEntry>();
		blockStack.add(new QueueEntry(blockX, blockY, blockZ, waterBlock));
	}

	public boolean resolve() {
		ExtendedBlockStorage[] chunkStorage = chunk.getBlockStorageArray();
		short subChunksHaveChanges = 0;
		while(!blockStack.isEmpty()) {
			QueueEntry entry = blockStack.pop();
			int waterId = entry.waterBlock.getWaterId();
			Block block = chunk.getBlock(entry.blockX & 15, entry.blockY, entry.blockZ & 15);
			boolean removeBlock = !HEServer.instance.canSpread(waterId)
					|| HEServer.instance.isBlockOutOfBounds(waterId, entry.blockX, entry.blockY, entry.blockZ);
			if(removeBlock) {
				if(block == entry.waterBlock) {
					int chunkY = entry.blockY >> 4;
					chunkStorage[chunkY].func_150818_a(entry.blockX & 15, entry.blockY & 15, entry.blockZ & 15, Blocks.air);
					HEServer.instance.onBlockRemoved(waterId, entry.blockY);
					subChunksHaveChanges |= HEUtil.chunkYToFlag(chunkY);

					add(entry.blockX - 1, entry.blockY, entry.blockZ, entry.waterBlock);
					add(entry.blockX, entry.blockY - 1, entry.blockZ, entry.waterBlock);
					add(entry.blockX, entry.blockY, entry.blockZ - 1, entry.waterBlock);
					add(entry.blockX + 1, entry.blockY, entry.blockZ, entry.waterBlock);
					add(entry.blockX, entry.blockY + 1, entry.blockZ, entry.waterBlock);
					add(entry.blockX, entry.blockY, entry.blockZ + 1, entry.waterBlock);
				}
			}
			else {
				if(entry.waterBlock.canFlowInto(chunk.worldObj, entry.blockX, entry.blockY, entry.blockZ)) {
					int chunkY = entry.blockY >> 4;
					if(chunkStorage[chunkY] == null) {
						chunkStorage[chunkY] = new ExtendedBlockStorage(chunkY << 4, !chunk.worldObj.provider.hasNoSky);
					}
					chunkStorage[chunkY].func_150818_a(entry.blockX & 15, entry.blockY & 15, entry.blockZ & 15, entry.waterBlock);
					HEServer.instance.onBlockPlaced(waterId, entry.blockY);
					subChunksHaveChanges |= HEUtil.chunkYToFlag(chunkY);

					add(entry.blockX - 1, entry.blockY, entry.blockZ, entry.waterBlock);
					add(entry.blockX, entry.blockY - 1, entry.blockZ, entry.waterBlock);
					add(entry.blockX, entry.blockY, entry.blockZ - 1, entry.waterBlock);
					add(entry.blockX + 1, entry.blockY, entry.blockZ, entry.waterBlock);
					add(entry.blockX, entry.blockY + 1, entry.blockZ, entry.waterBlock);
					add(entry.blockX, entry.blockY, entry.blockZ + 1, entry.waterBlock);
				}
			}
		}
		boolean changedChunk = subChunksHaveChanges > 0;
		if(changedChunk) {

			chunk.setChunkModified();

			// TODO: Send to clients in range
			// TODO: Singleplayer is renderUpdate instead
			HEPacketChunkUpdate message = new HEPacketChunkUpdate(chunk, subChunksHaveChanges);
			for(EntityPlayerMP player : (List<EntityPlayerMP>)MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
				if(player.getServerForPlayer().getPlayerManager().isPlayerWatchingChunk(player, chunk.xPosition, chunk.zPosition)) {
					HE.network.sendTo(message, player);
				}
			}
		}
		return changedChunk;
	}

	public void add(int blockX, int blockY, int blockZ, HEWater waterBlock) {
		final QueueEntry entry = new QueueEntry(blockX, blockY, blockZ, waterBlock);
		int chunkX = HEUtil.coordBlockToChunk(blockX);
		int chunkZ = HEUtil.coordBlockToChunk(blockZ);
		if (chunkX < chunk.xPosition) {
			neighborChunkWest.push(entry);
		} else if (chunkZ < chunk.zPosition) {
			neighborChunkNorth.push(entry);
		} else if (chunkX > chunk.xPosition) {
			neighborChunkEast.push(entry);
		} else if (chunkZ > chunk.zPosition) {
			neighborChunkSouth.push(entry);
		} else if(chunk.getBlock(blockX & 15, blockY, blockZ & 15) == Blocks.air) {
			this.blockStack.push(entry);
		}
	}
}

class QueueEntry {
	public int blockX;
	public int blockY;
	public int blockZ;
	public HEWater waterBlock;

	public QueueEntry(int blockX, int blockY, int blockZ, HEWater waterBlock) {
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		this.waterBlock = waterBlock;
	}
}