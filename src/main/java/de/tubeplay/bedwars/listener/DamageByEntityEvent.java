package de.tubeplay.bedwars.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.tubeplay.bedwars.Bedwars;

public class DamageByEntityEvent implements Listener {
	
	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent event) {
		
		Entity damager = event.getDamager();
		
		if (damager.getType().equals(EntityType.PLAYER)) {
			
			Player pDamager = (Player) damager;
			
			if (pDamager.getAllowFlight() && !Bedwars.getBuildModePlayers().contains(pDamager.getName())) {
				
				event.setCancelled(true);
			}
			
		}
	}
}
