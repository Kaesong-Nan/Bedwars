package de.tubeplay.bedwars.listener;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class CloseEvent implements Listener {
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		
		Inventory inv = event.getInventory();
		
		if (inv.getType().equals(InventoryType.MERCHANT)) {
			
			Villager villager = (Villager) inv.getHolder();
			villager.remove();
		}
	}
}
