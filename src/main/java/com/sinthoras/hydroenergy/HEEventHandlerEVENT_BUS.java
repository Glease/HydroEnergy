package com.sinthoras.hydroenergy;

import com.sinthoras.hydroenergy.controller.HEDams;
import com.sinthoras.hydroenergy.hewater.render.HERenderManager;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

public class HEEventHandlerEVENT_BUS {
	
	@SubscribeEvent
	public void onEvent(ChunkEvent.Unload event)
	{
		HERenderManager.instance.onChunkUnload(event);}
}
