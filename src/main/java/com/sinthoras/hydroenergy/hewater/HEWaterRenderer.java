package com.sinthoras.hydroenergy.hewater;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class HEWaterRenderer {
	
	@SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
    	//System.out.println("render last call");
    }
}
