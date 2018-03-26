package de.tubeplay.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import de.tubeplay.bedwars.Bedwars;

public class DropItemEvent implements Listener {
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		
		if ((!Bedwars.isGameStarted() || event.getPlayer().getAllowFlight()) && !Bedwars.getBuildModePlayers().contains(event.getPlayer().getName())) {
			
			event.setCancelled(true);
		}
	}
}
