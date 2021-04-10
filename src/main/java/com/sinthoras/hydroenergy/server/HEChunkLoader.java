package com.sinthoras.hydroenergy.server;

import com.sinthoras.hydroenergy.HETags;
import com.sinthoras.hydroenergy.HEUtil;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HEChunkLoader {

    public static class HEChunkLoaderServerStartCallback implements ForgeChunkManager.OrderedLoadingCallback {
        @Override
        public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {

        }

        @Override
        public List<ForgeChunkManager.Ticket> ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world, int maxTicketCount)
        {
            return new ArrayList<>();
        }
    }

    private static class TicketCounter {
        public static ForgeChunkManager.Ticket ticket;
        public static int counter;

        public TicketCounter(World world) {
            ticket = ForgeChunkManager.requestTicket(HETags.MODID, world, ForgeChunkManager.Type.NORMAL);
            counter = 0;
        }
    }

    private static final HashMap<Integer, HashMap<Long, TicketCounter>> ticketsPerDimension = new HashMap<>();

    public static void forceChunk(Chunk chunk, int offsetBlockX, int offsetBlockZ) {
        final int dimensionId = chunk.worldObj.provider.dimensionId;
        if(!ticketsPerDimension.containsKey(dimensionId)) {
            ticketsPerDimension.put(dimensionId, new HashMap<>());
        }
        final HashMap<Long, TicketCounter> tickets = ticketsPerDimension.get(dimensionId);
        long key = HEUtil.chunkCoordsToKey(chunk.xPosition + offsetBlockX, chunk.zPosition + offsetBlockZ);
        if(!tickets.containsKey(key)) {
            final TicketCounter ticket = new TicketCounter(chunk.worldObj);
            ForgeChunkManager.forceChunk(ticket.ticket, new ChunkCoordIntPair(chunk.xPosition + offsetBlockX, chunk.zPosition + offsetBlockZ));
            tickets.put(key, ticket);
        }
        TicketCounter ticket = tickets.get(key);
        final int counter = ticket.counter;
        if(ticket.ticket == null) {
            ticket = new TicketCounter(chunk.worldObj);
            ticket.counter = counter;
            tickets.put(key, ticket);
        }
        ticket.counter++;
    }

    public static void unforceChunk(Chunk chunk, int offsetBlockX, int offsetBlockZ) {
        final int dimensionId = chunk.worldObj.provider.dimensionId;
        final HashMap<Long, TicketCounter> tickets = ticketsPerDimension.get(dimensionId);
        long key = HEUtil.chunkCoordsToKey(chunk.xPosition + offsetBlockX, chunk.zPosition + offsetBlockZ);
        final TicketCounter ticket = tickets.get(key);
        ticket.counter--;
        if(ticket.counter <= 0) {
            if(ticket.ticket != null) {
                ForgeChunkManager.unforceChunk(ticket.ticket, new ChunkCoordIntPair(chunk.xPosition + offsetBlockX, chunk.zPosition + offsetBlockZ));
                ForgeChunkManager.releaseTicket(ticket.ticket);
            }
            tickets.remove(key);
        }
    }
}
