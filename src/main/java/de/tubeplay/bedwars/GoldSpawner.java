package de.tubeplay.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GoldSpawner {
	
	private short tillNextDrop = 0;
	
    public GoldSpawner(Location... locs) {
    	
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Bedwars.getMain(), new Runnable() {
			
        	@Override
        	public void run() {
        		
        		if (!Bedwars.isGameStopped()) {
        			
            		for (Player op : Bukkit.getOnlinePlayers()) {
            			
            			op.setLevel(tillNextDrop);
            			double progress = tillNextDrop * 0.01052631578947368421052631578947;
            			
            			if (progress < 1) {
            				
                			op.setExp((float) progress);
                			
            			} else {
            				
            				op.setExp(1);
            			}
            		}
            		
            		if (tillNextDrop == 0) {
            			
                		ItemStack item = new ItemStack(Material.GOLD_INGOT);
                		tillNextDrop = 95;
                		
                		for (Location loc : locs) {
                			
                			loc.getWorld().dropItem(loc, item);
                		}
                		
            		} else {
            			
                		tillNextDrop--;
            		}
        		}
        	}
        }, 20, 20);
        
	}
}
