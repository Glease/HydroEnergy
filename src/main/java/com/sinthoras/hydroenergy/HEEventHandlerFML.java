package com.sinthoras.hydroenergy;

import com.sinthoras.hydroenergy.controller.HEDams;
import com.sinthoras.hydroenergy.hewater.HERenderManager;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class HEEventHandlerFML  {

	@SubscribeEvent
	public void onEvent(ServerTickEvent event) {
		if(event.phase == ServerTickEvent.Phase.END)
		{
			HEDams.instance.onTick(event);
		}
	}
	
	@SubscribeEvent
	public void onEvent(ChunkEvent.Unload event)
	{
		HERenderManager.instance.onChunkUnload(event);
	}
}
