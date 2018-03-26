package de.tubeplay.bedwars.commands;

import java.util.HashSet;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import de.tubeplay.bedwars.Bedwars;

public class BuildCommand implements CommandExecutor {
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
        if (command.getName().equalsIgnoreCase("build")) {
			
        	Player p = null;
        	int argsLength = args.length;
        	
        	if (argsLength == 0) {
        		
        		if (sender instanceof Player) {
        			
        			p = (Player) sender;
        		}
        		
        	} else {
        		
				p = Bedwars.isPlayerOnline(args[0], sender);
        		
        		if (p == null) {
        			
        			sender.sendMessage("§6[§3Bedwars§6] §cSpieler konnte nicht gefunden werden!");
        			return true;
        		}
        	}
        	
        	if (p != null && sender.hasPermission("tubeplay.build")) {
        		
        		String name = p.getName();
				Inventory inv = p.getInventory();
				HashSet<String> buildModePlayers = Bedwars.getBuildModePlayers();
				Bedwars.clearInventoryCompletely(p);
				
				if (buildModePlayers.contains(name)) {
					
					buildModePlayers.remove(name);
					
                    if (argsLength == 0) {
						
						p.sendMessage("§6[§3Bedwars§6] §cDu bist nun nicht mehr im Bau-Modus!");
						
					} else {
						
						p.sendMessage("§6[§3Bedwars§6] §cDu wurdest aus dem Bau-Modus entfernt!");
						sender.sendMessage("§6[§3Bedwars§6] §aDie Person wurde aus dem Bau-Modus entfernt!");
					}
					
					if (!Bedwars.isGameStarted()) {
						
						Bedwars.giveLobbyItems(inv, p);
			            p.setGameMode(GameMode.SURVIVAL);
			            
					} else if (Bedwars.getScoreboard().getTeam("ZSpectator").hasEntry(p.getName())){
						
						Bedwars.spectate(p);
						
					} else {
						
						p.setFireTicks(0);
						p.setGameMode(GameMode.SURVIVAL);
					}
					return true;
					
				} else {
					
					p.setGameMode(GameMode.CREATIVE);
					buildModePlayers.add(name);
					
                    if (argsLength == 0) {
						
                    	p.sendMessage("§6[§3Bedwars§6] §aDu bist nun im Bau-Modus!");
						
					} else {
						
						p.sendMessage("§6[§3Bedwars§6] §cDu wurdest in den Bau-Modus gesetzt!");
						sender.sendMessage("§6[§3Bedwars§6] §aDie Person ist nun im Bau-Modus!");
					}
					return true;
				}
        	}
		}
		return false;
    }
}
