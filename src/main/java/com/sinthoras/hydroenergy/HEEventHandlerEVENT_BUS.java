package com.sinthoras.hydroenergy;

import com.sinthoras.hydroenergy.controller.HEDams;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;

public class HEEventHandlerEVENT_BUS {

	@SubscribeEvent
	public void onEvent(WorldEvent.Load event)
	{
		HEDams.get(event.world);
	}
}
