package de.tubeplay.bedwars.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;

import de.tubeplay.bedwars.Bedwars;

public class InteractEntityEvent implements Listener {
	
	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		
		Player p = event.getPlayer();
		Entity entity = event.getRightClicked();
		
		if (entity.getType().equals(EntityType.VILLAGER) && !p.getAllowFlight() && (entity.getCustomName() == null || (entity.getCustomName() != null && entity.getCustomName().contains("§6Mobiler Shop §7- §3")))) {
			
			if (entity.getCustomName() == null )
			event.setCancelled(true);
			Inventory inv = Bukkit.createInventory(p, 18, "§6Shop");
			Bedwars.giveShopItems(inv);
			p.openInventory(inv);
		}
	}
}
