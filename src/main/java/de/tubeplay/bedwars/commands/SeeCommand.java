package de.tubeplay.bedwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class SeeCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("see")) {
			
			if (sender instanceof Player) {
				
				Player p = (Player) sender;
				
				if (p.hasPermission("tubeplay.see")) {
					
					p.removePotionEffect(PotionEffectType.INVISIBILITY);
					
					for (Player op : Bukkit.getOnlinePlayers()) {
						
						op.showPlayer(p);
					}
					return true;
				}
				
			} else {
				
				sender.sendMessage("[Bedwars] Diese Befehl kann nur ingame ausgeführt werden, da er den Spieler sichtbar gegenüber allen anderen macht!");
				return true;
			}
		}
		return false;
	}
}
