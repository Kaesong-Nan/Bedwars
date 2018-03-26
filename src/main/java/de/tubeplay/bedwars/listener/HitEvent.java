package de.tubeplay.bedwars.listener;

import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitScheduler;

import de.tubeplay.bedwars.Bedwars;

public class HitEvent implements Listener {
	
	private Vector<Integer> untilExplode = new Vector<Integer>();
	private int countExplosion;
	
	@EventHandler
	public void onHit(ProjectileHitEvent event) {
		
		Entity entity = event.getEntity();
		
		if (entity instanceof Snowball) {
			
			Location loc = entity.getLocation();
			BukkitScheduler scheduler = Bukkit.getScheduler();
			untilExplode.add(3);
			int position = untilExplode.size() - 1;
			
			countExplosion = scheduler.scheduleSyncRepeatingTask(Bedwars.getMain(), new Runnable() {
				
				@Override
				public void run() {
					
					int untilExplodeThisProjectile = untilExplode.get(position);
					World world = loc.getWorld();
					untilExplode.set(position, untilExplodeThisProjectile - 1);
					
					if (untilExplodeThisProjectile == 0) {
						
						boolean allExploded = true;
						loc.getWorld().createExplosion(loc, 10);
						
						for (int untilExplodeProjectile : untilExplode) {
							
							if (untilExplodeProjectile > 0) {
								
								allExploded = false;
								break;
							}
						}
						
						if (allExploded) {
							
							scheduler.cancelTask(countExplosion);
						}
						
					} else if (untilExplodeThisProjectile > 0) {
						
						world.spigot().playEffect(loc, Effect.LARGE_SMOKE, 12, 0, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
						world.spigot().playEffect(loc, Effect.LARGE_SMOKE, 12, 1, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
						world.spigot().playEffect(loc, Effect.LARGE_SMOKE, 12, 2, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
						world.spigot().playEffect(loc, Effect.LARGE_SMOKE, 12, 3, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
						world.spigot().playEffect(loc, Effect.LARGE_SMOKE, 12, 4, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
						world.spigot().playEffect(loc, Effect.LARGE_SMOKE, 12, 5, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
						world.spigot().playEffect(loc, Effect.LARGE_SMOKE, 12, 6, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
						world.spigot().playEffect(loc, Effect.LARGE_SMOKE, 12, 7, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
						world.spigot().playEffect(loc, Effect.LARGE_SMOKE, 12, 8, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
						
						for (Player op : Bukkit.getOnlinePlayers()) {
							
							Location opLoc = op.getLocation();
							
							if (Math.abs(opLoc.getX() - loc.getX()) < 26 && Math.abs(opLoc.getZ() - loc.getZ()) < 26) {
								
								op.playSound(opLoc, Sound.BLAZE_HIT, 2, 2);
							}
						}
					}
				}
			}, 20, 20);
		}
	}
}
