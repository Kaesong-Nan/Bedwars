package de.tubeplay.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

public class BurnEvent implements Listener {
	
	@EventHandler
	public void onBurn(BlockBurnEvent event) {
		
		event.setCancelled(true);
	}
}
