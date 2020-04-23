package com.sinthoras.hydroenergy;

import com.sinthoras.hydroenergy.controller.HEController;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class HECommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "setwater";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/setwater <controllerId> <waterLevel>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) {
		HE.LOG.info("Hello World");
		boolean flag = true;
		
		if(params.length != 2) flag = false;
		else
		{
			try
			{
				int controllerId = Integer.parseInt(params[0]);
				if (controllerId < 0 || controllerId > 15) flag = false;
				
				float waterLevel = Float.parseFloat(params[1]);
				
				if(flag)
				{
					HEController.setWaterLevel(controllerId, waterLevel);
			        sender.addChatMessage(new ChatComponentText("Set water level of controller " + controllerId + " to " + waterLevel));
				}
			}
			catch(Exception ex)
			{
				flag = false;
			}
		}
		if(!flag)
			sender.addChatMessage(new ChatComponentText("Could not parse /setwater!\n" + getCommandUsage(null)));
	}

}
