package com.sinthoras.hydroenergy.commands;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.network.HEPacketDebug;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class HECommandDebug extends CommandBase {

	@Override
	public String getCommandName() {
		return "hedebug";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return getCommandName() + " <on|off>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) {
		boolean couldParse = false;
		if(params.length == 1)
		{
			if(params[0].equalsIgnoreCase("on"))
			{
				//HE.network.sendTo(new HEPacketDebug(true), MinecraftServer.getServer().getConfigurationManager().func_152612_a(sender.getCommandSenderName()));
				HE.DEBUGslowFill = true;
				couldParse = true;
			}
			else if(params[0].equalsIgnoreCase("off"))
			{
				//HE.network.sendTo(new HEPacketDebug(false), MinecraftServer.getServer().getConfigurationManager().func_152612_a(sender.getCommandSenderName()));
				couldParse = true;
				HE.DEBUGslowFill = false; 
			}
		}
		if(!couldParse)
			sender.addChatMessage(new ChatComponentText("Could not parse command!\n" + getCommandUsage(null)));
	}

}
