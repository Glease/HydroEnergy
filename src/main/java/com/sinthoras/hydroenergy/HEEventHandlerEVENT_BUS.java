package com.sinthoras.hydroenergy;

import com.sinthoras.hydroenergy.hewater.render.HERenderManager;

import com.sinthoras.hydroenergy.hewater.render.HETessalator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class HEEventHandlerEVENT_BUS {
	
	@SubscribeEvent
	public void onEvent(ChunkEvent.Unload event)
	{
		HERenderManager.instance.onChunkUnload(event);
		final Chunk chunk = event.getChunk();
		HETessalator.instance.onChunkUnload(chunk.xPosition, chunk.zPosition);
	}

	@SubscribeEvent
	public void onEvent(ChunkEvent.Load event) {
		final Chunk chunk = event.getChunk();
		HETessalator.instance.onChunkLoad(chunk.xPosition, chunk.zPosition);
	}

	@SubscribeEvent
	public void onEvent(RenderWorldEvent.Pre event) {
		HETessalator.instance.onPreRender(event.renderer.posX, event.renderer.posY, event.renderer.posZ);
	}

	@SubscribeEvent
	public void onEvent(RenderWorldEvent.Post event) {
		HETessalator.instance.onPostRender(event.renderer.posX, event.renderer.posY, event.renderer.posZ);
	}
}
