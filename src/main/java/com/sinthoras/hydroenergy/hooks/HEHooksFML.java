package com.sinthoras.hydroenergy.hooks;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.server.HEServer;
import com.sinthoras.hydroenergy.server.HEBlockQueue;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class HEHooksFML {
	
	private int counter = 0;
	private float waterLevel = 63.0f;
	private int sign = 1;

	@SubscribeEvent
	public void onEvent(ServerTickEvent event) {
		if(event.phase == ServerTickEvent.Phase.END) {
			HEBlockQueue.onTick();
			if(counter > 20) {
				counter = 0;
				if(HE.DEBUGslowFill) {
					waterLevel += sign * 0.02f;
					HEServer.instance.updateWaterLevel(0, waterLevel);
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
		HEServer.instance.synchronizeClient(event);
	}
}
