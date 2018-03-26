package de.tubeplay.bedwars.listener;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import de.tubeplay.bedwars.Bedwars;

public class PickupItemEvent implements Listener {
	
	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent event) {
		
		Player p = event.getPlayer();
		ItemStack stack = event.getItem().getItemStack();
		Material material = stack.getType();
		String name = p.getDisplayName();
		
		if ((!Bedwars.isGameStarted() || p.getAllowFlight()) && !Bedwars.getBuildModePlayers().contains(p.getName())) {
			
			event.setCancelled(true);
			
		} else if (material.equals(Material.STAINED_CLAY)) {
			
			int amount = stack.getAmount();
			event.getItem().remove();
			event.setCancelled(true);
			p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
			ItemStack item = null;
			
			if (name.contains("§4")) {
				
				item = new ItemStack(Material.STAINED_CLAY, amount, (byte) 14);
				
			} else if (name.contains("§2")) {
				
				item = new ItemStack(Material.STAINED_CLAY, amount, (byte) 13);
				
			} else if (name.contains("§1")) {
				
				item = new ItemStack(Material.STAINED_CLAY, amount, (byte) 11);
				
			} else {
				
				item = new ItemStack(Material.STAINED_CLAY, amount, (byte) 4);
			}
			
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§6Normaler Block");
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
			
		} else if (material.equals(Material.LEATHER_BOOTS) || material.equals(Material.LEATHER_LEGGINGS) || material.equals(Material.LEATHER_CHESTPLATE) || material.equals(Material.LEATHER_HELMET)) {
			
			ItemStack item = event.getItem().getItemStack();
			LeatherArmorMeta leatherMeta = (LeatherArmorMeta) item.getItemMeta();
			
			if (name.contains("§4")) {
				
				leatherMeta.setColor(Color.RED);
				
			} else if (name.contains("§2")) {
				
				leatherMeta.setColor(Color.GREEN);
				
			} else if (name.contains("§1")) {
				
				leatherMeta.setColor(Color.BLUE);
				
			} else {
				
				leatherMeta.setColor(Color.YELLOW);
			}
			item.setItemMeta(leatherMeta);
		}
	}
}
