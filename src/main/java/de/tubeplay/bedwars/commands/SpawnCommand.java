package de.tubeplay.bedwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tubeplay.bedwars.Bedwars;

public class SpawnCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("spawn")) {
			
			if (sender instanceof Player) {
				
				Player p = (Player) sender;
				
				if (Bedwars.isGameStarted()) {
					
					p.sendMessage("§6[§3Bedwars§6] §cDieser Befehl kann nur in der Wartelobby ausgeführt werden!");
					
				} else {
					
					Location spawn = new Location(Bukkit.getWorlds().get(0), 28.5, 118, -7.5, 90, 0);
					p.teleport(spawn);
					p.sendMessage("§6[§3Bedwars§6] §aDu wurdest teleportiert!");
				}
				
			} else {
				
				sender.sendMessage("[Bedwars] Dieser Befehl kann nur ingame ausgeführt werden!");
			}
			return true;
		}
		return false;
	}
}
