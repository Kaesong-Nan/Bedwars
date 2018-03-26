package de.tubeplay.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class ItemEvent implements Listener {
	
	@EventHandler
	public void OnCraft(CraftItemEvent event) {
		
		event.setCancelled(true);
	}
}
