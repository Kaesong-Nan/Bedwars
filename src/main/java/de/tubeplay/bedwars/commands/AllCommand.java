package de.tubeplay.bedwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tubeplay.bedwars.Bedwars;

public class AllCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		String commandName = command.getName();
		
		if ((commandName.equalsIgnoreCase("all") || commandName.equalsIgnoreCase("a")) && args.length != 0 && sender instanceof Player && Bedwars.isGameStarted()) {
			
			Player p = (Player) sender;
			String message = "§7[§3@all§7] §r<" +  p.getDisplayName() + "> ";
			
			for (String word : args) {
				
				message = message + word;
			}
			Bukkit.broadcastMessage(message);
			return true;
		}
		return false;
	}

}
