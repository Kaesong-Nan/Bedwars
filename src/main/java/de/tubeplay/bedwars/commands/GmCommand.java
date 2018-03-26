package de.tubeplay.bedwars.commands;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tubeplay.bedwars.Bedwars;

public class GmCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("gm")) {
			
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
        	
        	if (p != null && sender.hasPermission("minecraft.command.gamemode")) {
        		
        		GameMode pGameMode = p.getGameMode();
        		
        		if (pGameMode == GameMode.CREATIVE) {
        			
        			p.setGameMode(GameMode.SURVIVAL);
        			
                    if (argsLength == 0) {
						
						p.sendMessage("§6[§3Bedwars§6] §aDu bist nun im Überlebensmodus!");
						
					} else {
						
						p.sendMessage("§6[§3Bedwars§6] §aDu wurdest in den Überlebensmodus gesetzt!");
						sender.sendMessage("§6[§3Bedwars§6] §aDie Person wurde in den Überlebensmodus gesetzt!");
					}
        			
        		} else if (pGameMode == GameMode.SURVIVAL) {
        			
        			p.setGameMode(GameMode.CREATIVE);
        			
                    if (argsLength == 0) {
						
						p.sendMessage("§6[§3Bedwars§6] §aDu bist nun im Kreativmodus!");
						
					} else {
						
						p.sendMessage("§6[§3Bedwars§6] §aDu wurdest in den Kreativmodus gesetzt!");
						sender.sendMessage("§6[§3Bedwars§6] §aDie Person wurde in den Kreativmodus gesetzt!");
					}
        			
        		} else {
        			
                    if (argsLength == 0) {
						
						p.sendMessage("§6[§3Bedwars§6] §aDu bist weder im Kreativ- noch im Überlebensmodus!");
						
					} else {
						
						sender.sendMessage("§6[§3Bedwars§6] §aDie Person ist weder im Kreativ- noch im Überlebensmodus!");
					}
        		}
                return true;
        	}
		}
		return false;
	}
}
