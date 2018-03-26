package de.tubeplay.bedwars.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import de.tubeplay.bedwars.Bedwars;

public class BreakEvent implements Listener {
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		
		Block block = event.getBlock();
		Material type = block.getType();
		
		if (!(type.equals(Material.STAINED_CLAY) || type.equals(Material.ENDER_STONE) || type.equals(Material.CHEST) || type.equals(Material.ENDER_CHEST) || type.equals(Material.WEB)) && !Bedwars.getBuildModePlayers().contains(event.getPlayer().getName())) {
			
			event.setCancelled(true);
		}
		
		if (block.getType().equals(Material.BED_BLOCK)) {
			
			Bedwars.manageBedDestroy(block, event.getPlayer());
			
		} else if (block.getType().equals(Material.WEB)) {
			
			block.getDrops().clear();
		}
	}
}
