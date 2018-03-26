package de.tubeplay.bedwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tubeplay.bedwars.Bedwars;

public class DisableStatsCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("disablestats")) {
			
			if (sender.hasPermission("tubeplay.deleteStats")) {
				
				if (Bedwars.isStatsDisabled()) {
					
					Bukkit.broadcastMessage("§6[§3Bedwars§6] §aDie Stats sind nun wieder aktiviert!");
					Bedwars.setStatsDisabled(false);
					
				} else {
					
					Bukkit.broadcastMessage("§6[§3Bedwars§6] §cDie Stats sind nun bis zum Ende der Runde deaktiviert!");
					Bedwars.setStatsDisabled(true);
				}
				return true;
			}
		}
		return false;
	}

}
