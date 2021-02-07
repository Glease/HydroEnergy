package com.sinthoras.hydroenergy;

import com.sinthoras.hydroenergy.controller.HEDams;
import com.sinthoras.hydroenergy.hewater.HEBlockQueue;
import com.sinthoras.hydroenergy.hewater.render.HERenderManager;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class HEEventHandlerFML  {
	
	private int counter = 0;
	private float waterLevel = 63.0f;
	private int sign = 1;

	@SubscribeEvent
	public void onEvent(ServerTickEvent event) {
		if(event.phase == ServerTickEvent.Phase.END) {
			HEBlockQueue.instance.onTick(event);
			if(counter > 20) {
				counter = 0;
				if(HE.DEBUGslowFill ) {
					waterLevel += sign * 0.02f;
					HEDams.instance.updateWaterLevel(0, waterLevel);
				}
			}
			if(waterLevel >= 73.0f)
				sign = -1;
			if(waterLevel <= 63.0f)
				sign = 1;
			counter++;
			
			
		}
	}
	
	@SubscribeEvent
	public void onEvent(RenderTickEvent event) {
		if(event.phase == RenderTickEvent.Phase.END && HERenderManager.instance != null)
			HERenderManager.instance.onRenderTick();
			
	}
	
	@SubscribeEvent
	public void onEvent(PlayerLoggedInEvent event) {
		HEDams.instance.synchronizeClient(event);
	}
}
