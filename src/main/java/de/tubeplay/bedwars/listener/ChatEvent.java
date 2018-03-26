package de.tubeplay.bedwars.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.tubeplay.bedwars.Bedwars;

public class ChatEvent implements Listener {
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		
		Player p = event.getPlayer();
		String message = event.getMessage();
		String playerName = p.getDisplayName();
		
		if (Bedwars.isGameStarted()) {
			
			String replacedMessage = message.replaceFirst("@all", "");
			replacedMessage = replacedMessage.replaceFirst("@a", "");
			event.setFormat("§7[§3@all§7] §r<" +  playerName + "§r> " + replacedMessage);
			
			if (p.getAllowFlight()) {
				
				for (Player op : Bukkit.getOnlinePlayers()) {
					
					if (op.getAllowFlight()) {
						
						op.sendMessage("§8<" + playerName +"§8> §r" + message);
					}
				}
				event.setCancelled(true);
				
			} else if (!(message.contains("@all") || message.contains("@a"))) {
				
				String colorCode;
				event.setCancelled(true);
				
				if (playerName.contains("§4")) {
					
					colorCode = "§4";
					
				} else if (playerName.contains("§2")) {
					
					colorCode = "§2";
					
				} else if (playerName.contains("§1")) {
					
					colorCode = "§1";
					
				} else {
					
					colorCode = "§e";
				}
				
	            for (Player op : Bukkit.getOnlinePlayers()) {
					
					if (op.getDisplayName().contains(colorCode)) {
						
						op.sendMessage("§7[Team] §r<" + playerName +"§r> " + message);
					}
				}
			}
			
		} else {
			
			event.setFormat("<" +  playerName + "§r> " + message);
		}
	}

}
