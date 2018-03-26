package de.tubeplay.bedwars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tubeplay.bedwars.Bedwars;
import de.tubeplay.bedwars.MySQL;

public class StatsCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
    	if (command.getName().equalsIgnoreCase("stats")) {
    		
    		String name = null;
    		int argsLength = args.length;
    		
    		if (argsLength != 0 && !(args[0].equalsIgnoreCase("global") || args[0].equalsIgnoreCase("monthly") || args[0].equalsIgnoreCase("daily"))) {
    			
    			name = args[0];
    			
    		} else {
    			
    			if (sender instanceof Player) {
    				
    				Player p = (Player) sender;
    				name = p.getDisplayName();
    			}
    		}
    		
    		if (name != null) {
    			
        		MySQL mysql = Bedwars.getMySQL();
    			
    			if (name.equalsIgnoreCase("delete") && args[1] != null && sender.hasPermission("tubeplay.deleteStats")) {
        			
    				String stats = null;
    				
    				if (argsLength == 2) {
    					
    					stats = "Global";
    					
    				} else if (args[2].equalsIgnoreCase("global") || args[2].equalsIgnoreCase("monthly") || args[2].equalsIgnoreCase("daily")){
    					
    					stats = args[2];
    				}
    				
    				if (stats != null) {
    					
        				if (mysql.uuidExists(args[1])) {
        					
            				String uuid = mysql.getUUID(args[1]);
            				
            				if (mysql.playerExistsInStats(uuid, stats)) {
            					
            					mysql.update("UPDATE StatsDaily SET Points='0' WHERE UUID='" + uuid + "'");
            					mysql.update("UPDATE StatsDaily SET DestroyedBeds='0' WHERE UUID='" + uuid + "'");
            					mysql.update("UPDATE StatsDaily SET Points='0' WHERE UUID='" + uuid + "'");
            					mysql.update("UPDATE StatsDaily SET Kills='0' WHERE UUID='" + uuid + "'");
            					mysql.update("UPDATE StatsDaily SET Deaths='0' WHERE UUID='" + uuid + "'");
            					mysql.update("UPDATE StatsDaily SET WonGames='0' WHERE UUID='" + uuid + "'");
            					mysql.update("UPDATE StatsDaily SET PlayedGames='0' WHERE UUID='" + uuid + "'");
            					
            					if (!stats.equalsIgnoreCase("daily")) {
            						
                					mysql.update("UPDATE StatsMonthly SET Points='0' WHERE UUID='" + uuid + "'");
                					mysql.update("UPDATE StatsMonthly SET DestroyedBeds='0' WHERE UUID='" + uuid + "'");
                					mysql.update("UPDATE StatsMonthly SET Points='0' WHERE UUID='" + uuid + "'");
                					mysql.update("UPDATE StatsMonthly SET Kills='0' WHERE UUID='" + uuid + "'");
                					mysql.update("UPDATE StatsMonthly SET Deaths='0' WHERE UUID='" + uuid + "'");
                					mysql.update("UPDATE StatsMonthly SET WonGames='0' WHERE UUID='" + uuid + "'");
                					mysql.update("UPDATE StatsMonthly SET PlayedGames='0' WHERE UUID='" + uuid + "'");
            					}
            					
            					if (stats.equalsIgnoreCase("global")) {
            						
                					mysql.update("UPDATE StatsGlobal SET Points='0' WHERE UUID='" + uuid + "'");
                					mysql.update("UPDATE StatsGlobal SET DestroyedBeds='0' WHERE UUID='" + uuid + "'");
                					mysql.update("UPDATE StatsGlobal SET Points='0' WHERE UUID='" + uuid + "'");
                					mysql.update("UPDATE StatsGlobal SET Kills='0' WHERE UUID='" + uuid + "'");
                					mysql.update("UPDATE StatsGlobal SET Deaths='0' WHERE UUID='" + uuid + "'");
                					mysql.update("UPDATE StatsGlobal SET WonGames='0' WHERE UUID='" + uuid + "'");
                					mysql.update("UPDATE StatsGlobal SET PlayedGames='0' WHERE UUID='" + uuid + "'");
            					}
            					sender.sendMessage("§6[§3Bedwars§6] §aStats des Spielers wurden erfolgreich gelöscht!");
            					return true;
            					
            				} else {
            					
            					sender.sendMessage("§6[§3Bedwars§6] §cDieser Spieler hat keine Statistiken!");
                				return true;
            				}
            				
        				} else {
        					
        					sender.sendMessage("§6[§3Bedwars§6] §cDieser Spieler hat keine Statistiken!");
            				return true;
        				}
    				}
        			
        		} else if (mysql.uuidExists(name)) {
        			
        			String uuid = mysql.getUUID(name);
        			String stats = null;
        			
        			if (argsLength == 0) {
        				
        				stats = "Global";
        				
        			} else if (args[0].equalsIgnoreCase("global") || args[0].equalsIgnoreCase("monthly") || args[0].equalsIgnoreCase("daily")) {
        				
    					stats = args[0].toLowerCase();
    					stats = stats.substring(0, 1).toUpperCase() + stats.substring(1);
    					
        			} else {
        				
        				if (argsLength == 1) {
        					
        					stats = "Global";
        					
        				} else if (args[1].equalsIgnoreCase("global") || args[1].equalsIgnoreCase("monthly") || args[1].equalsIgnoreCase("daily")){
        					
        					stats = args[1].toLowerCase();
        					stats = stats.substring(0, 1).toUpperCase() + stats.substring(1);
        				}
        			}
        			
        			if (stats != null && mysql.playerExistsInStats(uuid, stats)) {
        				
            			int playedGames = mysql.getPlayedGames(uuid, stats);
            			int wonGames = mysql.getWonGames(uuid, stats);
            			int kills = mysql.getKills(uuid, stats);
            			int deaths = mysql.getDeaths(uuid, stats);
            			double winrate;
            			double kd;
            			
            			if (playedGames == 0) {
            				
            				winrate = 0.0;
            				
            			} else {
            				
            				winrate = wonGames * 100 / playedGames;
            			}
            			
            			if (deaths == 0) {
            				
            				kd = kills;
            				
            			} else {
            				
            				kd = kills / deaths;
            			}
            			
            			sender.sendMessage("§6" + stats + " §3Stats von §6" + name+ "\n"
            					+ "§6Zerstörte Betten: §3" +  mysql.getDestroyedBeds(uuid, stats) + "\n"
            					+ "§6Kills: §3" + kills + "\n"
            					+ "§6Deaths: §3" + deaths + "\n"
            					+ "§6K/D: §3" + kd + "\n"
            					+ "§6Gespielte Spiele: §3" + playedGames + "\n"
            					+ "§6Verlorene Spiele: §3" + (playedGames - wonGames) + "\n"
            					+ "§6Gewonnene Spiele: §3" + wonGames + "\n"
            					+ "§6Siegeswahrscheinlichkeit: §3" + winrate + "%\n"
            					+ "§6Punkte: §3" + mysql.getPoints(uuid, stats) + "\n");
            			
            			return true;
        				
        			} else {
        				
        				sender.sendMessage("§6[§3Bedwars§6] §cDieser Spieler hat keine Statistiken!");
        				return true;
        			}
        			
    			} else {
    				
    				sender.sendMessage("§6[§3Bedwars§6] §cDieser Spieler hat keine Statistiken!");
    				return true;
    			}
    		}
    	}
    	return false;
	}
}
