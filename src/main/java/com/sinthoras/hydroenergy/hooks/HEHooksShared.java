package com.sinthoras.hydroenergy.hooks;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.network.*;
import com.sinthoras.hydroenergy.server.commands.HECommandDebug;
import com.sinthoras.hydroenergy.server.commands.HECommandListControllers;
import com.sinthoras.hydroenergy.server.commands.HECommandSetWater;
import com.sinthoras.hydroenergy.blocks.HEControllerBlock;
import com.sinthoras.hydroenergy.blocks.HEControllerTileEntity;
import com.sinthoras.hydroenergy.server.HEServer;
import com.sinthoras.hydroenergy.blocks.HEWaterStill;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;

public class HEHooksShared {
	
	// preInit "Run before anything else. Read your config, create blocks, items, 
	// etc, and register them with the GameRegistry."
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event) 	{
    	HE.network = NetworkRegistry.INSTANCE.newSimpleChannel("hydroenergy");
    	HE.network.registerMessage(HEPacketWaterUpdate.Handler.class, HEPacketWaterUpdate.class, 0, Side.CLIENT);
    	HE.network.registerMessage(HEPacketSynchronize.Handler.class, HEPacketSynchronize.class, 1, Side.CLIENT);
		HE.network.registerMessage(HEPacketConfigUpdate.Handler.class, HEPacketConfigUpdate.class, 2, Side.CLIENT);
		HE.network.registerMessage(HEPacketConfigRequest.Handler.class, HEPacketConfigRequest.class, 3, Side.SERVER);
		HE.network.registerMessage(HEPacketChunkUpdate.Handler.class, HEPacketChunkUpdate.class, 4, Side.CLIENT);


		HE.LOG.info("The subsequent " + HE.maxControllers + " liquid errors are intendend. Please ignore...");

		for(int id=0;id<HE.waterBlocks.length;id++) {
			HE.waterBlocks[id] = new HEWaterStill(id);
			GameRegistry.registerBlock(HE.waterBlocks[id], HE.waterBlocks[id].getUnlocalizedName());
			HE.waterBlockIds[id] = Block.blockRegistry.getIDForObject(HE.waterBlocks[id]);
		}
    	HE.controller = new HEControllerBlock();
		GameRegistry.registerBlock(HE.controller, HE.controller.getUnlocalizedName());
		GameRegistry.registerTileEntity(HEControllerTileEntity.class, "he_controller_tile_entity");
	}
	
	// load "Do your mod setup. Build whatever data structures you care about. Register recipes."
	public void fmlLifeCycleEvent(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new HEHooksFML());
		MinecraftForge.EVENT_BUS.register(new HEHooksEVENT_BUS());
	}
	
	// postInit "Handle interaction with other mods, complete your setup based on this."
	public void fmlLifeCycleEvent(FMLPostInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(HE.MODID, HE.guiHandler);
	}
	
	public void fmlLifeCycleEvent(FMLServerAboutToStartEvent event) {

	}

	// register server commands in this event handler
	public void fmlLifeCycleEvent(FMLServerStartingEvent event) {
		event.registerServerCommand(new HECommandSetWater());
		event.registerServerCommand(new HECommandDebug());
		event.registerServerCommand(new HECommandListControllers());
		if(event.getSide() == Side.SERVER || event.getServer().isSinglePlayer()) {
			HEServer.instance = HEServer.load(event.getServer().worldServers[0]);
		}
	}
	
	public void fmlLifeCycleEvent(FMLServerStartedEvent event) {
		
	}
	
	public void fmlLifeCycleEvent(FMLServerStoppingEvent event) {
		
	}
	
	public void fmlLifeCycleEvent(FMLServerStoppedEvent event) {
		
	}
}
