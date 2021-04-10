package com.sinthoras.hydroenergy.server;

import java.util.*;

import com.mojang.authlib.GameProfile;
import com.sinthoras.hydroenergy.HE;

import com.sinthoras.hydroenergy.HEMod;
import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.blocks.HEWater;
import com.sinthoras.hydroenergy.config.HEConfig;
import com.sinthoras.hydroenergy.network.packet.HEPacketChunkUpdate;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class HEBlockQueue {
	
	private static final HashMap<Long, HEQueueChunk> chunks = new HashMap<Long, HEQueueChunk>();
	private static FakePlayer fakePlayer;
	private static long timestampLastQueueTick = 0;

	/*private static FakePlayer getFakePlayer(WorldServer world) {
		if(fakePlayer == null) {
			fakePlayer = FakePlayerFactory.get(world, new GameProfile(UUID.fromString("41C82C87-7AfB-4024-BA57-13D2C99C7567"), "HydroEnergyFakePlayer"));  // TODO: Move to config
		}
		if(fakePlayer.worldObj.provider.dimensionId != world.provider.dimensionId) {
			fakePlayer.travelToDimension(world.provider.dimensionId);
		}
		return fakePlayer;
	}*/

	public static void onTick() {
		long currentTime = System.currentTimeMillis();
		if(currentTime - timestampLastQueueTick < HEConfig.delayBetweenSpreadingChunks) {
			return;
		}
		timestampLastQueueTick = currentTime;

		long totalElements = chunks.values().stream()
                .mapToInt(chunk -> chunk.blockStack.size()
                        + chunk.neighborChunkEast.size()
                        + chunk.neighborChunkNorth.size()
                        + chunk.neighborChunkWest.size()
                        + chunk.neighborChunkSouth.size()).sum();
		long totalTickets = chunks.values().stream()
				.filter(chunk -> chunk.forceLoaded)
				.count() * 5;
		HE.info("Stack elements count: " + totalElements);
		HE.info("Chunks count: " + chunks.size());
		HE.info("Tickets count: " + totalTickets);

		Iterator<Map.Entry<Long, HEQueueChunk>> it = chunks.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Long, HEQueueChunk> entry = it.next();
			HEQueueChunk chunk = entry.getValue();
			long key = entry.getKey();
			int chunkX = (int)(key >> 32);
			int chunkZ = (int)key;
			if(chunk.isLoaded()) {
				it.remove();
				if (chunk.resolve()) {
					World world = chunk.chunk.worldObj;
					addToChunk(world, chunkX - 1, chunkZ, chunk.neighborChunkWest);
					addToChunk(world, chunkX, chunkZ - 1, chunk.neighborChunkNorth);
					addToChunk(world, chunkX + 1, chunkZ, chunk.neighborChunkEast);
					addToChunk(world, chunkX, chunkZ + 1, chunk.neighborChunkSouth);
					chunk.cancelLoadRequest();
					return;
				}
				chunk.cancelLoadRequest();
			}
			else {
				chunk.requestChunkLoad();
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
	public boolean forceLoaded = false;

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
					// If the block is over all opague blocks aka can see the sky simply set light to 15.
					// Else to the value of the first non HEWater block directly below
					if(chunk.canBlockSeeTheSky(entry.blockX & 15, entry.blockY, entry.blockZ & 15)) {
						chunkStorage[chunkY].getSkylightArray().set(entry.blockX & 15, entry.blockY & 15, entry.blockZ & 15, 15);
					}
					else {
						int highestOpaqueBlockY = chunk.heightMap[(entry.blockZ & 15) << 4 | (entry.blockX & 15)];
						int highestOpaqueChunkY = HEUtil.coordBlockToChunk(highestOpaqueBlockY);
						if(chunkStorage[highestOpaqueChunkY] == null) {
							chunkStorage[highestOpaqueChunkY] = new ExtendedBlockStorage(highestOpaqueChunkY << 4, !chunk.worldObj.provider.hasNoSky);
						}
						NibbleArray skylightArray = chunkStorage[highestOpaqueChunkY].getSkylightArray();
						if(skylightArray == null) {
							skylightArray = new NibbleArray(HE.blockPerSubChunk, 4);
							chunkStorage[highestOpaqueChunkY].setSkylightArray(skylightArray);
						}
						int lightValue = skylightArray.get(entry.blockZ & 15, highestOpaqueBlockY & 15, entry.blockX & 15);
						skylightArray.set(entry.blockX & 15, entry.blockY & 15, entry.blockZ & 15, lightValue);
					}
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

			HEPacketChunkUpdate message = new HEPacketChunkUpdate(chunk, subChunksHaveChanges);
			for (EntityPlayerMP player : (List<EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
				if(chunk.worldObj.provider.dimensionId == player.worldObj.provider.dimensionId
						&& player.getServerForPlayer().getPlayerManager().isPlayerWatchingChunk(player, chunk.xPosition, chunk.zPosition)) {
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
		Block block = chunk.getBlock(blockX & 15, blockY, blockZ & 15);
		if (chunkX < chunk.xPosition) {
			neighborChunkWest.push(entry);
		} else if (chunkZ < chunk.zPosition) {
			neighborChunkNorth.push(entry);
		} else if (chunkX > chunk.xPosition) {
			neighborChunkEast.push(entry);
		} else if (chunkZ > chunk.zPosition) {
			neighborChunkSouth.push(entry);
		} else if(block == waterBlock || waterBlock.canFlowInto(chunk.worldObj, blockX, blockY, blockZ)) {
			this.blockStack.push(entry);
		}
	}

	public boolean isLoaded() {
		final IChunkProvider chunkProvider = chunk.worldObj.getChunkProvider();
		return chunkProvider.chunkExists(chunk.xPosition, chunk.zPosition)
				&& chunkProvider.chunkExists(chunk.xPosition - 1, chunk.zPosition)
				&& chunkProvider.chunkExists(chunk.xPosition, chunk.zPosition - 1)
				&& chunkProvider.chunkExists(chunk.xPosition + 1, chunk.zPosition)
				&& chunkProvider.chunkExists(chunk.xPosition, chunk.zPosition + 1);
	}

	public void requestChunkLoad() {
		if(forceLoaded == false) {
			HEChunkLoader.forceChunk(chunk, -1, 0);
			HEChunkLoader.forceChunk(chunk, 0, -1);
			HEChunkLoader.forceChunk(chunk, 0, 0);
			HEChunkLoader.forceChunk(chunk, 1, 0);
			HEChunkLoader.forceChunk(chunk, 0, 1);
			forceLoaded = true;
		}
	}

	public void cancelLoadRequest() {
		if(forceLoaded) {
			HEChunkLoader.unforceChunk(chunk, -1, 0);
			HEChunkLoader.unforceChunk(chunk, 0, -1);
			HEChunkLoader.unforceChunk(chunk, 0, 0);
			HEChunkLoader.unforceChunk(chunk, 1, 0);
			HEChunkLoader.unforceChunk(chunk, 0, 1);
			forceLoaded = false;
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