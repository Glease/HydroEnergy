package com.sinthoras.hydroenergy;

import com.sinthoras.hydroenergy.controller.HEDams;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class HEEventHandlerFML  {

	@SubscribeEvent
	public void onEvent(ServerTickEvent event) {
		if(event.phase == ServerTickEvent.Phase.END)
		{
			HEDams.instance.onTick(event);
		}
	}
}
