package de.tubeplay.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class IronSpawner {

    public IronSpawner(Location... locs) {
		
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Bedwars.getMain(), new Runnable() {
			
        	@Override
        	public void run() {
				
        		ItemStack item = new ItemStack(Material.IRON_INGOT);
        		
        		for (Location loc : locs) {
        			
            		loc.getWorld().dropItem(loc, item);
        		}
        	}
        }, 0, 200);
        
	}
}
