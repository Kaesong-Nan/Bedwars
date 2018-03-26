package de.tubeplay.bedwars.listener;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import de.tubeplay.bedwars.Bedwars;

public class MoveEvent implements Listener {
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		
		Player p = event.getPlayer();
		Location playerLoc = p.getLocation();
		Block block = playerLoc.getBlock();
		World world = Bukkit.getWorlds().get(0);
		String name = p.getDisplayName();
		boolean gameStarted = Bedwars.isGameStarted();
		
		if (!gameStarted && block.getType().equals(Material.IRON_PLATE)) {
			
			p.playSound(playerLoc, Sound.ENDERDRAGON_WINGS, 3, 2);
			Vector velocity = playerLoc.getDirection().multiply(45);
			velocity.setY(2);
			p.setVelocity(velocity);
			
		} else if (playerLoc.getY() < -20) {
			
			if (gameStarted) {
				
				if (!p.isDead()) {
					
					p.setHealth(0);
				}
				
			} else {
				
				Location loc = new Location(world, 28, 118, -7, 90, 0);
				p.teleport(loc);
			}
			
		} else if (playerLoc.getWorld() == world && playerLoc.getBlockX() == -28 && playerLoc.getBlockZ() == -12) {
			
			Location teleporter = new Location(world, -51, 117, 22, 32, 0);
			p.teleport(teleporter);
			
		} else {
			
			HashMap<Block, Byte> redTeamTraps = Bedwars.getRedTeamTraps();
			HashMap<Block, Byte> greenTeamTraps = Bedwars.getGreenTeamTraps();
			HashMap<Block, Byte> blueTeamTraps = Bedwars.getBlueTeamTraps();
			HashMap<Block, Byte> yellowTeamTraps = Bedwars.getYellowTeamTraps();
			
			if (!name.contains("§4") && redTeamTraps.containsKey(block)) {
				
				trap(p, block, "§4", redTeamTraps.get(block));
				redTeamTraps.remove(block);
			
			} else if (!name.contains("§2") && greenTeamTraps.containsKey(block)) {
				
				trap(p, block, "§2", greenTeamTraps.get(block));
				greenTeamTraps.remove(block);
				
			} else if (!name.contains("§1") && blueTeamTraps.containsKey(block)) {
				
				trap(p, block, "§1", blueTeamTraps.get(block));
				blueTeamTraps.remove(block);
				
			} else if (!name.contains("§e") && yellowTeamTraps.containsKey(block)) {
				
				trap(p, block, "§e", yellowTeamTraps.get(block));
				yellowTeamTraps.remove(block);
			}
		}
	}
	
	private void trap(Player p, Block block, String team, byte data) {
		
		PotionEffect potion = null;
		
		switch (data) {
		
		case 15:
			
			potion = new PotionEffect(PotionEffectType.BLINDNESS, 160, 0);
			break;
			
		case 14:
			
			potion = new PotionEffect(PotionEffectType.WEAKNESS, 160, 0);
			break;
			
		case 8:
			
			for (Player op : Bukkit.getOnlinePlayers()) {
				
				if (op.getDisplayName().contains(team)) {
					
					op.sendMessage("§4§o§l§nWARNUNG: §r§cJemand ist über eine Benachrichtigungs-Falle deines Teams gelaufen!");
					op.playSound(op.getLocation(), Sound.GLASS, 1, 3);
				}
			}
			break;
			
		case 5:
			
			potion = new PotionEffect(PotionEffectType.CONFUSION, 200, 0);
			break;
			
		case 4:
			
			potion = new PotionEffect(PotionEffectType.SLOW, 160, 0);
			break;
			
		default:
			
			potion = new PotionEffect(PotionEffectType.HUNGER, 160, 0);
			break;
		}
		
		if (potion != null) {
			
			p.removePotionEffect(potion.getType());
			p.addPotionEffect(potion);
		}
		block.setType(Material.AIR);
		p.sendMessage("§6[§3Bedwars§6] §cDu bist über eine Falle gelaufen!");
		p.playSound(p.getLocation(), Sound.GLASS, 1, 3);
	}
}
