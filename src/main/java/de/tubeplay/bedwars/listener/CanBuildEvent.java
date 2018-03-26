package de.tubeplay.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCanBuildEvent;

public class CanBuildEvent implements Listener {
	
	@EventHandler
	public void canBuild(BlockCanBuildEvent event) {
		
		event.setBuildable(true);
	}
}
