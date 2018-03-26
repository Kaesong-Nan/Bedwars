package de.tubeplay.bedwars.listener;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.util.Vector;

public class ExplodeEvent implements Listener {
	
	private Random random = new Random();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void explode(BlockExplodeEvent event) {
		
		Location loc = event.getBlock().getLocation();
		event.setCancelled(true);
		
		for (Player op : Bukkit.getOnlinePlayers()) {
			
			Location opLoc = op.getLocation();
			
			if (Math.abs(opLoc.getX() - loc.getX()) < 26 && Math.abs(opLoc.getZ() - loc.getZ()) < 26) {
				
				op.playSound(opLoc, Sound.EXPLODE, 2, 2);
			}
		}
		
		for (Block block : event.blockList()) {
			
			Material type = block.getType();
			
			if (type.equals(Material.STAINED_CLAY) || type.equals(Material.ENDER_STONE) || type.equals(Material.CHEST) || type.equals(Material.WEB)) {
				
				int randomInt = random.nextInt(3);
				
				if (randomInt == 0) {
					
					FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(block.getLocation(), type, block.getData());
					block.setType(Material.AIR);
			        float x = (float) -1 + (float) (Math.random() * ((1 - -1) + 1));
			        float y = (float) -5 + (float)(Math.random() * ((5 - -5) + 1));
			        float z = (float) -0.3 + (float)(Math.random() * ((0.3 - -0.3) + 1));
			        fallingBlock.setVelocity(new Vector(x, y, z));
					
				} else if (randomInt == 1) {
					
					block.breakNaturally();
					
				} else {
					
					block.setType(Material.AIR);
				}
			}
		}
	}
}
