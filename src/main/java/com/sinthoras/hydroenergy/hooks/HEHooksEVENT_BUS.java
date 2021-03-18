package com.sinthoras.hydroenergy.hooks;

import com.sinthoras.hydroenergy.client.light.HELightManager;
import com.sinthoras.hydroenergy.client.renderer.HETessalator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldEvent;

public class HEHooksEVENT_BUS {

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onEvent(RenderWorldEvent.Pre event) {
		HELightManager.onPreRender(event.renderer.worldObj, event.renderer.posX, event.renderer.posY, event.renderer.posZ);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onEvent(RenderWorldEvent.Post event) {
		HETessalator.onPostRender(event.renderer.worldObj, event.renderer.posX, event.renderer.posY, event.renderer.posZ);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onEvent(RenderGameOverlayEvent.Text event) {
		if(Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			event.right.add("HydroEnergy GPU RAM: " + (HETessalator.getGpuMemoryUsage() >> 20) + "MB");  // Byte / 1024 / 1024
		}
	}
}
