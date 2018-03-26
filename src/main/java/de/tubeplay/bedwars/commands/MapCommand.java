package de.tubeplay.bedwars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.tubeplay.bedwars.Bedwars;

public class MapCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("map")) {
			
			sender.sendMessage("§3Daten der Karte");
			
			if (Bedwars.isGameStarted()) {
				
				String map = Bedwars.getMap();
				
				sender.sendMessage("§6Name: §3" + map + "\n"
						+ "§6Erbauer: §3" + Bedwars.getMapBuilder());
				
		        switch (map) {
		        
		        case "Pilz":
		        	
		    		sender.sendMessage("§6Link: §3https://www.youtube.com/watch?v=fKEXuNUdwbE");
		    		break;
		    		
		        case "Phizzle":
		        	
		    		sender.sendMessage("§6Link: §3https://www.youtube.com/watch?v=wZGPpaHwdvw");
		        	break;
		        	
		        case "Atlantis":
		        	
		        	sender.sendMessage("§6Link: §3https://www.youtube.com/watch?v=zoJc8vZGxsM");
		        	break;
		        	
		        case "Industry":
		        	
		        	sender.sendMessage("§6Link: §3https://www.youtube.com/watch?v=5vgxUPgpg2Y");
		    		break;
		    		
		        case "Sonic":
		        	
		        	sender.sendMessage("§6Link: §3https://www.youtube.com/watch?v=hdUOu_nyWPQ");
		    		break;
		    		
		        case "Frozen":
		        	
		        	sender.sendMessage("§6Link: §3https://www.youtube.com/watch?v=B9I5BhAm6KE");
		    		break;
		    		
		        default:
		        	
		        	sender.sendMessage("§6Link: §3https://www.youtube.com/watch?v=tVJO8xIlNa0");
		    		break;
		        }
				
			} else {
				
				sender.sendMessage("§6Name: §3Unbekannt\n"
						+ "§6Erbauer: §3Unbekannt\n"
						+ "§6Link: §3Unbekannt");
			}
			return true;
		}
		return false;
	}
}
