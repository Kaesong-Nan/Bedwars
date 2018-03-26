package de.tubeplay.bedwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;

import de.tubeplay.bedwars.Bedwars;

public class StartCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equals("start")) {
			
			if (Bedwars.isGameStarted()) {
				
				sender.sendMessage("§6[§3Bedwars§6] §cDas Spiel ist bereits gestartet!");
				
			} else if (sender.hasPermission("tubeplay.start")) {
					
				if (sender.hasPermission("tubeplay.startCountdown") && args.length != 0) {
					
					int countdown = 0;
					
					try {
						
						countdown = Integer.valueOf(args[0]);
						
					} catch (NumberFormatException e) {
						
						sender.sendMessage("§6[§3Bedwars§6] §cBitte gebe eine gültige Zahl im Bereich von zehn bis 20000 an!");
						return true;
					}
					
					if (countdown < 10 || countdown > 20000) {
						
						sender.sendMessage("§6[§3Bedwars§6] §cCountdown kann nicht kleiner als zehn und nicht größer als 20000 sein!");
						
					} else {
						
						Bedwars.setOriginalCountdown(countdown);
						sender.sendMessage("§6[§3Bedwars§6] §aCountdown erfolgreich auf " + countdown + " gesetzt!");
					}
					return true;
					
				} else {
					
	            	int onlinePlayers = Bukkit.getOnlinePlayers().size();
	            	Scoreboard board = Bedwars.getScoreboard();
	            	
	            	if (onlinePlayers == board.getTeam("Rot").getSize() || onlinePlayers == board.getTeam("Grün").getSize() || onlinePlayers == board.getTeam("Blau").getSize() || onlinePlayers == board.getTeam("Gelb").getSize()) {
	            		
	            		sender.sendMessage("§6[§3Bedwars§6] §cDas Spiel kann nicht gestartet werden, wenn alle Spieler im selben Team sind!");
	            		
	            	} else if (onlinePlayers == 1) {
	            		
	            		sender.sendMessage("§6[§3Bedwars§6] §cDas Spiel kann nicht mit nur einem Spieler gestartet werden!");
						
	            	} else {
	            		
	            		Bedwars.start();
	            	}
					return true;
				}
			}
		}
		return false;
	}
}
