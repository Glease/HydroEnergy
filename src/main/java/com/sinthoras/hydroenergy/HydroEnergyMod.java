package com.sinthoras.hydroenergy;

import com.sinthoras.hydroenergy.proxy.HECommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = HE.MODID, version = HE.VERSION, name = HE.NAME)
public class HydroEnergyMod
{
	@SidedProxy(clientSide="com.sinthoras.hydroenergy.proxy.HEClientProxy", serverSide="com.sinthoras.hydroenergy.proxy.HECommonProxy")
	public static HECommonProxy proxy;

    @EventHandler
	// preInit "Run before anything else. Read your config, create blocks, items, 
	// etc, and register them with the GameRegistry."
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event) 
	{    
		HE.LOG.info("preInit()"+event.getModMetadata().name);
    	proxy.fmlLifeCycleEvent(event);
	}
	
	@EventHandler
	// load "Do your mod setup. Build whatever data structures you care about. Register recipes."
	public void fmlLifeCycleEvent(FMLInitializationEvent event) 
	{
		HE.LOG.info("init()");
		proxy.fmlLifeCycleEvent(event);
	}

	@EventHandler
	// postInit "Handle interaction with other mods, complete your setup based on this."
	public void fmlLifeCycle(FMLPostInitializationEvent event)
	{
		HE.LOG.info("postInit()");
		proxy.fmlLifeCycleEvent(event);
	}
	
	@EventHandler
	public void fmlLifeCycle(FMLServerAboutToStartEvent event)
	{
		HE.LOG.info("Server about to start");
		proxy.fmlLifeCycleEvent(event);
	}
	
	@EventHandler
	// register server commands in this event handler
	public void fmlLifeCycle(FMLServerStartingEvent event)
	{
		HE.LOG.info("Server starting");
		proxy.fmlLifeCycleEvent(event);
	}
	
	@EventHandler
	public void fmlLifeCycle(FMLServerStartedEvent event)
	{
		HE.LOG.info("Server started");
		proxy.fmlLifeCycleEvent(event);
	}
	
	@EventHandler
	public void fmlLifeCycle(FMLServerStoppingEvent event)
	{
		HE.LOG.info("Server stopping");
		proxy.fmlLifeCycleEvent(event);
	}
	
	@EventHandler
	public void fmlLifeCycle(FMLServerStoppedEvent event)
	{
		HE.LOG.info("Server stopped");
		proxy.fmlLifeCycleEvent(event);
	}
}
