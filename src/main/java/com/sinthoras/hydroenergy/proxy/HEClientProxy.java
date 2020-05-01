package com.sinthoras.hydroenergy.proxy;

import com.sinthoras.hydroenergy.HECommand;
import com.sinthoras.hydroenergy.controller.HEDams;
import com.sinthoras.hydroenergy.hewater.HERenderManager;
import com.sinthoras.hydroenergy.hewater.HEWaterRenderer;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class HEClientProxy extends HECommonProxy {
	
	@Override
	// load "Do your mod setup. Build whatever data structures you care about. Register recipes."
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event) {
		super.fmlLifeCycleEvent(event);

    	RenderingRegistry.registerBlockHandler(HEWaterRenderer.instance);
	}
	
	@Override
	// load "Do your mod setup. Build whatever data structures you care about. Register recipes."
	public void fmlLifeCycleEvent(FMLInitializationEvent event) {
		super.fmlLifeCycleEvent(event);
		HEDams.init();
		HERenderManager.instance = new HERenderManager();
	}
	
	@Override
	// postInit "Handle interaction with other mods, complete your setup based on this."
	public void fmlLifeCycleEvent(FMLPostInitializationEvent event) {
		
	}
	
	@Override
	public void fmlLifeCycleEvent(FMLServerAboutToStartEvent event) {
		
	}
	
	@Override
	public void fmlLifeCycleEvent(FMLServerStartingEvent event) {
		event.registerServerCommand(new HECommand());
	}
	
	@Override
	public void fmlLifeCycleEvent(FMLServerStartedEvent event) {
		
	}
	
	@Override
	public void fmlLifeCycleEvent(FMLServerStoppingEvent event) {
		
	}
	
	@Override
	public void fmlLifeCycleEvent(FMLServerStoppedEvent event) {
		
	}
}
