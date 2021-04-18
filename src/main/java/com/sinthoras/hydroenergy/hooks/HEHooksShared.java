package com.sinthoras.hydroenergy.hooks;

import codechicken.nei.api.API;
import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.HETags;
import com.sinthoras.hydroenergy.blocks.*;
import com.sinthoras.hydroenergy.config.HEConfig;
import com.sinthoras.hydroenergy.network.packet.*;
import com.sinthoras.hydroenergy.recipes.CraftingRecipeLoader;
import com.sinthoras.hydroenergy.recipes.Hydro_ItemList;
import com.sinthoras.hydroenergy.recipes.MachineRecipeLoader;
import com.sinthoras.hydroenergy.server.commands.HECommandDebug;
import com.sinthoras.hydroenergy.server.commands.HECommandListControllers;
import com.sinthoras.hydroenergy.server.commands.HECommandSetWater;
import com.sinthoras.hydroenergy.server.HEServer;

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
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;


public class HEHooksShared {
	
	// preInit "Run before anything else. Read your config, create blocks, items, 
	// etc, and register them with the GameRegistry."
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event) 	{
		HEConfig.syncronizeConfiguration(event.getSuggestedConfigurationFile());

    	HE.network = NetworkRegistry.INSTANCE.newSimpleChannel("hydroenergy");
    	HE.network.registerMessage(HEPacketWaterUpdate.Handler.class, HEPacketWaterUpdate.class, 0, Side.CLIENT);
    	HE.network.registerMessage(HEPacketSynchronize.Handler.class, HEPacketSynchronize.class, 1, Side.CLIENT);
		HE.network.registerMessage(HEPacketConfigUpdate.Handler.class, HEPacketConfigUpdate.class, 2, Side.CLIENT);
		HE.network.registerMessage(HEPacketConfigRequest.Handler.class, HEPacketConfigRequest.class, 3, Side.SERVER);
		HE.network.registerMessage(HEPacketChunkUpdate.Handler.class, HEPacketChunkUpdate.class, 4, Side.CLIENT);


		HE.info("The subsequent " + HEConfig.maxDams + " liquid errors are intendend. Please ignore...");

		for(int waterId = 0; waterId<HEConfig.maxDams; waterId++) {
			HE.waterBlocks[waterId] = new HEWaterStill(waterId);
			GameRegistry.registerBlock(HE.waterBlocks[waterId], HE.waterBlocks[waterId].getUnlocalizedName());
			HE.waterBlockIds[waterId] = Block.blockRegistry.getIDForObject(HE.waterBlocks[waterId]);
			API.hideItem(new ItemStack(HE.waterBlocks[waterId]));
		}

		FluidRegistry.registerFluid(HE.pressurizedWater);
	}
	
	// load "Do your mod setup. Build whatever data structures you care about. Register recipes."
	public void fmlLifeCycleEvent(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new HEHooksFML());
		MinecraftForge.EVENT_BUS.register(new HEHooksEVENT_BUS());

		Hydro_ItemList.HydroDam.set(new HEHydroDamTileEntity(HEConfig.blockIdOffset, "he_dam", "Hydro Dam").getStackForm(1L));
		for(String tier : HEConfig.enabledTiers) {
			switch(tier) {
				case "lv":
					Hydro_ItemList.Hydro_Pump_LV.set(new HEHydroPumpTileEntity.LV(HEConfig.blockIdOffset + 1).getStackForm(1L));
					Hydro_ItemList.Hydro_Dynamo_LV.set(new HEHydroTurbineTileEntity.LV(HEConfig.blockIdOffset + 17).getStackForm(1L));
					break;
				case "mv":
					Hydro_ItemList.Hydro_Pump_MV.set(new HEHydroPumpTileEntity.MV(HEConfig.blockIdOffset + 1).getStackForm(1L));
					Hydro_ItemList.Hydro_Dynamo_MV.set(new HEHydroTurbineTileEntity.MV(HEConfig.blockIdOffset + 17).getStackForm(1L));
					break;
				case "hv":
					Hydro_ItemList.Hydro_Pump_HV.set(new HEHydroPumpTileEntity.HV(HEConfig.blockIdOffset + 1).getStackForm(1L));
					Hydro_ItemList.Hydro_Dynamo_HV.set(new HEHydroTurbineTileEntity.HV(HEConfig.blockIdOffset + 17).getStackForm(1L));
					break;
				case "ev":
					Hydro_ItemList.Hydro_Pump_EV.set(new HEHydroPumpTileEntity.EV(HEConfig.blockIdOffset + 1).getStackForm(1L));
					Hydro_ItemList.Hydro_Dynamo_EV.set(new HEHydroTurbineTileEntity.EV(HEConfig.blockIdOffset + 17).getStackForm(1L));
					break;
				case "iv":
					Hydro_ItemList.Hydro_Pump_IV.set(new HEHydroPumpTileEntity.IV(HEConfig.blockIdOffset + 1).getStackForm(1L));
					Hydro_ItemList.Hydro_Dynamo_IV.set(new HEHydroTurbineTileEntity.IV(HEConfig.blockIdOffset + 17).getStackForm(1L));
					break;
			}
		}
	}
	
	// postInit "Handle interaction with other mods, complete your setup based on this."
	public void fmlLifeCycleEvent(FMLPostInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(HETags.MODID, HE.guiHandler);
		new CraftingRecipeLoader().run();
		new MachineRecipeLoader().run();
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
