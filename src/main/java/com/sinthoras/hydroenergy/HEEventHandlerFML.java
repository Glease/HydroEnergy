package com.sinthoras.hydroenergy;

import com.sinthoras.hydroenergy.controller.HEController;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class HEEventHandlerFML  {
	
	private int counter = 0;

	@SubscribeEvent//(priority=EventPriority.HIGHEST, receiveCanceled=true)
	public void onEvent(ServerTickEvent event) {
		if(event.phase == ServerTickEvent.Phase.END)
		{
			counter++;
			if(counter >= 200) // Fire every 10 seconds
			{
				counter = 0;
				HEController.on10sTick(event);
				HE.LOG.info("10 SEC");
			}
		}
	}
}
