package de.tubeplay.bedwars.listener;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import de.tubeplay.bedwars.Bedwars;

public class PlaceEvent implements Listener {
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		
		Block block = event.getBlock();
		Material type = block.getType();
		Player p = event.getPlayer();
		String name = p.getDisplayName();
		
		if ((!Bedwars.isGameStarted() || p.getAllowFlight()) && !Bedwars.getBuildModePlayers().contains(p.getName()) || type.equals(Material.SKULL)) {
			
			event.setCancelled(true);
			
		} else if (type.equals(Material.CARPET)) {
			
			HashMap<Block, Byte> redTeamTraps = Bedwars.getRedTeamTraps();
			HashMap<Block, Byte> greenTeamTraps = Bedwars.getGreenTeamTraps();
			HashMap<Block, Byte> blueTeamTraps = Bedwars.getBlueTeamTraps();
			HashMap<Block, Byte> yellowTeamTraps = Bedwars.getYellowTeamTraps();
			
			if (redTeamTraps.containsKey(block) || greenTeamTraps.containsKey(block) || blueTeamTraps.containsKey(block) || yellowTeamTraps.containsKey(block)) {
				
				event.setCancelled(true);
				p.sendMessage("§6[§3Bedwars§6] §cAn diesem Ort befindet sich bereits eine Falle!");
				
			} else {
				
				@SuppressWarnings("deprecation")
				byte data = block.getData();
				
				if (name.contains("§4")) {
					
					redTeamTraps.put(block, data);
					
				} else if (name.contains("§2")) {
					
					greenTeamTraps.put(block, data);
					
				} else if (name.contains("§1")) {
					
					blueTeamTraps.put(block, data);
					
				} else if (name.contains("§e")) {
					
					yellowTeamTraps.put(block, data);
				}
				block.setType(Material.AIR);
				p.sendMessage("§6[§3Bedwars§6] §aDie Falle ist nun unsichtbar!");
			}
		}
	}
}
