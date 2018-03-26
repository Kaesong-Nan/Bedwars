package de.tubeplay.bedwars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tubeplay.bedwars.Bedwars;
import de.tubeplay.bedwars.MySQL;

public class PlaytimeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("playtime")) {
			
    		String name = null;
    		
    		if (args.length != 0) {
    			
    			name = args[0];
    			
    		} else {
    			
    			if (sender instanceof Player) {
    				
    				Player p = (Player) sender;
    				name = p.getDisplayName();
    			}
    		}
        	
        	if (name != null) {
        		
        		MySQL mysql = Bedwars.getMySQL();
        		
        		if (mysql.uuidExists(name)) {
        			
        			String uuid = mysql.getUUID(name);
        			
        			if (mysql.playerExistsInPlaytime(uuid)) {
        				
        				int minutes = mysql.getMinutes(uuid);
        				
        				if (minutes == 1) {
        					
            				sender.sendMessage("§6[§3Bedwars§6] §3" + name + " §ahat in Bedwars eine Spielzeit von §6einer Minute§a.");
            				
        				} else {
        					
            				double hours = minutes / 60;
            				sender.sendMessage("§6[§3Bedwars§6] §3" + name + " §ahat in Bedwars eine Spielzeit von §6" + minutes + " Minuten §aDas sind §6" + hours + " Stunden§a.");
        				}
        				return true;
        				
        			} else {
        				
        				sender.sendMessage("§6[§3Bedwars§6] §cDieser Spieler hat keine Spielzeit in Bedwars!");
        				return true;
        			}
        			
        		} else {
        			
    				sender.sendMessage("§6[§3Bedwars§6] §cDieser Spieler hat keine Spielzeit in Bedwars!");
    				return true;
        		}
        	}
		}
		return false;
	}
}
