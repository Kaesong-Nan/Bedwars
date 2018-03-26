package de.tubeplay.bedwars.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import de.tubeplay.bedwars.Bedwars;

public class LoginEvent implements Listener {
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		
		Player p = event.getPlayer();
		
		if (Bukkit.hasWhitelist() && !Bukkit.getWhitelistedPlayers().contains(p) || p.isBanned()) {
			
			return;
		}
		
		if (Bukkit.getOnlinePlayers().size() == 16 && !Bedwars.isGameStarted()) {
			
			boolean canJoin = false;
			
			if (p.hasPermission("tubeplay.joinLevel10")) {
				
				if (kickBelow9()) {
					
					canJoin = true;
					
				} else {
					
		            for (Player op : Bukkit.getOnlinePlayers()) {
						
						if (op.hasPermission("tubeplay.joinLevel9")) {
							
							op.kickPlayer("Du wurdest gekickt, um einer höherrangigen Person Platz zu machen!");
							canJoin = true;
							break;
						}
					}
				}
				
			} else if (p.hasPermission("tubeplay.joinLevel9")) {
				
                canJoin = kickBelow9();
				
			} else if (p.hasPermission("tubeplay.joinLevel8")) {
                
                canJoin = kickBelow8();
				
			} else if (p.hasPermission("tubeplay.joinLevel7")) {
				
				canJoin = kickBelow7();
				
			} else if (p.hasPermission("tubeplay.joinLevel6")) {
				
				canJoin = kickBelow6();
				
			} else if (p.hasPermission("tubeplay.joinLevel5")) {
				
				canJoin = kickBelow5();
			}
			
			if (canJoin) {
				
				event.allow();
			}
			
		} else {
			
			event.allow();
		}
	}
	
	private boolean kickBelow5() {
		
		for (Player op : Bukkit.getOnlinePlayers()) {
			
			if (!(op.hasPermission("tubeplay.joinLevel10") || op.hasPermission("tubeplay.joinLevel9") || op.hasPermission("tubeplay.joinLevel8") || op.hasPermission("tubeplay.joinLevel7") || op.hasPermission("tubeplay.joinLevel6") || op.hasPermission("tubeplay.joinLevel5"))) {
				
				op.kickPlayer("Du wurdest gekickt, um einer höherrangigen Person Platz zu machen!");
				return true;
			}
		}
		return false;
	}
	
	private boolean kickBelow6() {
		
		if (kickBelow5()) {
			
			return true;
			
		} else {
			
			for (Player op : Bukkit.getOnlinePlayers()) {
				
				if (op.hasPermission("tubeplay.joinLevel5")) {
					
					op.kickPlayer("Du wurdest gekickt, um einer höherrangigen Person Platz zu machen!");
					return true;
				}
			}
			return false;
		}
	}
	
	private boolean kickBelow7() {
		
		if (kickBelow6()) {
			
			return true;
			
		} else {
			
            for (Player op : Bukkit.getOnlinePlayers()) {
				
				if (op.hasPermission("tubeplay.joinLevel6")) {
					
					op.kickPlayer("Du wurdest gekickt, um einer höherrangigen Person Platz zu machen!");
					return true;
				}
			}
			return false;
		}
	}
	
	private boolean kickBelow8() {
		
		if (kickBelow7()) {
			
			return true;
			
		} else {
			
            for (Player op : Bukkit.getOnlinePlayers()) {
				
				if (op.hasPermission("tubeplay.joinLevel7")) {
					
					op.kickPlayer("Du wurdest gekickt, um einer höherrangigen Person Platz zu machen!");
					return true;
				}
			}
			return false;
		}
	}
	
	private boolean kickBelow9() {
		
		if (kickBelow8()) {
			
			return true;
			
		} else {
			
            for (Player op : Bukkit.getOnlinePlayers()) {
				
				if (op.hasPermission("tubeplay.joinLevel8")) {
					
					op.kickPlayer("Du wurdest gekickt, um einer höherrangigen Person Platz zu machen!");
					return true;
				}
			}
			return false;
		}
	}
}