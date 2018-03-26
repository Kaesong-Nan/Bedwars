package de.tubeplay.bedwars.commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tubeplay.bedwars.Bedwars;
import de.tubeplay.bedwars.MySQL;

public class TopCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("top")) {
			
			MySQL mysql = Bedwars.getMySQL();
			String stats = null;
			
			if (args.length == 0) {
				
				stats = "Global";
				
			} else if (args[0].equalsIgnoreCase("global") || args[0].equalsIgnoreCase("monthly") || args[0].equalsIgnoreCase("daily")){
				
				stats = args[0].toLowerCase();
				stats = stats.substring(0, 1).toUpperCase() + stats.substring(1);
			}
			
			if (stats != null) {
				
				byte i = 1;
				int count = 0;
				ResultSet rs = mysql.query("SELECT UUID FROM Stats" + stats + " ORDER BY Points DESC");
				sender.sendMessage("§3Top 10 Spieler §6" + stats);
				
				try {
					
					while (rs.next()) {
						
						String uuid = rs.getString("UUID");
						
						if (i < 11) {
					    	
							if (mysql.playerExists(uuid)) {
								
								sender.sendMessage("§6" + i + ". §3" + mysql.getPlayername(uuid));	
							}
							
						} else if (count != 0) {
							
							break;
						}
						
						if (sender instanceof Player) {
							
							Player p = (Player) sender;
							
							if (uuid.equalsIgnoreCase(p.getUniqueId().toString())) {
								
								count = i;
								
								if (i > 9) {
									
									break;
								}
							}
						}
						i++;
					}
					
				} catch (SQLException e) {
					
					mysql.errorReadingDatabase(e);
				}
				
				if (count != 0) {
					
					sender.sendMessage("§3Dein Platz: §6" + count);
				}
				return true;
			}
		}
		return false;
	}
}
