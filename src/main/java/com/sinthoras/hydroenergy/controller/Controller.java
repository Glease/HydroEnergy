package com.sinthoras.hydroenergy.controller;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.event.ServerChatEvent;

public class Controller {
	public static float waterlevel = 3.5f;
	
	@SubscribeEvent
	public void onServerChat(ServerChatEvent e)
	{
		System.out.println(e.message);
		e.setCanceled(true);
	}
}
