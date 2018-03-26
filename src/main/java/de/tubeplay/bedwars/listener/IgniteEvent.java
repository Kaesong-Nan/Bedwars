package de.tubeplay.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

public class IgniteEvent implements Listener {
	
	@EventHandler
	public void onIgnite(BlockIgniteEvent event) {
		
		if (event.getCause() == IgniteCause.SPREAD) {
			
			event.setCancelled(true);
		}
	}
}
