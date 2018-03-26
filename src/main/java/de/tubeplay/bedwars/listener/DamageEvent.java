package de.tubeplay.bedwars.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tubeplay.bedwars.Bedwars;

public class DamageEvent implements Listener {
	
	@EventHandler
	public void onEDamage(EntityDamageEvent event) {
		
		if (event.getEntityType().equals(EntityType.PLAYER)) {
			
			Player p = (Player) event.getEntity();
			DamageCause cause = event.getCause();
			double halfDamage = event.getDamage() / 2;
			
			if (!Bedwars.isGameStarted() || p.getAllowFlight() || Bedwars.isGameStopped()) {
				
				event.setCancelled(true);
				
			} else if (cause.equals(DamageCause.FALL)) {
				
				event.setDamage(halfDamage);
				
			} else if (cause.equals(DamageCause.BLOCK_EXPLOSION) || cause.equals(DamageCause.ENTITY_EXPLOSION)) {
				
				event.setDamage(halfDamage);
			}
			
		} else {
			
			event.setCancelled(true);
		}
	}
}
