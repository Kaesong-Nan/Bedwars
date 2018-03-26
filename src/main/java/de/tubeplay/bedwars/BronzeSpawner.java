package de.tubeplay.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BronzeSpawner {
	
	public BronzeSpawner(Location... locs) {
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Bedwars.getMain(), new Runnable() {
			
			@Override
			public void run() {
				
				ItemStack item = new ItemStack(Material.CLAY_BRICK);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("Bronze");
				item.setItemMeta(meta);
				
				for (Location loc : locs) {
					
					loc.getWorld().dropItem(loc, item);
				}
			}
		}, 0, 10);
		
	}
}
