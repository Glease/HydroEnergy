package com.sinthoras.hydroenergy;

import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import com.sinthoras.hydroenergy.controller.Controller;
import com.sinthoras.hydroenergy.hewater.HEWater;
import com.sinthoras.hydroenergy.hewater.HEWaterFakeRenderer;
import com.sinthoras.hydroenergy.hewater.HEWaterRenderer;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Main.MODID, version = Main.VERSION, name = Main.NAME)
public class Main
{
    public static final String MODID = "hydroenergy";
    public static final String VERSION = "1.0";
    public static final String NAME = "HydroEnergy";
    
    public static HEWater water = new HEWater();
    
    @EventHandler
    public void pre(FMLPreInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(new HEWaterRenderer());
    	MinecraftForge.EVENT_BUS.register(new Controller());
    	GameRegistry.registerBlock(water, water.getUnlocalizedName());
    	RenderingRegistry.registerBlockHandler(HEWaterFakeRenderer.instance);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		
    }
    
    @EventHandler
    public void post(FMLPostInitializationEvent event)
    {
		
    }
}
