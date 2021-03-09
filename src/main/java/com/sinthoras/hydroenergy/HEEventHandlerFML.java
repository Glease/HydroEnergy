package com.sinthoras.hydroenergy;

import com.sinthoras.hydroenergy.controller.HEDamsServer;
import com.sinthoras.hydroenergy.hewater.HEBlockQueue;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class HEEventHandlerFML {
	
	private int counter = 0;
	private float waterLevel = 63.0f;
	private int sign = 1;

	@SubscribeEvent
	public void onEvent(ServerTickEvent event) {
		if(event.phase == ServerTickEvent.Phase.END) {
			HEBlockQueue.onTick(event);
			if(counter > 20) {
				counter = 0;
				if(HE.DEBUGslowFill) {
					waterLevel += sign * 0.02f;
					HEDamsServer.instance.updateWaterLevel(0, waterLevel);
				}
			}
			if(waterLevel >= 73.0f) {
				sign = -1;
			}
			if(waterLevel <= 63.0f) {
				sign = 1;
			}
			counter++;
		}
	}
	
	@SubscribeEvent
	public void onEvent(PlayerLoggedInEvent event) {
		HEDamsServer.instance.synchronizeClient(event);
	}
}
