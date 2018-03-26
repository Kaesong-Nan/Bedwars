package de.tubeplay.bedwars.listener;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.tubeplay.bedwars.Bedwars;
import de.tubeplay.bedwars.MySQL;

public class DeathEvent implements Listener {
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		
		Player p = event.getEntity();
		p.playSound(p.getLocation(), Sound.FALL_BIG, 1, 1);
		String name = p.getDisplayName();
		String realName = p.getName();
		Scoreboard board = Bedwars.getScoreboard();
		Objective ob = board.getObjective(DisplaySlot.SIDEBAR);
		String deathMessage = event.getDeathMessage();
		Team red = board.getTeam("Rot");
		Team green = board.getTeam("Grün");
		Team blue = board.getTeam("Blau");
		Team yellow = board.getTeam("Gelb");
		int redTeamPlayers = red.getSize();
		int greenTeamPlayers = green.getSize();
		int blueTeamPlayers = blue.getSize();
		int yellowTeamPlayers = yellow.getSize();
		event.setDroppedExp(0);
		
		deathMessage = deathMessage.replace(realName, "");
		deathMessage = deathMessage.replace(" was doomed to fall by ", "§");
		deathMessage = deathMessage.replace(" was burnt to a crisp whilst fighting ", "§");
		deathMessage = deathMessage.replace(" walked into a fire whilst fighting ", "§");
		deathMessage = deathMessage.replace(" tried to swim in lava while trying to escape ", "§");
		deathMessage = deathMessage.replace(" was slain by ", "§");
		deathMessage = deathMessage.replace(" was killed by ", "§");
		deathMessage = deathMessage.replace(" was killed while trying to hurt ", "§");
		deathMessage = deathMessage.replace(" got finished off by ", "§");
		deathMessage = deathMessage.replace(" using ", ":");
		deathMessage = deathMessage.split(":")[0];
		
		if (deathMessage.contains("shot")) {
			
			deathMessage = deathMessage.replace(" was shot by ", "");
			deathMessage = deathMessage.replace(" was shot off a ladder by ", "");
			deathMessage = deathMessage.replace(" was shot off some vines by ", "");
			checkIfHasAKill(deathMessage, p);
			deathMessage = addColorCode(deathMessage);
			event.setDeathMessage(name + " §bwurde von " + deathMessage + " §babgeschossen!");
			
		} else if (deathMessage.contains("blown")) {
			
			deathMessage = deathMessage.replace(" was blown up by ", "");
			deathMessage = deathMessage.replace(" was blown from a high place by ", "");
			checkIfHasAKill(deathMessage, p);
			deathMessage = addColorCode(deathMessage);
			event.setDeathMessage(name + " §bwurde von " + deathMessage + " §bin die Luft gesprengt!");
			
		} else if (deathMessage.contains("§")) {
			
			deathMessage = deathMessage.substring(1);
			checkIfHasAKill(deathMessage, p);
			deathMessage = addColorCode(deathMessage);
			event.setDeathMessage(name + " §bwurde von " + deathMessage + " §bgetötet!");
			
		} else {
			
			event.setDeathMessage(name + " §bist gestorben!");
		}
		
		if (name.contains("§4")) {
			
			if (Bedwars.isRedTeamBed()) {
				
				respawn(p);
				
			} else {
				
				redTeamPlayers--;
				ob.getScore("§c✘ §4Rot").setScore(redTeamPlayers);
				respawnInSpectatorMode(p);
				red.removeEntry(realName);
				
				if (redTeamPlayers == 0) {
					
					Bukkit.broadcastMessage("§6[§3Bedwars§6] §aDas Team §4Rot §awurde ausgelöscht!");	
				}
			}
			
		} else if (name.contains("§2")) {
			
			if (Bedwars.isGreenTeamBed()) {
				
				respawn(p);
				
			} else {
				
				greenTeamPlayers--;
				ob.getScore("§c✘ §2Grün").setScore(greenTeamPlayers);
				respawnInSpectatorMode(p);
				green.removeEntry(realName);
				
				if (greenTeamPlayers == 0) {
					
					Bukkit.broadcastMessage("§6[§3Bedwars§6] §aDas Team §2Grün §awurde ausgelöscht!");
				}
			}
			
		} else if (name.contains("§1")) {
			
			if (Bedwars.isBlueTeamBed()) {
				
				respawn(p);
				
			} else {
				
				blueTeamPlayers--;
				ob.getScore("§c✘ §1Blau").setScore(blueTeamPlayers);
				respawnInSpectatorMode(p);
				blue.removeEntry(realName);
				
				if (blueTeamPlayers == 0) {
					
					Bukkit.broadcastMessage("§6[§3Bedwars§6] §aDas Team §1Blau §awurde ausgelöscht!");
				}
			}
			
		} else {
			
			if (Bedwars.isYellowTeamBed()) {
				
				respawn(p);
				
			}  else {
				
				yellowTeamPlayers--;
				ob.getScore("§c✘ §eGelb").setScore(yellowTeamPlayers);
				respawnInSpectatorMode(p);
				yellow.removeEntry(realName);
				
				if (yellowTeamPlayers == 0) {
					
					Bukkit.broadcastMessage("§6[§3Bedwars§6] §aDas Team §eGelb §awurde ausgelöscht!");
				}
			}
		}
		Bedwars.findOutIfSomeoneWon(redTeamPlayers, greenTeamPlayers, blueTeamPlayers, yellowTeamPlayers);
	}
	
	private String addColorCode(String deathMessage) {
		
		for (Player op : Bukkit.getOnlinePlayers()) {
			
			if (op.getName().equalsIgnoreCase(deathMessage)) {
				
				deathMessage = op.getDisplayName();
			}
		}
		
		if (deathMessage.contains("§")) {
			
			return deathMessage;
			
		} else {
			
			return "§7" + deathMessage;
		}
	}
	
	
	private void respawn(Player p) {
		
		BukkitScheduler scheduler = Bukkit.getScheduler();
		
		scheduler.runTaskLater(Bedwars.getMain(), new Runnable() {
			
			@Override
			public void run() {
				
				p.spigot().respawn();
				Bedwars.clearInventoryCompletely(p);
            	p.teleport(Bedwars.getSpawnLocation(p.getDisplayName()));
			}
		}, 20);
	}
	
	private void checkIfHasAKill(String killer, Player killed) {
		
		String name = killed.getDisplayName();
		MySQL mysql = Bedwars.getMySQL();
		String uuid = killed.getUniqueId().toString();
		boolean statsDisabled = Bedwars.isStatsDisabled();
		
		if ((name.contains("§4") && !Bedwars.isRedTeamBed()) || (name.contains("§2") && Bedwars.isGreenTeamBed()) || (name.contains("§1") && Bedwars.isBlueTeamBed()) || (name.contains("§e") && Bedwars.isYellowTeamBed())) {
			
			if (!statsDisabled) {
				
				Bukkit.getScheduler().runTaskAsynchronously(Bedwars.getMain(), new Runnable() {
					
					@Override
					public void run() {
						
						mysql.addToDeaths(uuid);
					}
				});
				
			}
			addKill(killer);
		}
	}
	
	private void addKill(String killerName) {
		
		for (Player op : Bukkit.getOnlinePlayers()) {
			
			String opName = op.getName();
			
			if (opName.equalsIgnoreCase(killerName)) {
				
				if (Bedwars.isStatsDisabled()) {
					
					op.sendMessage("§6[§3Bedwars§6] §aStats: §4deaktiviert");
					
				} else {
					
					HashMap<String, Integer> kills = Bedwars.getKills();
					
					Bukkit.getScheduler().runTaskAsynchronously(Bedwars.getMain(), new Runnable() {
						
						@Override
						public void run() {
							
							Bedwars.getMySQL().addToKills(op.getUniqueId().toString());
						}
					});
					
					op.sendMessage("§6[§3Bedwars§6] §aStats: §6Kills §3+1");
					
					if (kills.containsKey(killerName)) {
						
						kills.put(killerName, kills.get(killerName) + 1);
						
					} else {
						
						kills.put(killerName, 1);
					}
				}
				break;
			}
		}
	}
	
    private void respawnInSpectatorMode(Player p) {
		
    	BukkitScheduler scheduler = Bukkit.getScheduler();
    	Bedwars main = Bedwars.getMain();
    	Bedwars.getPlayersWithBonusPoints().add(p.getName());
    	
    	scheduler.runTaskAsynchronously(main, new Runnable() {
			
			@Override
			public void run() {
				
				Bedwars.getMySQL().addToPoints(p.getUniqueId().toString(), 10);
			}
		});
    	
		scheduler.runTaskLater(main, new Runnable() {
			
			@Override
			public void run() {
				
				p.spigot().respawn();
				Bedwars.spectate(p);
			}
		}, 20);
	}
}
