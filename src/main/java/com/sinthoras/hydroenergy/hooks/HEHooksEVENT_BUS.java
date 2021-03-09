package com.sinthoras.hydroenergy.hooks;

import com.sinthoras.hydroenergy.client.light.HELightManager;
import com.sinthoras.hydroenergy.client.renderer.HETessalator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.RenderWorldEvent;

public class HEHooksEVENT_BUS {

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onEvent(RenderWorldEvent.Pre event) {
		HELightManager.onPreRender(event.renderer.worldObj, event.renderer.posX, event.renderer.posY, event.renderer.posZ);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onEvent(RenderWorldEvent.Post event) {
		HETessalator.onPostRender(event.renderer.worldObj, event.renderer.posX, event.renderer.posY, event.renderer.posZ);
	}
}
