package de.tubeplay.bedwars.listener;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import de.tubeplay.bedwars.Bedwars;

public class LevelChangeEvent implements Listener {
	
	private Random random = new Random();
	
	@EventHandler
	public void onLevelChange(FoodLevelChangeEvent event) {
		
		Player p = (Player) event.getEntity();
		
		if (!Bedwars.isGameStarted() || p.getAllowFlight() || random.nextInt(4) != 0 && event.getFoodLevel() < p.getFoodLevel()) {
			
			event.setCancelled(true);
		}
	}
}
