package de.tubeplay.bedwars.listener;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.tubeplay.bedwars.Bedwars;
import de.tubeplay.bedwars.MySQL;

public class QuitEvent implements Listener {
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		
		Player p = event.getPlayer();
		Scoreboard board = Bedwars.getScoreboard();
		Objective ob = board.getObjective(DisplaySlot.SIDEBAR);
		String realName = p.getName();
		String name = p.getDisplayName();
		Bedwars.calculateMinutesOnline(p);
		Bedwars.getBuildModePlayers().remove(realName);
		
		if (Bedwars.isGameStarted()) {
			
			if (p.getAllowFlight() || Bedwars.isGameStopped()) {
				
				event.setQuitMessage(null);
				
			} else {
				
				Team red = board.getTeam("Rot");
				Team green = board.getTeam("Grün");
				Team blue = board.getTeam("Blau");
				Team yellow = board.getTeam("Gelb");
				int redTeamPlayers = red.getSize();
				int greenTeamPlayers = green.getSize();
				int blueTeamPlayers = blue.getSize();
				int yellowTeamPlayers = yellow.getSize();
				boolean statsDisabled = Bedwars.isStatsDisabled();
				Bedwars main = Bedwars.getMain();
				MySQL mySQL = Bedwars.getMySQL();
				BukkitScheduler scheduler = Bukkit.getScheduler();
				
				if (!statsDisabled) {
					
					scheduler.runTaskAsynchronously(main, new Runnable() {
						
						@Override
						public void run() {
							
							mySQL.addToDeaths(p.getUniqueId().toString());
						}
					});
				}
				
				event.setQuitMessage("§6[§3Bedwars§6] §cDer Spieler " + p.getDisplayName() + " §chat die Runde vorzeitig verlassen!");
				
				EntityDamageEvent damageEvent = p.getLastDamageCause();
				
				if (damageEvent != null && damageEvent.getCause().equals(DamageCause.ENTITY_ATTACK)) {
					
					EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) damageEvent;
					Player damager = (Player) damageByEntityEvent.getDamager();
					String damagerName = damager.getName();
					HashMap<String, Integer> kills = Bedwars.getKills();
					
					if (statsDisabled) {
						
						damager.sendMessage("§6[§3Bedwars§6] §aStats: §4deaktiviert");
						
					} else {
						
						scheduler.runTaskAsynchronously(main, new Runnable() {
							
							@Override
							public void run() {
								
								mySQL.addToKills(damager.getUniqueId().toString());;
							}
						});
						
						damager.sendMessage("§6[§3Bedwars§6] §aStats: §6Kills §3+1");
						
						if (kills.containsKey(damagerName)) {
							
							kills.put(damagerName, kills.get(damagerName) + 1);
							
						} else {
							
							kills.put(damagerName, 1);
						}
					}
				}
				
				if (name.contains("§4")) {
					
					redTeamPlayers--;
					red.removeEntry(realName);
					
					if (redTeamPlayers == 0) {
						
						Bedwars.destroyBedRedAndSendMessage();
						addBedToDamager(p);
					}
					
					if (Bedwars.isRedTeamBed()) {
						
						ob.getScore("§a✔ §4Rot").setScore(redTeamPlayers);
						
					} else {
						
						ob.getScore("§c✘ §4Rot").setScore(redTeamPlayers);
					}
					
				} else if (name.contains("§2")) {
					
                    greenTeamPlayers--;
					green.removeEntry(realName);
					
					if (greenTeamPlayers == 0) {
						
						Bedwars.destroyBedGreenAndSendMessage();
						addBedToDamager(p);
					}
					
					if (Bedwars.isGreenTeamBed()) {
						
						ob.getScore("§a✔ §2Grün").setScore(redTeamPlayers);
						
					} else {
						
						ob.getScore("§c✘ §2Grün").setScore(redTeamPlayers);
					}
					
				} else if (name.contains("§1")) {
					
                    blueTeamPlayers--;
					blue.removeEntry(realName);
					
					if (blueTeamPlayers == 0) {
						
						Bedwars.destroyBedBlueAndSendMessage();
						addBedToDamager(p);
					}
					
					if (Bedwars.isBlueTeamBed()) {
						
						ob.getScore("§a✔ §1Blau").setScore(redTeamPlayers);
						
					} else {
						
						ob.getScore("§c✘ §1Blau").setScore(redTeamPlayers);
					}
					
				} else {
					
                    yellowTeamPlayers--;
					yellow.removeEntry(realName);
					
					if (yellowTeamPlayers == 0) {
						
						Bedwars.destroyBedYellowAndSendMessage();
						addBedToDamager(p);
					}
					
					if (Bedwars.isYellowTeamBed()) {
						
						ob.getScore("§a✔ §eGelb").setScore(redTeamPlayers);
						
					} else {
						
						ob.getScore("§c✘ §eGelb").setScore(redTeamPlayers);
					}
				}
				Bedwars.findOutIfSomeoneWon(redTeamPlayers, greenTeamPlayers, blueTeamPlayers, yellowTeamPlayers);
			}
			
		} else {
			
			int onlinePlayersBefore = Bukkit.getOnlinePlayers().size();
			int onlinePlayers = onlinePlayersBefore - 1;
			event.setQuitMessage("§c[-] §r" + event.getPlayer().getDisplayName());
			
			if (onlinePlayers == 7) {
				
				board.resetScores("§l§7» §a8§7/§a16");
				ob.getScore("§l§7» §c7§7/§a16").setScore(7);
				Bedwars.cancelCountdown();
				Bukkit.broadcastMessage("§6[§3Bedwars§6] §cCountdown beendet, da weniger als acht Spieler Online sind!");
				Bedwars.setCountdown(Bedwars.getOriginalCountdown());
				Bedwars.updateCountdown(); 
				
			} else if (onlinePlayers < 7) {
				
				board.resetScores("§l§7» §c" + onlinePlayersBefore + "§7/§a16");
				ob.getScore("§l§7» §c" + onlinePlayers + "§7/§a16").setScore(7);
				
			} else {
				
				board.resetScores("§l§7» §a" + onlinePlayersBefore + "§7/§a16");
				ob.getScore("§l§7» §a" + onlinePlayers + "§7/§a16").setScore(7);
			}
		}
	}
	
	private void addBedToDamager(Player p) {
		
		EntityDamageEvent damageEvent = p.getLastDamageCause();
		
		if (damageEvent != null && damageEvent.getCause().equals(DamageCause.ENTITY_ATTACK)) {
			
			EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) damageEvent;
			Player damager = (Player) damageByEntityEvent.getDamager();
			String damagerName = damager.getName();
			HashMap<String, Integer> destroyedBeds = Bedwars.getDestroyedBeds();
			
			if (Bedwars.isStatsDisabled()) {
				
				damager.sendMessage("§6[§3Bedwars§6] §aStats: §4deaktiviert");
				
			} else {
				
				Bukkit.getScheduler().runTaskAsynchronously(Bedwars.getMain(), new Runnable() {
					
					@Override
					public void run() {
						
						Bedwars.getMySQL().addToDestroyedBeds(damager.getUniqueId().toString());
					}
				});
				
				damager.sendMessage("§6[§3Bedwars§6] §aStats: §6Zerstörte Betten §3+1");
				
				if (destroyedBeds.containsKey(damagerName)) {
					
					destroyedBeds.put(damagerName, destroyedBeds.get(damagerName) + 1);
					
				} else {
					
					destroyedBeds.put(damagerName, 1);
				}
			}
		}
	}
}
